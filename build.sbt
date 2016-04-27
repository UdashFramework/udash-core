name := "udash"

version in ThisBuild := "0.2.0"
scalaVersion in ThisBuild := versionOfScala
organization in ThisBuild := "io.udash"
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
  moduleName := "udash-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value
)

lazy val udash = project.in(file("."))
  .aggregate(`core-macros`, `core-shared-JS`, `core-shared-JVM`, `core-frontend`)
  .settings(publishArtifact := false)

/** Project containing implementations of macros. Macros can be used in both JS and JVM code. */
lazy val `core-macros` = project.in(file("core/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
  )

lazy val `core-shared` = crossProject.crossType(CrossType.Pure).in(file("core/shared"))
  .jsConfigure(_.dependsOn(`core-macros`))
  .jvmConfigure(_.dependsOn(`core-macros`))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= coreCrossDeps.value,
    libraryDependencies ++= commonTestDeps.value
  )
  .jsSettings(
    emitSourceMaps in Compile := true,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage,
    jsDependencies in Test += RuntimeDOM % Test
  )

lazy val `core-shared-JVM` = `core-shared`.jvm
lazy val `core-shared-JS` = `core-shared`.js

lazy val `core-frontend` = project.in(file("core/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    emitSourceMaps in Compile := true,
    libraryDependencies ++= coreFrontendDeps.value,
    jsDependencies += RuntimeDOM % Test,
    persistLauncher in Compile := true,
    publishedJS <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((fastOptJS in Compile).value) else Def.task((fullOptJS in Compile).value)
    },
    publishedJSDependencies <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((packageJSDependencies in Compile).value) else Def.task((packageMinifiedJSDependencies in Compile).value)
    },

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )