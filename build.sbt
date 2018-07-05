import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalajs.jsenv.selenium.SeleniumJSEnv

name := "udash"

inThisBuild(Seq(
  version := "0.8.0-SNAPSHOT",
  scalaVersion := Dependencies.versionOfScala,
  crossScalaVersions := Seq("2.11.12", Dependencies.versionOfScala),
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
    if (CrossVersion.partialVersion((udash / scalaVersion).value).contains((2, 12))) Seq(
      "-Ywarn-unused:_,-explicits,-implicits",
      "-Ybackend-parallelism", "4",
      "-Ycache-plugin-class-loader:last-modified",
      "-Ycache-macro-class-loader:last-modified"
    ) else Seq.empty
  },
  resolvers += Resolver.defaultLocal
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

val commonSettings = Seq(
  moduleName := "udash-" + moduleName.value,
  libraryDependencies ++= Dependencies.compilerPlugins.value,
  libraryDependencies ++= Dependencies.commonDeps.value,
  libraryDependencies ++= Dependencies.commonTestDeps.value,
)

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
  .configure(p => if (forIdeaImport) p.dependsOn(`auth-shared`) else p)
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

lazy val `selenium-shared` = project.in(file("selenium/shared"))
  .dependsOn(
    `core-shared` % CompileAndTest, `rpc-shared` % CompileAndTest, `rest-shared` % CompileAndTest,
    `css-shared` % CompileAndTest, `auth-shared` % CompileAndTest, `i18n-shared` % CompileAndTest
  ).settings(
    commonSettings,
  noPublishSettings,
    sourceDirsSettings(_ / ".jvm"),
  )

lazy val `selenium-shared-JS` = project.in(`selenium-shared`.base / ".js")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(
    `core-shared-JS` % CompileAndTest, `rpc-shared-JS` % CompileAndTest, `rest-shared-JS` % CompileAndTest,
    `css-shared-JS` % CompileAndTest, `auth-shared-JS` % CompileAndTest, `i18n-shared-JS` % CompileAndTest
  ).configure(p => if (forIdeaImport) p.dependsOn(`selenium-shared`) else p)
  .settings(
    commonSettings,
    commonJSSettings,
    noPublishSettings,

    name := (`selenium-shared` / name).value,
    sourceDirsSettings(_.getParentFile),
  )

lazy val `selenium-backend` = project.in(file("selenium/backend"))
  .dependsOn(
    `selenium-shared` % CompileAndTest, `rpc-backend` % CompileAndTest, `rest-backend` % CompileAndTest,
    `css-backend` % CompileAndTest, `i18n-backend` % CompileAndTest
  ).settings(
    commonSettings,
    noPublishSettings,

    Test / parallelExecution := false,
    Test / compile := (Test / compile).dependsOn(`selenium-frontend` / compileAndOptimizeStatics).value,

    libraryDependencies ++= Dependencies.seleniumBackendDeps.value,
    libraryDependencies ++= Dependencies.seleniumTestingDeps.value
  )

// Custom SBT tasks
val copyAssets = taskKey[Unit]("Copies all assets to the target directory.")
val cssDir = settingKey[File]("Target for `compileCss` task.")
val compileCss = taskKey[Unit]("Compiles CSS files.")
val compileStatics = taskKey[File](
  "Compiles JavaScript files and copies all assets to the target directory."
)
val compileAndOptimizeStatics = taskKey[File](
  "Compiles and optimizes JavaScript files and copies all assets to the target directory."
)

val seleniumStaticsRoot = "UdashStatics/WebContent"
lazy val `selenium-frontend` = project.in(file("selenium/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(
    `selenium-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest, `rpc-frontend` % CompileAndTest,
    `css-frontend` % CompileAndTest, `auth-frontend` % CompileAndTest, `bootstrap` % CompileAndTest
  ).settings(
    commonSettings,
    commonJSSettings,
    noPublishSettings,

    Compile / emitSourceMaps  := true,
    Compile / scalaJSUseMainModuleInitializer := true,

    Compile / copyAssets := {
      IO.copyDirectory(
        sourceDirectory.value / "main/assets",
        target.value / s"$seleniumStaticsRoot/assets"
      )
      IO.copyFile(
        sourceDirectory.value / "main/assets/index.html",
        target.value / s"$seleniumStaticsRoot/index.html"
      )
    },

    // Compiles JS files without full optimizations
    compileStatics := {
      (Compile / fastOptJS / target).value / "UdashStatics"
    },
    compileStatics := compileStatics.dependsOn(
      Compile / fastOptJS, Compile / copyAssets
    ).value,

    // Compiles JS files with full optimizations
    compileAndOptimizeStatics := {
      (Compile / fullOptJS / target).value / "UdashStatics"
    },
    compileAndOptimizeStatics := compileAndOptimizeStatics.dependsOn(
      Compile / fullOptJS, Compile / copyAssets
    ).value,

    // Target files for Scala.js plugin
    Compile / fastOptJS / artifactPath :=
      (Compile / fastOptJS / target).value /
        seleniumStaticsRoot / "scripts" / "frontend.js",
    Compile / fullOptJS / artifactPath :=
      (Compile / fullOptJS / target).value /
        seleniumStaticsRoot / "scripts" / "frontend.js",
    Compile / packageJSDependencies / artifactPath :=
      (Compile / packageJSDependencies / target).value /
        seleniumStaticsRoot / "scripts" / "frontend-deps.js",
    Compile / packageMinifiedJSDependencies / artifactPath :=
      (Compile / packageMinifiedJSDependencies / target).value /
        seleniumStaticsRoot / "scripts" / "frontend-deps.js"
  )