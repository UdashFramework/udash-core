import org.openqa.selenium.Capabilities
import org.openqa.selenium.firefox.FirefoxOptions
import org.scalajs.jsenv.selenium.SeleniumJSEnv

name := "udash"

inThisBuild(Seq(
  version := "0.8.0-SNAPSHOT",
  organization := "io.udash",
  cancelable := true,
  resolvers += Resolver.defaultLocal
))

val forIdeaImport = System.getProperty("idea.managed", "false").toBoolean && System.getProperty("idea.runid") == null
val CompileAndTest = "test->test;compile->compile"
val TestAll = "test->test"

// Settings for JS tests run in browser
val browserCapabilities: Capabilities = {
  // requires gecko driver, see https://github.com/mozilla/geckodriver
  new FirefoxOptions().setHeadless(true)
}

val commonSettings = Seq(
  scalaVersion := Dependencies.versionOfScala,
  crossScalaVersions := Seq("2.11.12", Dependencies.versionOfScala),
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
    "-language:postfixOps",
    "-language:higherKinds",
    "-language:experimental.macros",
    "-Xfuture",
    "-Xfatal-warnings",
    "-Xlint:_,-missing-interpolator",
  ),
  scalacOptions ++= {
    if (scalaBinaryVersion.value == "2.12") Seq(
      "-Ywarn-unused:_,-explicits,-implicits",
      "-Ybackend-parallelism", "4",
      "-Ycache-plugin-class-loader:last-modified",
      "-Ycache-macro-class-loader:last-modified"
    ) else Seq.empty
  },
  moduleName := "udash-" + moduleName.value,
  ideBasePackages := Seq("io.udash"),
  ideOutputDirectory in Compile := Some(target.value.getParentFile / "out/production"),
  ideOutputDirectory in Test := Some(target.value.getParentFile / "out/test"),
  libraryDependencies ++= Dependencies.compilerPlugins.value,
  libraryDependencies ++= Dependencies.commonDeps.value,
  libraryDependencies ++= Dependencies.commonTestDeps.value,
  autoAPIMappings := true
)

val commonJSSettings = Seq(
  Compile / emitSourceMaps := true,
  Test / parallelExecution := false,
  Test / scalaJSStage := FastOptStage,
//  Test / jsEnv := new SeleniumJSEnv(browserCapabilities),,
  Test / requireJsDomEnv := true,
  Test / installJsdom / version := "12.0.0",
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

def mkResourceDirs(base: File, conf: String): Seq[File] = Seq(
  base / "src" / conf / "resources"
)

def sourceDirsSettings(baseMapper: File => File) = Seq(
  Compile / unmanagedSourceDirectories ++=
    mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "main"),
  Compile / unmanagedResourceDirectories ++=
    mkResourceDirs(baseMapper(baseDirectory.value), "main"),
  Test / unmanagedSourceDirectories ++=
    mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "test"),
  Test / unmanagedResourceDirectories ++=
    mkResourceDirs(baseMapper(baseDirectory.value), "test"),
)

def jvmProject(proj: Project): Project =
  proj.settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),
  )

def jsProject(proj: Project): Project =
  proj.in(proj.base / ".js")
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      commonSettings,
      commonJSSettings
    )

def jsProjectFor(jsProj: Project, jvmProj: Project): Project =
  jsProj.in(jvmProj.base / ".js")
    .enablePlugins(ScalaJSBundlerPlugin)
    .configure(p => if (forIdeaImport) p.dependsOn(jvmProj) else p)
    .settings(
      commonSettings,
      commonJSSettings,

      moduleName := (jvmProj / moduleName).value,
      sourceDirsSettings(_.getParentFile),
      // workaround for some cross-compilation problems in IntelliJ
      libraryDependencies :=
        (if (forIdeaImport) (jvmProj / libraryDependencies).value else Seq.empty) ++ libraryDependencies.value
    )

lazy val udash = project.in(file("."))
  .aggregate(
    macros,
    utils, `utils-js`,
    core, `core-js`,
    rpc, `rpc-js`,
    rest, `rest-js`,
    i18n, `i18n-js`,
    auth, `auth-js`,
    css, `css-js`,
    bootstrap, bootstrap4, charts
  )
  .settings(noPublishSettings)

lazy val macros = project
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.macroDeps.value,
  )

lazy val utils = jvmProject(project)
  .dependsOn(macros)
  .settings(
    libraryDependencies ++= Dependencies.utilsJvmDeps.value,
  )

lazy val `utils-js` = jsProjectFor(project, utils)
  .dependsOn(macros)
  .settings(
    libraryDependencies ++= Dependencies.utilsSjsDeps.value,
  )

lazy val core = jvmProject(project)
  .dependsOn(utils % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.coreJvmDeps.value,
  )

lazy val `core-js` = jsProjectFor(project, core)
  .dependsOn(`utils-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.coreSjsDeps.value,
  )

lazy val rpc = jvmProject(project)
  .dependsOn(utils % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.rpcJvmDeps.value,
  )

lazy val `rpc-js` = jsProjectFor(project, rpc)
  .dependsOn(`utils-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.rpcSjsDeps.value,
    Compile / npmDependencies ++= Dependencies.rpcJsDeps.value,
  )

lazy val rest = jvmProject(project)
  .dependsOn(utils % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.restJvmDeps.value,
  )

