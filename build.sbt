import org.openqa.selenium.Capabilities
import org.openqa.selenium.firefox.{FirefoxDriverLogLevel, FirefoxOptions}
import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.jsenv.selenium.SeleniumJSEnv
import org.scalajs.sbtplugin.JSModuleID

name := "udash"

inThisBuild(Seq(
  version := "0.9.0-SNAPSHOT",
  organization := "io.udash",
  cancelable := true,
  resolvers += Resolver.defaultLocal
))

val forIdeaImport = System.getProperty("idea.managed", "false").toBoolean && System.getProperty("idea.runid") == null
val CompileAndTest = "test->test;compile->compile"

// Settings for JS tests run in browser
val browserCapabilities: Capabilities = {
  // requires gecko driver, see https://github.com/mozilla/geckodriver
  new FirefoxOptions().setHeadless(true).setLogLevel(FirefoxDriverLogLevel.WARN)
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
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
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
  scalaVersion := Dependencies.versionOfScala,
  crossScalaVersions := Seq(Dependencies.versionOfScala /*, "2.13.0"*/),
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
    "-Yrangepos",
    "-P:silencer:checkUnused",
    "-Ywarn-unused:_,-explicits,-implicits",
    "-Ybackend-parallelism", "8",
    "-Ycache-plugin-class-loader:last-modified",
    "-Ycache-macro-class-loader:last-modified"
  ),
  moduleName := "udash-" + moduleName.value,
  ideBasePackages := Seq("io.udash"),
  ideOutputDirectory in Compile := Some(target.value.getParentFile / "out/production"),
  ideOutputDirectory in Test := Some(target.value.getParentFile / "out/test"),
  libraryDependencies ++= Dependencies.compilerPlugins.value,
  libraryDependencies ++= Dependencies.commonDeps.value,
  libraryDependencies ++= Dependencies.commonTestDeps.value,
  autoAPIMappings := true
) ++ deploymentConfiguration

val commonJsSettings = commonSettings ++ Seq(
  Compile / emitSourceMaps := true,
  Test / scalaJSStage := FastOptStage,
  Test / scalaJSUseMainModuleInitializer := false,
  Test / jsEnv := new NodeJSEnv,
  scalacOptions += {
    val localDir = (ThisBuild / baseDirectory).value.toURI.toString
    val githubDir = "https://raw.githubusercontent.com/UdashFramework/udash-core"
    s"-P:scalajs:mapSourceURI:$localDir->$githubDir/v${version.value}/"
  },
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",

  //library CSS settings
  LessKeys.cleancss in Assets := true,
  LessKeys.compress in Assets := true,
  LessKeys.strictMath in Assets := true,
  LessKeys.verbose in Assets := true,
)

val testInBrowser = Seq(
  Test / parallelExecution := false,
  Test / jsEnv := new SeleniumJSEnv(browserCapabilities),
)

val noPublishSettings = Seq(
  publish / skip := true,
  Compile / doc := (doc / target).value,
)

val aggregateProjectSettings = noPublishSettings ++ Seq(
  ideSkipProject := true,
  crossScalaVersions := Nil,
)

def sourceDirsSettings(baseMapper: File => File) = {
  def mkSourceDirs(base: File, scalaBinary: String, conf: String): Seq[File] = Seq(
    base / "src" / conf / "scala",
    base / "src" / conf / s"scala-$scalaBinary",
    base / "src" / conf / "java"
  )

  def mkResourceDirs(base: File, conf: String): Seq[File] = Seq(
    base / "src" / conf / "resources"
  )
  Seq(
    Compile / unmanagedSourceDirectories ++=
      mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "main"),
    Compile / unmanagedResourceDirectories ++=
      mkResourceDirs(baseMapper(baseDirectory.value), "main"),
    Test / unmanagedSourceDirectories ++=
      mkSourceDirs(baseMapper(baseDirectory.value), scalaBinaryVersion.value, "test"),
    Test / unmanagedResourceDirectories ++=
      mkResourceDirs(baseMapper(baseDirectory.value), "test"),
  )
}

