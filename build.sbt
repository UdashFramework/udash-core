name := "udash-rpc"

version in ThisBuild := "0.1.0"
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
  moduleName := "udash-rpc-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value
)

lazy val udash = project.in(file("."))
  .aggregate(macros, sharedJS, sharedJVM, frontend, backend)
  .settings(publishArtifact := false)

/** Project containing implementations of macros. Macros can be used in both JS and JVM code. */
lazy val macros = project.in(file("macros"))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )

def crossLibs(configuration: Configuration) = {
  libraryDependencies ++= crossDeps.value.map(_ % configuration)
}

def crossTestLibs() = {
  libraryDependencies ++= crossTestDeps.value
}

/** Cross project containing code compiled to both JS and JVM. */
lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
  .jsConfigure(_.dependsOn(macros))
  .jvmConfigure(_.dependsOn(macros))
  .settings(commonSettings: _*).settings(
    crossLibs(Provided),
    crossTestLibs()
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js.enablePlugins(ScalaJSPlugin)
  .settings(
    jsDependencies in Test += RuntimeDOM % Test,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage
  )

/** Project containing code compiled to JVM only. */
lazy val backend = project.in(file("backend"))
  .dependsOn(sharedJVM % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    crossLibs(Compile),
    crossTestLibs(),
    libraryDependencies ++= backendDeps.value,
    libraryDependencies ++= backendTestDeps.value
  )

/** Project containing code compiled to JS only. */
lazy val frontend = project.in(file("frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    crossLibs(Compile),
    libraryDependencies ++= frontendDeps.value,
    jsDependencies ++= frontendJsDeps.value,
    jsDependencies += RuntimeDOM % Test,

    crossTestLibs(),
    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )