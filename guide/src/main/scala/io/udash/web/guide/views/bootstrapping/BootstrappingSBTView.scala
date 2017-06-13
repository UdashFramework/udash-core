package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.Versions
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object BootstrappingSBTViewPresenter extends DefaultViewPresenterFactory[BootstrappingSBTState.type](() => new BootstrappingSBTView)

class BootstrappingSBTView extends FinalView with CssView {
  import Context._
  import io.udash.web.guide.views.References._

  import JsDom.all._

  override def getTemplate: Modifier =
    div(
      h2("SBT configuration"),
      p(
        a(href := SbtHomepage)("SBT"),
        " is the recommended build tool for ScalaJS. The excellent ",
        a(href := SbtScalaJsPluginHomepage)("ScalaJS SBT plugin"),
        " provides tools for configuring cross-compiled modules and JS dependencies management. If you want to use it, " +
          "add the following line into the ", i("project/plugins.sbt"), " file: "
      ),
      CodeBlock(s"""addSbtPlugin("org.scala-js" % "sbt-scalajs" % "${Versions.scalaJSPluginVersion}")""")(GuideStyles),
      h3("SBT dependencies"),
      p(
        "To keep the configuration clean, keep dependencies in a separate file such as ",
        i("project/Dependencies.scala"),
        ""
      ),
      CodeBlock(
        s"""object Dependencies extends Build {
           |  val versionOfScala = "${Versions.scalaVersion}"
           |  // We are going to use Jetty as webserver in this guide
           |  val jettyVersion = "${Versions.jettyVersion}"
           |
           |  val udashVersion = "${Versions.udashVersion}"
           |
           |  val bootstrapVersion = "3.3.1-1"
           |
           |  val scalatestVersion = "3.0.0-M15"
           |  val scalamockVersion = "3.2.2"
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
           |    "io.udash" %%% "udash-rpc-frontend" % udashVersion
           |  ))
           |
           |  // JavaScript libraries dependencies
           |  // Those will be added into frontend-deps.js
           |  val frontendJSDeps = Def.setting(Seq(
           |    // it's optional of course
           |    "org.webjars" % "bootstrap-sass" % bootstrapVersion / "3.3.1/javascripts/bootstrap.js" dependsOn "jquery.js"
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
           |    "org.scalatest" %% "scalatest" % scalatestVersion
           |  ).map(_ % Test))
           |
           |  // JVM test dependencies
           |  val testDeps = Def.setting(Seq(
           |    "org.scalamock" %% "scalamock-scalatest-support" % scalamockVersion
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
          |version in ThisBuild := "0.0.1-SNAPSHOT"
          |scalaVersion in ThisBuild := versionOfScala
          |organization in ThisBuild := "com.example"
          |scalacOptions in ThisBuild ++= Seq(
          |  "-feature",
          |  "-deprecation",
          |  "-unchecked",
          |  "-language:implicitConversions",
          |  "-language:existentials",
          |  "-language:dynamics",
          |  "-Xfuture",
          |  "-Xfatal-warnings",
          |  "-Xlint:_,-missing-interpolator,-adapted-args"
          |)
          |
          |val commonSettings = Seq(
          |  moduleName := "my-app-" + moduleName.value,
          |  (publishArtifact in packageDoc) := false
          |)
          |
          |// Adds cross-compiled dependencies with specified Configuration
          |def crossLibs(configuration: Configuration) =
          |  libraryDependencies ++= crossDeps.value.map(_ % configuration)""".stripMargin
      )(GuideStyles),
      p("The root project will aggregate all needed modules and will not publish an artifact."),
      CodeBlock(
        """lazy val udashGuide = project.in(file("."))
          |  .aggregate(sharedJS, sharedJVM, frontend, backend)
          |  .settings(publishArtifact := false)""".stripMargin
      )(GuideStyles),
      p("Next, you need to create the shared module. Cross libraries are provided by backend and frontend modules."),
      CodeBlock(
        """lazy val shared = crossProject
          |  .crossType(CrossType.Pure).in(file("shared"))
          |  .settings(commonSettings: _*).settings(
          |    crossLibs(Provided),
          |    libraryDependencies ++= crossTestDeps.value
          |  )
          |
          |lazy val sharedJVM = shared.jvm
          |lazy val sharedJS = shared.js""".stripMargin
      )(GuideStyles),
      p(
        "The frontend module uses ScalaJSPlugin and depends on sharedJS. It also provides cross-compiled libraries. persistLauncher ",
        "indicates that you want to generate the JS application launcher."
      ),
      CodeBlock(
        """lazy val frontend = project
          |  .in(file("frontend")).enablePlugins(ScalaJSPlugin)
          |  .dependsOn(sharedJS)
          |  .settings(commonSettings: _*).settings(
          |    crossLibs(Compile),
          |    libraryDependencies ++= frontendDeps.value,
          |    jsDependencies ++= frontendJSDeps.value,
          |    persistLauncher in Compile := true,
          |
          |    compileStatics := {
          |      IO.copyDirectory(
          |        sourceDirectory.value / "main/assets/fonts",
          |        crossTarget.value / StaticFilesDir / "WebContent/assets/fonts"
          |      )
          |      IO.copyDirectory(
          |        sourceDirectory.value / "main/assets/images",
          |        crossTarget.value / StaticFilesDir / "WebContent/assets/images"
          |      )
          |      compileStaticsForRelease.value
          |      (crossTarget.value / StaticFilesDir).***.get
          |    },
          |
          |    // Names of final JS files
          |    artifactPath in(Compile, fastOptJS) :=
          |      (crossTarget in(Compile, fastOptJS)).value /
          |        StaticFilesDir / "WebContent/scripts/frontend-impl-fast.js",
          |    artifactPath in(Compile, fullOptJS) :=
          |      (crossTarget in(Compile, fullOptJS)).value /
          |        StaticFilesDir / "WebContent/scripts/frontend-impl.js",
          |    artifactPath in(Compile, packageJSDependencies) :=
          |      (crossTarget in(Compile, packageJSDependencies)).value /
          |        StaticFilesDir / "WebContent/scripts/frontend-deps-fast.js",
          |    artifactPath in(Compile, packageMinifiedJSDependencies) :=
          |      (crossTarget in(Compile, packageMinifiedJSDependencies)).value /
          |        StaticFilesDir / "WebContent/scripts/frontend-deps.js",
          |    artifactPath in(Compile, packageScalaJSLauncher) :=
          |      (crossTarget in(Compile, packageScalaJSLauncher)).value /
          |        StaticFilesDir / "WebContent/scripts/frontend-init.js"
          |  )""".stripMargin
      )(GuideStyles),
      p(i("compileStatics"), " is our custom task which prepares the whole static files directory for deployment."),
      p("To make it work, add the following lines into the ", i("project/AppBuild.scala"), " file:"),
      CodeBlock(
        """import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
          |import sbt.Keys._
          |import sbt._
          |
          |object AppBuild extends Build {
          |  val StaticFilesDir = "UdashStatic"
          |
          |  def copyIndex(file: File, to: File) = {
          |    val newFile = Path(to.toPath.toString + "/index.html")
          |    IO.copyFile(file, newFile.asFile)
          |  }
          |
          |  val compileStatics = taskKey[Seq[File]]("Frontend static files manager.")
          |  val copyStatics = taskKey[Unit]("Copy frontend static files into backend target.")
          |
          |  // Compile proper version of JS depending on build version.
          |  val compileStaticsForRelease = Def.taskDyn {
          |    val outDir = crossTarget.value / StaticFilesDir / "WebContent"
          |    if (!isSnapshot.value) {
          |      Def.task {
          |        val indexFile = sourceDirectory.value / s"main/assets/index.prod.html"
          |        copyIndex(indexFile, outDir)
          |        (fullOptJS in Compile).value
          |        (packageMinifiedJSDependencies in Compile).value
          |        (packageScalaJSLauncher in Compile).value
          |      }
          |    } else {
          |      Def.task {
          |        val indexFile = sourceDirectory.value / s"main/assets/index.dev.html"
          |        copyIndex(indexFile, outDir)
          |        (fastOptJS in Compile).value
          |        (packageJSDependencies in Compile).value
          |        (packageScalaJSLauncher in Compile).value
          |      }
          |    }
          |  }
          |}""".stripMargin
      )(GuideStyles),
      p(
        "It defines required custom SBT tasks. ", i("compileStaticsForRelease"), " compiles JS using fast or full optimization " +
          "depending on a build version. It also copies the ", i("index.html"), " file with proper links in the assets directory."
      ),
      p(
        "There is only the backend module left:"
      ),
      CodeBlock(
        """lazy val backend = project.in(file("backend"))
          |  .dependsOn(sharedJVM)
          |  .settings(commonSettings: _*).settings(
          |    crossLibs(Compile),
          |    libraryDependencies ++= backendDeps.value,
          |
          |    // mainClass in Compile := Some("..."),
          |
          |    compile <<= (compile in Compile).dependsOn(copyStatics),
          |    copyStatics := IO.copyDirectory(
          |      (crossTarget in frontend).value / StaticFilesDir,
          |      (target in Compile).value / StaticFilesDir
          |    ),
          |    copyStatics <<= copyStatics.dependsOn(compileStatics in frontend),
          |
          |    mappings in (Compile, packageBin) ++= {
          |      copyStatics.value
          |      ((target in Compile).value / StaticFilesDir).***.get map { file =>
          |        file -> file.getAbsolutePath.stripPrefix(
          |          (target in Compile).value.getAbsolutePath
          |        )
          |      }
          |    },
          |
          |    // Recompile when frontend files get changed, hint: ~compile in SBT
          |    watchSources ++= (sourceDirectory in frontend).value.***.get
          |  )""".stripMargin
      )(GuideStyles),
      p(
        "Notice that backend depends on ", i("sharedJVM"), " not ", i("sharedJs"), ". It also provides cross-compiled libraries " +
          "in JVM versions. Compilation of this module copies static files from the frontend module into the backend module target. " +
          "Now SBT configuration is complete."
      ),
      span(
        h3("Static files"),
        p(
          "In the ", i("frontend"), " module create the ", i("src/main/assets"), " directory. Inside it create two files: ",
          i("index.dev.html"), " and ", i("index.prod.html"), ", then fill them with the below bootstrapping html:"
        ),
        CodeBlock(
          """<!DOCTYPE html>
            |<html>
            |<head lang="en">
            |    <meta charset="UTF-8">
            |    <title>My Udash App</title>
            |    <script src="scripts/frontend-deps-fast.js"></script>
            |    <script src="scripts/frontend-impl-fast.js"></script>
            |    <script src="scripts/frontend-init.js"></script>
            |</head>
            |<body>
            |<div id="application"></div>
            |</body>
            |</html>""".stripMargin
        )(GuideStyles),
        p("It is recommended to use fully optimized ", i("frontend-deps.js"), " and ", i("frontend-impl.js"), " in the production version."),
        p("Remember to check if file names correspond to those in SBT configurations.")
      ),
      h2("What's next?"),
      p(
        "SBT configuration is ready, now it is time to prepare ", a(href := BootstrappingRpcState.url)("RPC interfaces"),
        " in the ", b("shared"), " module."
      )
    )
}