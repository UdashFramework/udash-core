package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.Versions
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object BootstrappingSbtViewFactory extends StaticViewFactory[BootstrappingSbtState.type](() => new BootstrappingSbtView)

class BootstrappingSbtView extends FinalView with CssView {
  import Context._
  import JsDom.all._
  import io.udash.web.guide.views.References._

  override def getTemplate: Modifier =
    div(
      h2("SBT configuration"),
      p(
        a(href := SbtHomepage)("SBT"),
        " is the recommended build tool for ScalaJS. The excellent ",
        a(href := SbtScalaJsPluginHomepage)("Scala.js SBT plugin"),
        " provides tools for configuring cross-compiled modules and JS dependencies management. If you want to use it, " +
          "add the following line into the ", i("project/plugins.sbt"), " file: "
      ),
      CodeBlock(
        s"""addSbtPlugin("org.scala-js" % "sbt-scalajs" % "${Versions.scalaJSPluginVersion}")""".stripMargin
      )(GuideStyles),
      p(
        "We recommend testing your code compiled to JavaScript in a browser. The ",
        a(href := ScalaJsEnvSeleniumHomepage)("scalajs-env-selenium"), " plugin enables you to do that. ",
        "Put the following line on top of the ", i("project/plugins.sbt"), " file. "
      ),
      CodeBlock(
        s"""libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "${Versions.scalaJSSeleniumPluginVersion}" """.stripMargin
      )(GuideStyles),
      h3("SBT dependencies"),
      p(
        "To keep the configuration clean, put dependencies in a separate file such as ",
        i("project/Dependencies.scala"), "."
      ),
      CodeBlock(
        s"""import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
           |import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
           |import sbt._
           |
           |object Dependencies {
           |  val versionOfScala = "${Versions.scalaVersion}"
           |  // We are going to use Jetty as webserver in this guide
           |  val jettyVersion = "${Versions.jettyVersion}"
           |  val udashVersion = "${Versions.udashVersion}"
           |  val udashJQueryVersion = "${Versions.udashJQueryVersion}"
           |  val bootstrapVersion = "${Versions.bootstrapVersion}"
           |  val scalatestVersion = "${Versions.scalatestVersion}"
           |
           |  // Dependencies for both frontend and backend
           |  // Those have to be cross-compilable
           |  val crossDeps = Def.setting(Seq(
           |    "io.udash" %%% "udash-core-shared" % udashVersion,
           |    "io.udash" %%% "udash-rpc-shared" % udashVersion
           |  ))
           |
           |  // Dependencies compiled to JavaScript code
           |  val frontendDeps = Def.setting(Seq(
           |    "io.udash" %%% "udash-core-frontend" % udashVersion,
           |    "io.udash" %%% "udash-rpc-frontend" % udashVersion,
           |    // type-safe wrapper for jQuery
           |    "io.udash" %%% "udash-jquery" % udashJQueryVersion
           |  ))
           |
           |  // JavaScript libraries dependencies
           |  // Those will be added into frontend-deps.js
           |  val frontendJSDeps = Def.setting(Seq(
           |    // it's optional of course
           |    // "jquery.js" is provided by "udash-jquery" dependency
           |    "org.webjars" % "bootstrap" % bootstrapVersion /
           |      "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
           |  ))
           |
           |  // Dependencies for JVM part of code
           |  val backendDeps = Def.setting(Seq(
           |    "io.udash" %% "udash-rpc-backend" % udashVersion,
           |
           |    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
           |    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion
           |  ))
           |
           |  // Test dependencies
           |  val crossTestDeps = Def.setting(Seq(
           |    "org.scalatest" %%% "scalatest" % scalatestVersion
           |  ).map(_ % Test))
           |}""".stripMargin
      )(GuideStyles),
      p(
        "You are not obligated to use ", a(href := JettyHomepage)("Jetty"), " as webserver nor to use ",
        a(href := BootstrapHomepage)("Twitter bootstrap"), " in frontend. Anyway it is recommended to use ",
        a(href := JettyHomepage)("Jetty"), " for an easy start, because you can embed it inside your code and launch easily anywhere."
      ),
      h3("SBT build configuration"),
      p("Let's start with some basic options inside the ", i("build.sbt"), " file (you can change these if you need):"),
      CodeBlock(
        """name := "my-app"
          |
          |inThisBuild(Seq(
          |  version := "0.1.0-SNAPSHOT",
          |  scalaVersion := Dependencies.versionOfScala,
          |  organization := "io.app",
          |  scalacOptions ++= Seq(
          |    "-feature",
          |    "-deprecation",
          |    "-unchecked",
          |    "-language:implicitConversions",
          |    "-language:existentials",
          |    "-language:dynamics",
          |    "-Xfuture",
          |    "-Xfatal-warnings",
          |    "-Xlint:_,-missing-interpolator,-adapted-args",
          |  ),
          |))""".stripMargin
        )(GuideStyles),
      p("Lets define some helper values to reuse common modules configuration parts."),
      CodeBlock(
        """// Custom SBT tasks
          |val copyAssets = taskKey[Unit]("Copies all assets to the target directory.")
          |val compileStatics = taskKey[Unit](
          |  "Compiles JavaScript files and copies all assets to the target directory."
          |)
          |val compileAndOptimizeStatics = taskKey[Unit](
          |  "Compiles and optimizes JavaScript files and copies all assets to the target directory."
          |)
          |
          |// Settings for JS tests run in browser
          |import org.scalajs.jsenv.selenium.SeleniumJSEnv
          |import org.openqa.selenium.chrome.ChromeOptions
          |import org.openqa.selenium.remote.DesiredCapabilities
          |val browserCapabilities: DesiredCapabilities = {
          |  // requires ChromeDriver: https://sites.google.com/a/chromium.org/chromedriver/
          |  val capabilities = DesiredCapabilities.chrome()
          |  capabilities.setCapability(ChromeOptions.CAPABILITY, {
          |    val options = new ChromeOptions()
          |    options.addArguments("--headless", "--disable-gpu")
          |    options
          |  })
          |  capabilities
          |}
          |
          |// Reusable settings for all modules
          |val commonSettings = Seq(
          |  moduleName := "my-app-" + moduleName.value,
          |)
          |
          |// Reusable settings for modules compiled to JS
          |val commonJSSettings = Seq(
          |  Compile / emitSourceMaps := true,
          |  // enables scalajs-env-selenium plugin
          |  Test / jsEnv := new SeleniumJSEnv(browserCapabilities),
          |)""".stripMargin
      )(GuideStyles),
      p(
        "The root project will aggregate all needed modules and will not publish an artifact. ",
        "It depends on the ", i("backend"), " module and defines main class for the SBT command: ",
        i("run"), "."
      ),
      CodeBlock(
        """lazy val udashGuide = project.in(file("."))
          |  .aggregate(sharedJS, sharedJVM, frontend, backend)
          |  .dependsOn(backend)
          |  .settings(
          |    publishArtifact := false,
          |    Compile / mainClass := Some("io.app.backend.Launcher")
          |  )""".stripMargin
      )(GuideStyles),
      p("Next, you need to create the shared module."),
      CodeBlock(
        """lazy val shared = crossProject
          |  .crossType(CrossType.Pure).in(file("shared"))
          |  .settings(commonSettings)
          |  .jsSettings(commonJSSettings)
          |  .settings(
          |    libraryDependencies ++= Dependencies.crossDeps.value,
          |    libraryDependencies ++= Dependencies.crossTestDeps.value
          |  )
          |
          |lazy val sharedJVM = shared.jvm
          |lazy val sharedJS = shared.js""".stripMargin
      )(GuideStyles),
      p(
        "The frontend module uses ScalaJSPlugin and depends on ", i("sharedJS"), ". Besides common settings, ",
        "it requires a bit more customizations.",
        ul(GuideStyles.defaultList)(
          li("Definition of main class for Scala.js application."),
          li("Implementation of ", i("copyAssets"), " task - copies frontend assets to target directory."),
          li("Implementation of ", i("compileStatics"), " task - runs fast compilation of JS and ", i("copyAssets"), " task."),
          li(
            "Implementation of ", i("compileAndOptimizeStatics"), " task - runs full compilation of JS and ",
            i("copyAssets"), " task."
          ),
          li(
            "Definition of Scala.js target files - we recommend using the same names for development and production ",
            "configuration, because it significantly simplifies configuration."
          )
        )
      ),
      CodeBlock(
        """lazy val frontend = project.in(file("frontend"))
          |  .enablePlugins(ScalaJSPlugin) // enables Scala.js plugin in this module
          |  .dependsOn(sharedJS % "test->test;compile->compile")
          |  .settings(commonSettings)
          |  .settings(commonJSSettings)
          |  .settings(
          |    libraryDependencies ++= Dependencies.frontendDeps.value,
          |    jsDependencies ++= Dependencies.frontendJSDeps.value, // native JS dependencies
          |
          |    // Make this module executable in JS
          |    Compile / mainClass := Some("io.app.frontend.JSLauncher"),
          |    scalaJSUseMainModuleInitializer := true,
          |
          |    // Implementation of custom tasks defined above
          |    copyAssets := {
          |      IO.copyDirectory(
          |        sourceDirectory.value / "main/assets",
          |        target.value / "UdashStatics/WebContent/assets"
          |      )
          |      IO.copyFile(
          |        sourceDirectory.value / "main/assets/index.html",
          |        target.value / "UdashStatics/WebContent/index.html"
          |      )
          |    },
          |    compileStatics := {},
          |    compileStatics := compileStatics.dependsOn(
          |      Compile / fastOptJS, Compile / copyAssets
          |    ).value,
          |    compileAndOptimizeStatics := {},
          |    compileAndOptimizeStatics := compileAndOptimizeStatics.dependsOn(
          |      Compile / fullOptJS, Compile / copyAssets
          |    ).value,
          |
          |    // Target files for Scala.js plugin
          |    Compile / fastOptJS / artifactPath :=
          |      (Compile / fastOptJS / target).value /
          |        "UdashStatics" / "WebContent" / "scripts" / "frontend.js",
          |    Compile / fullOptJS / artifactPath :=
          |      (Compile / fullOptJS / target).value /
          |        "UdashStatics" / "WebContent" / "scripts" / "frontend.js",
          |    Compile / packageJSDependencies / artifactPath :=
          |      (Compile / packageJSDependencies / target).value /
          |        "UdashStatics" / "WebContent" / "scripts" / "frontend-deps.js",
          |    Compile / packageMinifiedJSDependencies / artifactPath :=
          |      (Compile / packageMinifiedJSDependencies / target).value /
          |        "UdashStatics" / "WebContent" / "scripts" / "frontend-deps.js"
          |
          |  )""".stripMargin
      )(GuideStyles),
      p(
        "There is only the backend module left. Notice that backend depends on ",
        i("sharedJVM"), " not ", i("sharedJS"), ". Besides there is nothing unusual in this configuration."
      ),
      CodeBlock(
        """lazy val backend = project.in(file("backend"))
          |  .dependsOn(sharedJVM % "test->test;compile->compile")
          |  .settings(commonSettings)
          |  .settings(
          |    libraryDependencies ++= Dependencies.backendDeps.value,
          |    Compile / mainClass := Some("io.app.backend.Launcher"),
          |  )""".stripMargin
      )(GuideStyles),
      h3("Static files"),
      p(
        "In the ", i("frontend"), " module create the ", i("src/main/assets"), " directory. Inside it create ",
        i("index.html"), " file, then fill it with the below bootstrapping html:"
      ),
      CodeBlock(
        """<!DOCTYPE html>
          |<html>
          |<head lang="en">
          |  <meta charset="UTF-8">
          |  <title>My Udash App</title>
          |  <script src="scripts/frontend-deps.js"></script>
          |  <script src="scripts/frontend.js"></script>
          |</head>
          |<body>
          |  <div id="application"></div>
          |</body>
          |</html>""".stripMargin
      )(GuideStyles),
      p("You can also put your fonts, styles or images into this directory."),
      p(
        "It is recommended to use fully optimized ", i("frontend-deps.js"), " and ", i("frontend-impl.js"),
        " in the production version. Remember to check if file names correspond to those in SBT configurations."
      ),
      h3("SBT commands"),
      p("You project configuration is ready. Take a look at sbt commands which you will use:"),
      ul(GuideStyles.defaultList)(
        li("compile - compiles all your Scala sources (it does not produce JS files)."),
        li("copyAssets - copies all your assets into target directory."),
        li("compileStatics - produces whole frontend application with full JS optimization (includes copyAssets)."),
        li("compileAndOptimizeStatics  - as above but with full JS optimization."),
        li("run  - starts backend server."),
      ),
      p("Notice that you can prefix commands with ~ to automatically rerun them on sources change."),
      h2("What's next?"),
      p(
        "SBT configuration is ready, now it is time to prepare ", a(href := BootstrappingRpcState.url)("RPC interfaces"),
        " in the ", b("shared"), " module."
      )
    )
}