def jvmProject(proj: Project): Project =
  proj.settings(
    commonSettings,
    sourceDirsSettings(_ / ".jvm"),
  )

def jsProject(proj: Project): Project =
  proj.in(proj.base / ".js")
    .enablePlugins(ScalaJSPlugin)
    .settings(commonJsSettings)

def jsProjectFor(jsProj: Project, jvmProj: Project): Project =
  jsProj.in(jvmProj.base / ".js")
    .enablePlugins(ScalaJSPlugin)
    .configure(p => if (forIdeaImport) p.dependsOn(jvmProj) else p)
    .settings(
      commonJsSettings,

      moduleName := (jvmProj / moduleName).value,
      sourceDirsSettings(_.getParentFile),

      // workaround for some cross-compilation problems in IntelliJ
      libraryDependencies ++= (if (forIdeaImport) (jvmProj / libraryDependencies).value else Seq.empty)
    )

def frontendExecutable(proj: Project)(
  staticsRoot: String,
  jsDeps: Def.Initialize[Seq[JSModuleID]],
  cssRenderer: Option[(Project, String)] = None,
  additionalAssetsDirectory: Def.Initialize[Task[Option[File]]] = Def.task(None),
) = {
  proj
    .enablePlugins(ScalaJSPlugin, SbtWeb)
    .settings(commonJsSettings)
    .settings(
      noPublishSettings,

      jsDependencies ++= jsDeps.value,
      Compile / emitSourceMaps := true,
      Compile / scalaJSUseMainModuleInitializer := true,

      Assets / LessKeys.less / includeFilter := "assets.less",
      Assets / LessKeys.less / resourceManaged := (Compile / target).value / staticsRoot / "assets" / "styles",

      Compile / copyAssets := {
        val udashStatics = target.value / staticsRoot
        val assets = udashStatics / "assets"
        additionalAssetsDirectory.value.foreach(IO.copyDirectory(_, assets))
        IO.copyDirectory(sourceDirectory.value / "main" / "assets", assets)
        IO.move(assets / "index.html", udashStatics / "index.html")
        IO.delete(assets / "assets.less")
        // copying font-awesome webfonts to a location required by font-awesome styles
        // (appropriate CSS file is inlined by less within UdashStatics/WebContent/guide/assets/styles/assets.min.css)
        IO.copyDirectory(
          (Assets / WebKeys.webJarsDirectory).value / WebKeys.webModulesLib.value / "font-awesome/webfonts",
          assets / "webfonts"
        )
      },
      // a font-awesome WebJar is required on the classpath to execute the copyAssets task
      Compile / copyAssets := (Compile / copyAssets).dependsOn(Assets / WebKeys.webJars).value,

      // Compiles CSS files and put them in the target directory
      compileCss := Def.taskDyn {
        cssRenderer.map { case (rendererProject, rendererClass) =>
          val dir = (Compile / target).value / staticsRoot / "styles"
          val path = dir.absolutePath
          dir.mkdirs()
          (rendererProject / Compile / runMain).toTask(s" $rendererClass $path false")
        }.getOrElse(Def.task(()))
      }.value,

      // Compiles JS files without full optimizations
      compileStatics := (Compile / fastOptJS / target).value / "UdashStatics",
      compileStatics := compileStatics.dependsOn(
        Compile / fastOptJS, Compile / copyAssets, Compile / compileCss
      ).value,

      // Compiles JS files with full optimizations
      compileAndOptimizeStatics := (Compile / fullOptJS / target).value / "UdashStatics",
      compileAndOptimizeStatics := compileAndOptimizeStatics.dependsOn(
        Compile / fullOptJS, Compile / copyAssets, Compile / compileCss
      ).value,

      // Workaround for source JS dependencies overwriting the minified ones - just use the latter all the time
      skip in (Compile / packageJSDependencies) := true,
      (Compile / fastOptJS) := (Compile / fastOptJS).dependsOn(Compile / packageMinifiedJSDependencies).value,

      // Target files for Scala.js plugin
      Compile / fastOptJS / artifactPath :=
        (Compile / fastOptJS / target).value / staticsRoot / "scripts" / "frontend.js",
      Compile / fullOptJS / artifactPath :=
        (Compile / fullOptJS / target).value / staticsRoot / "scripts" / "frontend.js",
      Compile / packageJSDependencies / artifactPath :=
        (Compile / packageJSDependencies / target).value / staticsRoot / "scripts" / "frontend-deps.js",
      Compile / packageMinifiedJSDependencies / artifactPath :=
        (Compile / packageMinifiedJSDependencies / target).value / staticsRoot / "scripts" / "frontend-deps.js"
    )
}