lazy val `rest-js` = jsProjectFor(project, rest)
  .dependsOn(`utils-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.restSjsDeps.value,
  )

lazy val i18n = jvmProject(project)
  .dependsOn(core % CompileAndTest, rpc % CompileAndTest)

lazy val `i18n-js` = jsProjectFor(project, i18n)
  .dependsOn(`core-js` % CompileAndTest, `rpc-js` % CompileAndTest)

lazy val auth = jvmProject(project)
  .dependsOn(core % CompileAndTest, rpc % CompileAndTest)

lazy val `auth-js` = jsProjectFor(project, auth)
  .dependsOn(`core-js` % CompileAndTest, `rpc-js` % CompileAndTest)

lazy val css = jvmProject(project)
  .dependsOn(core % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.cssJvmDeps.value,
  )

lazy val `css-js` = jsProjectFor(project, css)
  .dependsOn(`core-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.cssSjsDeps.value,
  )

lazy val bootstrap = jsProject(project)
  .dependsOn(`core-js` % CompileAndTest, `css-js` % CompileAndTest, `i18n-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.bootstrapSjsDeps.value,
    Compile / npmDependencies ++= Dependencies.bootstrapJsDeps.value,
    webpackConfigFile := Some(baseDirectory.value / "custom.webpack.config.js")
  )

lazy val bootstrap4 = jsProject(project)
  .dependsOn(`core-js` % CompileAndTest, `css-js` % CompileAndTest, `i18n-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.bootstrap4SjsDeps.value,
    Compile / npmDependencies ++= Dependencies.bootstrap4JsDeps.value,
    webpackConfigFile := Some(baseDirectory.value / "custom.webpack.config.js"),
  )

lazy val charts = jsProject(project)
  .dependsOn(`core-js` % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.chartsSjsDeps.value
  )

lazy val benchmarks = jsProject(project)
  .dependsOn(`core-js`, `i18n-js`, `css-js`)
  .settings(
    noPublishSettings,

    libraryDependencies ++= Dependencies.benchmarksSjsDeps.value,
    Compile / scalaJSUseMainModuleInitializer := true,
  )

lazy val selenium = jvmProject(project)
  .dependsOn(
    core % CompileAndTest, rpc % CompileAndTest, rest % CompileAndTest,
    css % CompileAndTest, auth % CompileAndTest, i18n % CompileAndTest
  )
  .settings(
    noPublishSettings,

    Test / parallelExecution := false,
    Test / compile := (Test / compile)
      .dependsOn(LocalProject("selenium-js") / compileAndOptimizeStatics).value,

    libraryDependencies ++= Dependencies.seleniumJvmDeps.value
  )

// Custom SBT tasks
val copyAssets = taskKey[Unit]("Copies all assets to the target directory.")
val cssDir = settingKey[File]("Target for `compileCss` task.")
val compileCss = taskKey[Unit]("Compiles CSS files.")
val compileStatics = taskKey[Unit](
  "Compiles JavaScript files and copies all assets to the target directory."
)
val compileAndOptimizeStatics = taskKey[Unit](
  "Compiles and optimizes JavaScript files and copies all assets to the target directory."
)

val seleniumStaticsRoot = "UdashStatics/WebContent"

lazy val `selenium-js` = jsProjectFor(project, selenium)
  .dependsOn(
    `core-js` % CompileAndTest, `rpc-js` % CompileAndTest, `rest-js` % CompileAndTest,
    `css-js` % CompileAndTest, `auth-js` % CompileAndTest, `i18n-js` % CompileAndTest,
    bootstrap4 % CompileAndTest
  )
  .settings(
    noPublishSettings,

    Compile / emitSourceMaps := true,
    Compile / scalaJSUseMainModuleInitializer := true,

    webpackConfigFile := Some(baseDirectory.value / "custom.webpack.config.js"),

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
      val sjsFileName = (Compile / fastOptJS).value.data.name.stripSuffix(".js")
      IO.copyFile(
        (Compile / npmUpdate / crossTarget).value / s"$sjsFileName-bundle.js",
        target.value / s"$seleniumStaticsRoot/scripts/frontend.js"
      )
      IO.copyFile(
        (Compile / npmUpdate / crossTarget).value / s"$sjsFileName-bundle.js.map",
        target.value / s"$seleniumStaticsRoot/scripts/frontend.js.map"
      )
    },
    compileStatics := compileStatics.dependsOn(
      Compile / fastOptJS / webpack, Compile / copyAssets
    ).value,

    // Compiles JS files with full optimizations
    compileAndOptimizeStatics := {
      val sjsFileName = (Compile / fullOptJS).value.data.name.stripSuffix(".js")
      IO.copyFile(
        (Compile / npmUpdate / crossTarget).value / s"$sjsFileName-bundle.js",
        target.value / s"$seleniumStaticsRoot/scripts/frontend.js"
      )
      IO.copyFile(
        (Compile / npmUpdate / crossTarget).value / s"$sjsFileName-bundle.js.map",
        target.value / s"$seleniumStaticsRoot/scripts/frontend.js.map"
      )
    },
    compileAndOptimizeStatics := compileAndOptimizeStatics.dependsOn(
      Compile / fullOptJS / webpack, Compile / copyAssets
    ).value
  )
