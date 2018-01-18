import com.typesafe.sbt.SbtPgp.autoImportImpl.PgpKeys._
import Dependencies._
import UdashBuild._

name := "udash"
cancelable in Global := true

inThisBuild(Seq(
  version := "0.6.0-RC2",
  scalaVersion := versionOfScala,
  crossScalaVersions := Seq("2.11.11", versionOfScala),
  organization := "io.udash",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
    "-language:experimental.macros",
    "-Xfuture",
    "-Xfatal-warnings",
    CrossVersion.partialVersion(scalaVersion.value).collect {
      // WORKAROUND https://github.com/scala/scala/pull/5402
      case (2, 12) => "-Xlint:-unused,_"
    }.getOrElse("-Xlint:_"),
  ),
  jsTestEnv := new org.scalajs.jsenv.selenium.SeleniumJSEnv({
    import org.openqa.selenium.chrome.ChromeOptions
    val chrome = org.openqa.selenium.remote.DesiredCapabilities.chrome()
    val chromeOptions = new ChromeOptions()
    chromeOptions.addArguments("--headless", "--disable-gpu")
    chrome.setCapability(ChromeOptions.CAPABILITY, chromeOptions)
    chrome
  })
))

val forIdeaImport = System.getProperty("idea.managed", "false").toBoolean && System.getProperty("idea.runid") == null

// Deployment configuration
val deploymentConfiguration = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },

  pomExtra := {
    <url>https://github.com/UdashFramework/udash-core</url>
      <licenses>
        <license>
          <name>Apache v.2 License</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:UdashFramework/udash-core.git</url>
        <connection>scm:git@github.com:UdashFramework/udash-core.git</connection>
      </scm>
      <developers>
        <developer>
          <id>avsystem</id>
          <name>AVSystem</name>
          <url>http://www.avsystem.com/</url>
        </developer>
      </developers>
  }
)

val commonSettings = Seq(
  moduleName := "udash-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value,
  libraryDependencies ++= commonTestDeps.value,
) ++ deploymentConfiguration

val commonJSSettings = Seq(
  emitSourceMaps in Compile := true,
  scalaJSStage in Test := FastOptStage,
  jsEnv in Test := jsTestEnv.value,
  scalacOptions += {
    val localDir = (baseDirectory in ThisBuild).value.toURI.toString
    val githubDir = "https://raw.githubusercontent.com/UdashFramework/udash-core"
    s"-P:scalajs:mapSourceURI:$localDir->$githubDir/v${version.value}/"
  },
  parallelExecution in Test := false,
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
)

val noPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  publishM2 := {},
  publishSigned := {},
  publishLocalSigned := {},
  doc := (target in doc).value,
)

def mkSourceDirs(base: File, scalaBinary: String, conf: String): Seq[File] = Seq(
  base / "src" / conf / "scala",
  base / "src" / conf / s"scala-$scalaBinary",
  base / "src" / conf / "java"
)

def sourceDirsSettings(baseMapper: File => File) = Seq(
  unmanagedSourceDirectories in Compile ++=
    mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "main"),
  unmanagedSourceDirectories in Test ++=
    mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "test"),
)

lazy val udash = project.in(file("."))
  .aggregate(
    `core-macros`, `core-shared-JS`, `core-shared`, `core-frontend`,
    `rpc-shared-JS`, `rpc-shared`, `rpc-frontend`, `rpc-backend`,
    `rest-macros`, `rest-shared-JS`, `rest-shared`, `rest-backend`,
    `i18n-shared-JS`, `i18n-shared`, `i18n-frontend`, `i18n-backend`,
    `auth-shared-JS`, `auth-shared`, `auth-frontend`,
    `css-macros`, `css-shared-JS`, `css-shared`, `css-frontend`, `css-backend`,
    `bootstrap`, `charts`
  )
  .settings(noPublishSettings:_*)

lazy val `core-macros` = project.in(file("core/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
  )

lazy val `core-shared` = project.in(file("core/shared"))
  .dependsOn(`core-macros`)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= coreCrossDeps.value,
    libraryDependencies ++= coreCrossJVMDeps.value,
  )

