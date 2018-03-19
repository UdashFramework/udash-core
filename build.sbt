import com.typesafe.sbt.SbtPgp.autoImportImpl.PgpKeys._
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalajs.jsenv.selenium.SeleniumJSEnv

name := "udash"

inThisBuild(Seq(
  version := "0.7.0-M2",
  scalaVersion := Dependencies.versionOfScala,
  crossScalaVersions := Seq("2.11.11", Dependencies.versionOfScala),
  organization := "io.udash",
  cancelable := true,
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
    "-language:postfixOps",
    "-language:experimental.macros",
    "-Xfuture",
    "-Xfatal-warnings",
    "-Xlint:_",
  ),
  scalacOptions ++= {
    if (CrossVersion.partialVersion((udash / scalaVersion).value).contains((2, 12))) Seq("-Ywarn-unused:_,-explicits,-implicits")
    else Seq.empty
  },
))

val forIdeaImport = System.getProperty("idea.managed", "false").toBoolean && System.getProperty("idea.runid") == null
val CompileAndTest = "test->test;compile->compile"
val TestAll = "test->test"

// Settings for JS tests run in browser
val browserCapabilities: DesiredCapabilities = {
  // requires ChromeDriver: https://sites.google.com/a/chromium.org/chromedriver/
  val capabilities = DesiredCapabilities.chrome()
  capabilities.setCapability(ChromeOptions.CAPABILITY, {
    val options = new ChromeOptions()
    options.addArguments("--headless", "--disable-gpu")
    options
  })
  capabilities
}

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
  libraryDependencies ++= Dependencies.compilerPlugins.value,
  libraryDependencies ++= Dependencies.commonDeps.value,
  libraryDependencies ++= Dependencies.commonTestDeps.value,
) ++ deploymentConfiguration

val commonJSSettings = Seq(
  Compile / emitSourceMaps := true,
  Test / parallelExecution := false,
  Test / scalaJSStage := FastOptStage,
  Test / jsEnv := new SeleniumJSEnv(browserCapabilities),
  scalacOptions += {
    val localDir = (ThisBuild / baseDirectory).value.toURI.toString
    val githubDir = "https://raw.githubusercontent.com/UdashFramework/udash-core"
    s"-P:scalajs:mapSourceURI:$localDir->$githubDir/v${version.value}/"
  },
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
)

val noPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  publishM2 := {},
  publishSigned := {},
  publishLocalSigned := {},
  doc := (doc / target).value,
)

def mkSourceDirs(base: File, scalaBinary: String, conf: String): Seq[File] = Seq(
  base / "src" / conf / "scala",
  base / "src" / conf / s"scala-$scalaBinary",
  base / "src" / conf / "java"
)

def sourceDirsSettings(baseMapper: File => File) = Seq(
  Compile / unmanagedSourceDirectories ++=
    mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "main"),
  Test / unmanagedSourceDirectories ++=
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
  .settings(noPublishSettings)

lazy val `core-macros` = project.in(file("core/macros"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.coreMacroDeps.value,
  )

lazy val `core-shared` = project.in(file("core/shared"))
  .dependsOn(`core-macros`)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= Dependencies.coreCrossDeps.value,
    libraryDependencies ++= Dependencies.coreCrossJVMDeps.value,
  )

lazy val `core-shared-JS` = project.in(`core-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-macros`)
  .configure(p => if (forIdeaImport) p.dependsOn(`core-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,

    name := (`core-shared` / name).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= Dependencies.coreCrossDeps.value,
  )

lazy val `core-frontend` = project.in(file("core/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings,

    libraryDependencies ++= Dependencies.coreFrontendDeps.value
  )

lazy val `rpc-shared` = project.in(file("rpc/shared"))
  .dependsOn(`core-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= Dependencies.rpcCrossTestDeps.value,
    libraryDependencies ++= Dependencies.rpcSharedJVMDeps.value,
  )

lazy val `rpc-shared-JS` = project.in(`rpc-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`rpc-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,

    name := (`rpc-shared` / name).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= Dependencies.rpcCrossTestDeps.value,
  )

lazy val `rpc-backend` = project.in(file("rpc/backend"))
  .dependsOn(`rpc-shared` % CompileAndTest)
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.rpcBackendDeps.value
  )

lazy val `rpc-frontend` = project.in(file("rpc/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings,

    jsDependencies ++= Dependencies.rpcFrontendJsDeps.value
  )

lazy val `rest-macros` = project.in(file("rest/macros"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.restMacroDeps.value
  )

lazy val `rest-shared` = project.in(file("rest/shared"))
  .dependsOn(`rest-macros`, `rpc-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= Dependencies.restCrossDeps.value,
  )

lazy val `rest-shared-JS` = project.in(`rest-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`rest-macros`, `rpc-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`rest-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,

    name := (`rest-shared` / name).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= Dependencies.restCrossDeps.value,
  )

lazy val `rest-backend` = project.in(file("rest/backend"))
  .dependsOn(`rest-shared` % CompileAndTest)
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.restBackendDeps.value
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

    name := (`i18n-shared` / name).value,
    sourceDirsSettings(_.getParentFile),
  )

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared` % CompileAndTest, `rpc-backend` % CompileAndTest)
  .settings(commonSettings)

lazy val `i18n-frontend` = project.in(file("i18n/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings
  )

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

    name := (`auth-shared` / name).value,
    sourceDirsSettings(_.getParentFile),
  )

lazy val `auth-frontend` = project.in(file("auth/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`auth-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings
  )

lazy val `css-macros` = project.in(file("css/macros"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.cssMacroDeps.value
  )

lazy val `css-shared` = project.in(file("css/shared"))
  .dependsOn(`css-macros`, `core-shared` % CompileAndTest)
  .settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),

    libraryDependencies ++= Dependencies.cssMacroDeps.value,
  )

lazy val `css-shared-JS` = project.in(`css-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`css-macros`, `core-shared-JS` % CompileAndTest)
  .configure(p => if (forIdeaImport) p.dependsOn(`css-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,

    name := (`css-shared` / name).value,
    sourceDirsSettings(_.getParentFile),

    libraryDependencies ++= Dependencies.cssMacroDeps.value,
  )

lazy val `css-backend` = project.in(file("css/backend"))
  .dependsOn(`css-shared` % CompileAndTest, `core-shared` % TestAll)
  .settings(commonSettings)

lazy val `css-frontend` = project.in(file("css/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`css-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings,
    libraryDependencies ++= Dependencies.cssFrontendDeps.value
  )

lazy val `bootstrap` = project.in(file("bootstrap/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend` % CompileAndTest, `css-frontend` % CompileAndTest, `i18n-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings,

    libraryDependencies ++= Dependencies.bootstrapFrontendDeps.value,
    jsDependencies ++= Dependencies.bootstrapFrontendJsDeps.value
  )

lazy val `charts` = project.in(file("charts/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend` % CompileAndTest)
  .settings(
    commonSettings,
    commonJSSettings,
    libraryDependencies ++= Dependencies.chartsFrontendDeps.value
  )

lazy val `benchmarks-frontend` = project.in(file("benchmarks/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend`, `i18n-frontend`, `css-frontend`)
  .settings(
    commonSettings,
    commonJSSettings,
    noPublishSettings,

    libraryDependencies ++= Dependencies.benchmarksFrontendDeps.value,
    Compile / scalaJSUseMainModuleInitializer := true,
  )
