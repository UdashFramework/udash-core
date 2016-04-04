name := "udash-core"

version in ThisBuild := "0.1.1-SNAPSHOT"
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
  moduleName := "udash-core-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value
)

lazy val udash = project.in(file("."))
  .aggregate(macros, sharedJS, sharedJVM, frontend)
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
    emitSourceMaps in Compile := true,
    jsDependencies in Test += RuntimeDOM % Test,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage
  )

/** Project containing code compiled to JS only. */
lazy val frontend = project.in(file("frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(macros, sharedJS % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    crossLibs(Compile),
    emitSourceMaps in Compile := true,
    libraryDependencies ++= frontendDeps.value,
    jsDependencies += RuntimeDOM % Test,
    persistLauncher in Compile := true,
    publishedJS <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((fastOptJS in Compile).value) else Def.task((fullOptJS in Compile).value)
    },
    publishedJSDependencies <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((packageJSDependencies in Compile).value) else Def.task((packageMinifiedJSDependencies in Compile).value)
    },

    crossTestLibs(),
    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )