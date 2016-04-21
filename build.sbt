name := "udash-guide"

version in ThisBuild := "0.1.0"
scalaVersion in ThisBuild := versionOfScala
organization in ThisBuild := "io.udash"
crossPaths in ThisBuild := false
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
  "-Xlint:_,-missing-interpolator,-adapted-args"
)

val commonSettings = Seq(
  moduleName := "udash-guide-" + moduleName.value,
  (publishArtifact in packageDoc) := false
)

lazy val udashGuide = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, frontend, backend)
    .dependsOn(backend)
  .settings(
    publishArtifact := false,
    mainClass in Compile := Some("io.udash.guide.Launcher")
  )

def crossLibs(configuration: Configuration) =
  libraryDependencies ++= crossDeps.value.map(_ % configuration)

/** Cross project containing code compiled to both JS and JVM.
  */
lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
  .settings(commonSettings: _*).settings(
    crossLibs(Provided)
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

/** Project containing code compiled to JVM only.
  */
lazy val backend = project.in(file("backend"))
  .dependsOn(sharedJVM)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= backendDeps.value,
    crossLibs(Compile),

    (compile in Compile) <<= (compile in Compile).dependsOn(copyStatics),
    copyStatics := IO.copyDirectory((crossTarget in frontend).value / StaticFilesDir, (target in Compile).value / StaticFilesDir),
    copyStatics <<= copyStatics.dependsOn(compileStatics in frontend),

    mappings in (Compile, packageBin) ++= {
      copyStatics.value
      ((target in Compile).value / StaticFilesDir).***.get map { file =>
        file -> file.getAbsolutePath.stripPrefix((target in Compile).value.getAbsolutePath)
      }
    },

    watchSources ++= (sourceDirectory in frontend).value.***.get,

    assemblyJarName in assembly := "udash-guide.jar",
    mainClass in assembly := Some("io.udash.guide.Launcher")
  )

/** Project containing code compiled to JS only.
  */
lazy val frontend = project.in(file("frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= frontendDeps.value,
    crossLibs(Compile),
    jsDependencies ++= frontendJSDeps.value,
    persistLauncher in Compile := true,

    unmanagedJars in Compile ++= {
      val base = baseDirectory.value / "libs"
      val customJars = (base ** "*.jar")
      customJars.classpath
    },

    compileStatics := {
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", crossTarget.value / StaticFilesDir / "WebContent/assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/pdf", crossTarget.value / StaticFilesDir / "WebContent/assets/pdf")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", crossTarget.value / StaticFilesDir / "WebContent/assets/images")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg", crossTarget.value / StaticFilesDir / "WebContent/assets/svg")
      IO.copyDirectory(sourceDirectory.value / "main/assets/prism", crossTarget.value / StaticFilesDir / "WebContent/assets/prism")
      compileStaticsForRelease.value
      (crossTarget.value / StaticFilesDir).***.get
    },

    artifactPath in(Compile, fastOptJS) :=
      (crossTarget in(Compile, fastOptJS)).value / StaticFilesDir / "WebContent" / "scripts" / "frontend-impl-fast.js",
    artifactPath in(Compile, fullOptJS) :=
      (crossTarget in(Compile, fullOptJS)).value / StaticFilesDir / "WebContent" / "scripts" / "frontend-impl.js",
    artifactPath in(Compile, packageJSDependencies) :=
      (crossTarget in(Compile, packageJSDependencies)).value / StaticFilesDir / "WebContent" / "scripts" / "frontend-deps-fast.js",
    artifactPath in(Compile, packageMinifiedJSDependencies) :=
      (crossTarget in(Compile, packageMinifiedJSDependencies)).value / StaticFilesDir / "WebContent" / "scripts" / "frontend-deps.js",
    artifactPath in(Compile, packageScalaJSLauncher) :=
      (crossTarget in(Compile, packageScalaJSLauncher)).value / StaticFilesDir / "WebContent" / "scripts" / "frontend-init.js",

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )

/** Project containg Selenium tests of application
  */
lazy val selenium = project.in(file("selenium"))
  .dependsOn(backend)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= seleniumDeps.value,
    libraryDependencies ++= testDeps.value,

    parallelExecution := false
  )