lazy val udash = project.in(file("."))
  .aggregate(`udash-jvm`, `udash-js`, guide)
  .settings(
    aggregateProjectSettings,
    ideSkipProject := false,
  )

//for simplifying Travis build matrix and project dependencies
lazy val jvmLibraries = Seq[ProjectReference](macros, utils, core, rpc, rest, `rest-jetty`, i18n, auth, css)
lazy val `udash-jvm` = project.in(file(".jvm"))
  .aggregate(jvmLibraries: _*)
  .settings(aggregateProjectSettings)

lazy val jsLibraries = Seq[ProjectReference](
  macros, `utils-js`, `core-js`, `rpc-js`, `rest-js`, `i18n-js`, `auth-js`, `css-js`, bootstrap4, charts
)
lazy val `udash-js` = project.in(file(".js"))
  .aggregate(jsLibraries: _*)
  .settings(aggregateProjectSettings)

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
    testInBrowser,
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
    testInBrowser,
    libraryDependencies ++= Dependencies.rpcSjsDeps.value,
    jsDependencies ++= Dependencies.rpcJsDeps.value,
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

lazy val `rest-jetty` = jvmProject(project.in(file("rest/jetty")))
  .dependsOn(rest % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.restJettyDeps.value,
  )

lazy val i18n = jvmProject(project)
  .dependsOn(core % CompileAndTest, rpc % CompileAndTest)

lazy val `i18n-js` = jsProjectFor(project, i18n)
  .dependsOn(`core-js` % CompileAndTest, `rpc-js` % CompileAndTest)
  .settings(testInBrowser)

lazy val auth = jvmProject(project)
  .dependsOn(core % CompileAndTest, rpc)

lazy val `auth-js` = jsProjectFor(project, auth)
  .dependsOn(`core-js` % CompileAndTest, `rpc-js`)
  .settings(testInBrowser)

lazy val css = jvmProject(project)
  .dependsOn(core % CompileAndTest)
  .settings(
    libraryDependencies ++= Dependencies.cssJvmDeps.value,
  )

lazy val `css-js` = jsProjectFor(project, css)
  .dependsOn(`core-js` % CompileAndTest)
  .settings(
    testInBrowser,
    libraryDependencies ++= Dependencies.cssSjsDeps.value,
  )

lazy val bootstrap4 = jsProject(project)
  .dependsOn(`core-js` % CompileAndTest, `css-js`, `i18n-js`)
  .settings(
    testInBrowser,
    libraryDependencies ++= Dependencies.bootstrap4SjsDeps.value,
    jsDependencies ++= Dependencies.bootstrap4JsDeps.value
  )

lazy val charts = jsProject(project)
  .dependsOn(`core-js`)
  .settings(
    libraryDependencies ++= Dependencies.chartsSjsDeps.value
  )

lazy val benchmarks = jsProject(project)
  .dependsOn(jsLibraries.map(p => p: ClasspathDep[ProjectReference]): _*)
  .settings(
    noPublishSettings,

    libraryDependencies ++= Dependencies.benchmarksSjsDeps.value,
    Compile / scalaJSUseMainModuleInitializer := true,
  )

