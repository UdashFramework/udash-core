import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalajs.jsenv.selenium.SeleniumJSEnv
import sbt.Compile
import sbtcrossproject.{crossProject, CrossType}

name := "udash-guide"

inThisBuild(Seq(
  version := "0.7.1",
  scalaVersion := Dependencies.versionOfScala,
  organization := "io.udash",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:dynamics",
    "-Xfuture",
    "-Xfatal-warnings",
    "-Xlint:_,-missing-interpolator,-adapted-args"
  ),
))

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

// Reusable settings for all modules
val commonSettings = Seq(
  moduleName := "udash-guide-" + moduleName.value
)

// Reusable settings for modules compiled to JS
val commonJSSettings = Seq(
  Compile / emitSourceMaps  := true,
  Compile / scalaJSUseMainModuleInitializer := true,
  Test / scalaJSUseMainModuleInitializer := false,

  // native JS dependencies
  jsDependencies ++= Dependencies.frontendJsDeps.value,

  // enables scalajs-env-selenium plugin
  Test / jsEnv := new SeleniumJSEnv(browserCapabilities)
)

lazy val udashGuide = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, guide, homepage, backend, `frontend-commons`)
  .dependsOn(backend)
  .settings(
    publishArtifact := false,
    Compile / mainClass := Some("io.udash.web.Launcher")
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("shared"))
  .settings(commonSettings)
  .jsSettings(commonJSSettings)
  .settings(
    libraryDependencies ++= Dependencies.crossDeps.value
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val backend = project.in(file("backend"))
  .dependsOn(sharedJVM)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Dependencies.backendDeps.value,
    Compile / mainClass := Some("io.udash.web.Launcher"),
  )

lazy val `frontend-commons` = project.in(file("commons")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(commonSettings)
  .settings(commonJSSettings)
  .settings(
    libraryDependencies ++= Dependencies.frontendDeps.value
  )

def frontendProject(proj: Project, sourceDir: File)(
  staticsRoot: String, cssRendererObject: String, jsDeps: Def.Initialize[Seq[org.scalajs.sbtplugin.JSModuleID]]
) = {
  proj.in(sourceDir)
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(`frontend-commons`)
    .settings(commonSettings)
    .settings(commonJSSettings)
    .settings(
      jsDependencies ++= jsDeps.value,

      Compile / copyAssets := {
        IO.copyDirectory(
          (`frontend-commons` / sourceDirectory).value / "main/assets",
          target.value / s"$staticsRoot/assets"
        )
        IO.copyDirectory(
          sourceDirectory.value / "main/assets",
          target.value / s"$staticsRoot/assets"
        )
        IO.copyFile(
          sourceDirectory.value / "main/assets/index.html",
          target.value / s"$staticsRoot/index.html"
        )
      },

      // Compiles CSS files and put them in the target directory
      cssDir := (Compile / fastOptJS / target).value / staticsRoot / "styles",
      compileCss := Def.taskDyn {
        val dir = (Compile / cssDir).value
        val path = dir.absolutePath
        dir.mkdirs()
        (backend / Compile / runMain).toTask(s" $cssRendererObject $path false")
      }.value,

      // Compiles JS files without full optimizations
      compileStatics := {
        (Compile / fastOptJS / target).value / "UdashStatics"
      },
      compileStatics := compileStatics.dependsOn(
        Compile / fastOptJS, Compile / copyAssets, Compile / compileCss
      ).value,

      // Compiles JS files with full optimizations
      compileAndOptimizeStatics := {
        (Compile / fullOptJS / target).value / "UdashStatics"
      },
      compileAndOptimizeStatics := compileAndOptimizeStatics.dependsOn(
        Compile / fullOptJS, Compile / copyAssets, Compile / compileCss
      ).value,

      // Target files for Scala.js plugin
      Compile / fastOptJS / artifactPath :=
        (Compile / fastOptJS / target).value /
          staticsRoot / "scripts" / "frontend.js",
      Compile / fullOptJS / artifactPath :=
        (Compile / fullOptJS / target).value /
          staticsRoot / "scripts" / "frontend.js",
      Compile / packageJSDependencies / artifactPath :=
        (Compile / packageJSDependencies / target).value /
          staticsRoot / "scripts" / "frontend-deps.js",
      Compile / packageMinifiedJSDependencies / artifactPath :=
        (Compile / packageMinifiedJSDependencies / target).value /
          staticsRoot / "scripts" / "frontend-deps.js"
    )
}

lazy val guide = frontendProject(project, file("guide"))(
  staticsRoot = "UdashStatics/WebContent/guide",
  cssRendererObject = "io.udash.web.styles.GuideCssRenderer",
  jsDeps = Dependencies.guideJsDeps,
)

lazy val homepage = frontendProject(project, file("homepage"))(
  staticsRoot = "UdashStatics/WebContent/homepage",
  cssRendererObject = "io.udash.web.styles.HomepageCssRenderer",
  jsDeps = Dependencies.homepageJsDeps
)

lazy val selenium = project.in(file("selenium"))
  .dependsOn(backend)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Dependencies.seleniumDeps.value,
    libraryDependencies ++= Dependencies.testDeps.value,

    parallelExecution := false,

    Compile / compile := (Compile / compile).dependsOn(
      homepage / Compile / compileStatics,
      guide / Compile / compileStatics,
    ).value
  )

lazy val packager = project
  .in(file("packager"))
  .dependsOn(backend)
  .enablePlugins(JavaServerAppPackaging)
  .settings(commonSettings)
  .settings(
    normalizedName := "udash-guide",
    Compile / mainClass := (backend / Compile / mainClass).value,

    // add homepage statics to the package
    Universal / mappings ++= {
      import Path.relativeTo
      val frontendStatics = (homepage / Compile / compileAndOptimizeStatics).value
      (frontendStatics.allPaths --- frontendStatics) pair relativeTo(frontendStatics.getParentFile)
    },

    // add guide statics to the package
    Universal / mappings ++= {
      import Path.relativeTo
      val frontendStatics = (guide / Compile / compileAndOptimizeStatics).value
      (frontendStatics.allPaths --- frontendStatics) pair relativeTo(frontendStatics.getParentFile)
    },
  )