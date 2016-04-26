name := "udash-i18n"

version in ThisBuild := "0.2.0-rc.1"
scalaVersion in ThisBuild := "2.11.7"
organization in ThisBuild := "io.udash"
crossPaths in ThisBuild := false
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
  "-Xlint:_,-missing-interpolator,-adapted-args"
)

val commonSettings = Seq(
  moduleName := "udash-i18n-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value
)

lazy val udash = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, frontend, backend)
  .settings(publishArtifact := false)

def crossLibs(configuration: Configuration) = {
  libraryDependencies ++= crossDeps.value.map(_ % configuration)
}

def crossTestLibs() = {
  libraryDependencies ++= crossTestDeps.value
}

/** Cross project containing code compiled to both JS and JVM. */
lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
  .settings(commonSettings: _*).settings(
    crossLibs(Provided),
    crossTestLibs()
  )
  .jsSettings(
    jsDependencies in Test += RuntimeDOM % Test,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage,
    emitSourceMaps in Test := true
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

/** Project containing code compiled to JVM only. */
lazy val backend = project.in(file("backend"))
  .dependsOn(sharedJVM % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    crossLibs(Compile),
    crossTestLibs()
  )

/** Project containing code compiled to JS only. */
lazy val frontend = project.in(file("frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    crossLibs(Compile),
    libraryDependencies ++= frontendDeps.value,
    jsDependencies += RuntimeDOM % Test,

    crossTestLibs(),
    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false,
    emitSourceMaps in Test := true
  )