lazy val `core-shared-JS` = project.in(`core-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-macros`)
  .configure(p => if (forIdeaImport) p.dependsOn(`core-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `core-shared`).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= coreCrossDeps.value,
  )

lazy val `core-frontend` = project.in(file("core/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    emitSourceMaps in Compile := true,
    libraryDependencies ++= coreFrontendDeps.value,
    publishedJS := Def.taskDyn {
      if (isSnapshot.value) Def.task((fastOptJS in Compile).value) else Def.task((fullOptJS in Compile).value)
    }.value,
    publishedJSDependencies := Def.taskDyn {
      if (isSnapshot.value) Def.task((packageJSDependencies in Compile).value) else Def.task((packageMinifiedJSDependencies in Compile).value)
    }.value
  )

lazy val `rpc-shared` = project.in(file("rpc/shared"))
  .dependsOn(`core-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= rpcCrossTestDeps.value,
    libraryDependencies ++= rpcSharedJVMDeps.value,
  )

lazy val `rpc-shared-JS` = project.in(`rpc-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`rpc-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `rpc-shared`).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= rpcCrossTestDeps.value,
  )

lazy val `rpc-backend` = project.in(file("rpc/backend"))
  .dependsOn(`rpc-shared` % CompileAndTest)
  .settings(commonSettings: _*).settings(
  libraryDependencies ++= rpcBackendDeps.value
)

lazy val `rpc-frontend` = project.in(file("rpc/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    jsDependencies ++= rpcFrontendJsDeps.value
  )

lazy val `rest-macros` = project.in(file("rest/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion
    )
  )

lazy val `rest-shared` = project.in(file("rest/shared"))
  .dependsOn(`rest-macros`, `rpc-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= restCrossDeps.value,
  )

lazy val `rest-shared-JS` = project.in(`rest-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rest-macros`, `rpc-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`rest-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `rest-shared`).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= restCrossDeps.value,
  )

lazy val `rest-backend` = project.in(file("rest/backend"))
  .dependsOn(`rest-shared` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= restBackendDeps.value
  )

lazy val `i18n-shared` = project.in(file("i18n/shared"))
  .dependsOn(`rpc-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),
  )

lazy val `i18n-shared-JS` = project.in(`i18n-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`i18n-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `i18n-shared`).value,
    sourceDirsSettings(_.getParentFile),
  )

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared` % CompileAndTest, `rpc-backend` % CompileAndTest)
  .settings(commonSettings: _*)

lazy val `i18n-frontend` = project.in(file("i18n/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)

lazy val `auth-shared` = project.in(file("auth/shared"))
  .dependsOn(`rpc-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),
  )

lazy val `auth-shared-JS` = project.in(`auth-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`i18n-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `auth-shared`).value,
    sourceDirsSettings(_.getParentFile),
  )

lazy val `auth-frontend` = project.in(file("auth/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`auth-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)

lazy val `css-macros` = project.in(file("css/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
    libraryDependencies ++= cssMacroDeps.value
  )

lazy val `css-shared` = project.in(file("css/shared"))
  .dependsOn(`css-macros`, `core-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),
    libraryDependencies ++= cssMacroDeps.value,
  )

lazy val `css-shared-JS` = project.in(`css-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`css-macros`, `core-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`css-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    name := (name in `css-shared`).value,
    sourceDirsSettings(_.getParentFile),
    libraryDependencies ++= cssMacroDeps.value,
  )

lazy val `css-backend` = project.in(file("css/backend"))
  .dependsOn(`css-shared` % CompileAndTest, `core-shared` % TestAll)
  .settings(commonSettings: _*)

lazy val `css-frontend` = project.in(file("css/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`css-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    emitSourceMaps in Compile := true,
    libraryDependencies ++= cssFrontendDeps.value
  )

lazy val `bootstrap` = project.in(file("bootstrap/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend` % CompileAndTest, `css-frontend` % CompileAndTest, `i18n-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    libraryDependencies ++= bootstrapFrontendDeps.value,
    jsDependencies ++= bootstrapFrontendJsDeps.value
  )

lazy val `charts` = project.in(file("charts/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    libraryDependencies ++= chartsFrontendDeps.value
  )

lazy val `benchmarks-frontend` = project.in(file("benchmarks/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend`, `i18n-frontend`, `css-frontend`)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= benchmarksFrontendDeps.value,
    scalaJSUseMainModuleInitializer in Compile := true,
  )
