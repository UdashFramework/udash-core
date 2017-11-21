import Dependencies._
import UdashBuild._

name := "udash"

version in ThisBuild := "0.6.0-starzu-11"
scalaVersion in ThisBuild := "2.11.11"
crossScalaVersions in ThisBuild := Seq("2.11.11", versionOfScala)
organization in ThisBuild := "io.udash"
cancelable in Global := true
scalacOptions in ThisBuild ++= Seq(
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
  }.getOrElse("-Xlint:_")
)

jsTestEnv in ThisBuild := new org.scalajs.jsenv.selenium.SeleniumJSEnv({
  import org.openqa.selenium.chrome.ChromeOptions
  val chrome = org.openqa.selenium.remote.DesiredCapabilities.chrome()
  val chromeOptions = new ChromeOptions()
  chromeOptions.addArguments("--headless", "--disable-gpu")
  chrome.setCapability(ChromeOptions.CAPABILITY, chromeOptions)
  chrome
})

val commonSettings = Seq(
  moduleName := "udash-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value,
  libraryDependencies ++= commonTestDeps.value
)

val commonJSSettings = Seq(
  parallelExecution in Test := false,
  emitSourceMaps in Compile := true,
  scalaJSStage in Test := FastOptStage,
  jsEnv in Test := jsTestEnv.value,
  scalacOptions += {
    val localDir = (baseDirectory in ThisBuild).value.toURI.toString
    val githubDir = "https://raw.githubusercontent.com/UdashFramework/udash-core"
    s"-P:scalajs:mapSourceURI:$localDir->$githubDir/v${version.value}/"
  }
)

val noPublishSettings = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  publishM2 := {},
  doc := (target in doc).value,
)

lazy val udash = project.in(file("."))
  .aggregate(
    `core-macros`, `core-shared-JS`, `core-shared-JVM`, `core-frontend`,
    `rpc-shared-JS`, `rpc-shared-JVM`, `rpc-frontend`, `rpc-backend`,
    `rest-macros`, `rest-shared-JS`, `rest-shared-JVM`, `rest-backend`,
    `i18n-shared-JS`, `i18n-shared-JVM`, `i18n-frontend`, `i18n-backend`,
    `auth-shared-JS`, `auth-shared-JVM`, `auth-frontend`,
    `css-macros`, `css-shared-JS`, `css-shared-JVM`, `css-frontend`, `css-backend`,
    `bootstrap`, `charts`
  )
  .settings(noPublishSettings: _*)

lazy val `core-macros` = project.in(file("core/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
  )

lazy val `core-shared` = crossProject.crossType(CrossType.Pure).in(file("core/shared"))
  .jsConfigure(_.dependsOn(`core-macros`))
  .jvmConfigure(_.dependsOn(`core-macros`))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= coreCrossDeps.value
  )
  .jsSettings(commonJSSettings: _*)

lazy val `core-shared-JVM` = `core-shared`.jvm
lazy val `core-shared-JS` = `core-shared`.js

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

lazy val `rpc-shared` = crossProject.crossType(CrossType.Full).in(file("rpc/shared"))
  .configureCross(_.dependsOn(`core-shared` % CompileAndTest))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= rpcCrossTestDeps.value
  )
  .jsSettings(commonJSSettings: _*)
  .jvmSettings(
    libraryDependencies ++= rpcSharedJVMDeps.value
  )

lazy val `rpc-shared-JVM` = `rpc-shared`.jvm
lazy val `rpc-shared-JS` = `rpc-shared`.js

lazy val `rpc-backend` = project.in(file("rpc/backend"))
  .dependsOn(`rpc-shared-JVM` % CompileAndTest)
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

lazy val `rest-shared` = crossProject.crossType(CrossType.Pure).in(file("rest/shared"))
  .configureCross(_.dependsOn(`rpc-shared` % CompileAndTest))
  .jsConfigure(_.dependsOn(`rest-macros`))
  .jvmConfigure(_.dependsOn(`rest-macros`))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= restCrossDeps.value
  )
  .jsSettings(commonJSSettings: _*)

lazy val `rest-shared-JVM` = `rest-shared`.jvm
lazy val `rest-shared-JS` = `rest-shared`.js

lazy val `rest-backend` = project.in(file("rest/backend"))
  .dependsOn(`rest-shared-JVM` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= restBackendDeps.value
  )

lazy val `i18n-shared` = crossProject.crossType(CrossType.Pure).in(file("i18n/shared"))
  .configureCross(_.dependsOn(`core-shared`, `rpc-shared` % CompileAndTest))
  .settings(commonSettings: _*)
  .jsSettings(commonJSSettings: _*)

lazy val `i18n-shared-JVM` = `i18n-shared`.jvm
lazy val `i18n-shared-JS` = `i18n-shared`.js

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared-JVM` % CompileAndTest, `rpc-backend` % CompileAndTest)
  .settings(commonSettings: _*)

lazy val `i18n-frontend` = project.in(file("i18n/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)

lazy val `auth-shared` = crossProject.crossType(CrossType.Pure).in(file("auth/shared"))
  .configureCross(_.dependsOn(`core-shared`, `rpc-shared` % CompileAndTest))
  .settings(commonSettings: _*)
  .jsSettings(commonJSSettings: _*)

lazy val `auth-shared-JVM` = `auth-shared`.jvm
lazy val `auth-shared-JS` = `auth-shared`.js

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

lazy val `css-shared` = crossProject.crossType(CrossType.Pure).in(file("css/shared"))
  .jsConfigure(_.dependsOn(`css-macros`, `core-shared-JS` % Test))
  .jvmConfigure(_.dependsOn(`css-macros`, `core-shared-JVM` % Test))
  .settings(commonSettings: _*)
  .jsSettings(commonJSSettings: _*)
  .settings(libraryDependencies ++= cssMacroDeps.value)

lazy val `css-shared-JVM` = `css-shared`.jvm
lazy val `css-shared-JS` = `css-shared`.js

lazy val `css-backend` = project.in(file("css/backend"))
  .dependsOn(`css-shared-JVM` % CompileAndTest, `core-shared-JVM` % TestAll)
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
  .dependsOn(`core-frontend` % CompileAndTest, `css-frontend` % CompileAndTest)
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
  .dependsOn(`core-frontend`, `i18n-frontend`)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= benchmarksFrontendDeps.value,
    scalaJSUseMainModuleInitializer in Compile := true,
  )
