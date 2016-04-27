name := "udash-i18n"

version in ThisBuild := "0.2.0-rc.1"
scalaVersion in ThisBuild := "2.11.7"
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

externalResolvers in ThisBuild := Seq(
  Resolver.file("local", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)
)

val commonSettings = Seq(
  moduleName := "udash-i18n-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value
)

lazy val udash = project.in(file("."))
  .aggregate(`i18n-shared-JS`, `i18n-shared-JVM`, `i18n-frontend`, `i18n-backend`)
  .settings(publishArtifact := false)

lazy val `i18n-shared` = crossProject.crossType(CrossType.Pure).in(file("i18n/shared"))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= crossDeps.value,
    libraryDependencies ++= crossTestDeps.value
  )
  .jsSettings(
    jsDependencies in Test += RuntimeDOM % Test,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage,
    emitSourceMaps in Test := true
  )

lazy val `i18n-shared-JVM` = `i18n-shared`.jvm
lazy val `i18n-shared-JS` = `i18n-shared`.js

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared-JVM` % "test->test;compile->compile")
  .settings(commonSettings: _*)

lazy val `i18n-frontend` = project.in(file("i18n/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= frontendDeps.value,
    jsDependencies += RuntimeDOM % Test,

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false,
    emitSourceMaps in Test := true
  )