// Custom SBT tasks
val copyAssets = taskKey[Unit]("Copies all assets to the target directory.")
val compileCss = taskKey[Unit]("Compiles CSS files.")
val compileStatics = taskKey[File](
  "Compiles JavaScript files and copies all assets to the target directory."
)
val compileAndOptimizeStatics = taskKey[File](
  "Compiles and optimizes JavaScript files and copies all assets to the target directory."
)

lazy val guide = project.in(file("guide"))
  .aggregate(`guide-shared`, `guide-shared-js`, `guide-backend`, `guide-commons`, `guide-homepage`, 
    `guide-guide`, `guide-packager`, `guide-selenium`)
  .settings(
    aggregateProjectSettings,
    ideSkipProject := true,
  )

lazy val `guide-shared` =
  jvmProject(project.in(file("guide/shared")))
    .dependsOn(jvmLibraries.map(p => p: ClasspathDep[ProjectReference]): _*)
    .settings(noPublishSettings)

lazy val `guide-shared-js` =
  jsProjectFor(project, `guide-shared`)
    .dependsOn(jsLibraries.map(p => p: ClasspathDep[ProjectReference]): _*)
    .settings(noPublishSettings)

lazy val `guide-backend` =
  jvmProject(project.in(file("guide/backend")))
    .dependsOn(`guide-shared`)
    .settings(
      noPublishSettings,
      libraryDependencies ++= Dependencies.backendDeps.value,
      Compile / mainClass := Some("io.udash.web.Launcher"),
    )

lazy val `guide-commons` =
  jsProject(project.in(file("guide/commons")))
    .enablePlugins(SbtWeb)
    .dependsOn(`guide-shared-js`)
    .settings(
      noPublishSettings,
      libraryDependencies ++= Dependencies.guideFrontendDeps.value
    )
lazy val `guide-homepage` =
  frontendExecutable(jsProject(project.in(file("guide/homepage"))).dependsOn(`guide-commons`))(
    "UdashStatics/WebContent/homepage",
    Dependencies.homepageJsDeps,
    Some((`guide-backend`, "io.udash.web.styles.HomepageCssRenderer")),
    Def.task(Some((`guide-commons` / sourceDirectory).value / "main" / "assets"))
  )

lazy val `guide-guide` =
  frontendExecutable(jsProject(project.in(file("guide/guide"))).dependsOn(`guide-commons`))(
    "UdashStatics/WebContent/guide",
    Dependencies.guideJsDeps,
    Some((`guide-backend`, "io.udash.web.styles.GuideCssRenderer")),
    Def.task(Some((`guide-commons` / sourceDirectory).value / "main" / "assets"))
  )

lazy val `guide-packager` =
  project.in(file("guide/packager"))
    .dependsOn(`guide-backend`)
    .enablePlugins(JavaServerAppPackaging)
    .settings(
      noPublishSettings,
      commonSettings,
      
      normalizedName := "udash-guide",
      maintainer := "dawid.dworak@gmail.com",
      Compile / mainClass := (`guide-backend` / Compile / mainClass).value,

      // add homepage statics to the package
      Universal / mappings ++= {
        import Path.relativeTo
        val frontendStatics = (`guide-homepage` / Compile / compileAndOptimizeStatics).value
        (frontendStatics.allPaths --- frontendStatics) pair relativeTo(frontendStatics.getParentFile)
      },

      // add guide statics to the package
      Universal / mappings ++= {
        import Path.relativeTo
        val frontendStatics = (`guide-guide` / Compile / compileAndOptimizeStatics).value
        (frontendStatics.allPaths --- frontendStatics) pair relativeTo(frontendStatics.getParentFile)
      },
    )

lazy val `guide-selenium` =
  jvmProject(project.in(file("guide/selenium")))
    .dependsOn(`guide-backend`)
    .settings(
      noPublishSettings,

      libraryDependencies ++= Dependencies.backendDeps.value,

      Test / parallelExecution := false,

      Test / compile := (Test / compile).dependsOn(
        `guide-homepage` / Compile / compileStatics,
        `guide-guide` / Compile / compileStatics,
      ).value
    )