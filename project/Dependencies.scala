import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val versionOfScala = "2.11.7"
  val servletVersion = "3.1.0"
  val silencerVersion = "0.3"

  val udashCoreVersion = "0.1.0"

  val atmoshereJSVersion = "2.2.11"

  val upickleVersion = "0.3.8"
  val scalaJsDomVersion = "0.9.0"
  val atmoshereVersion = "2.4.0.1"
  val scalaLoggingVersion = "3.1.0"

  val scalatestVersion = "3.0.0-M15"
  val scalamockVersion = "3.2.2"

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" % "silencer-lib" % silencerVersion
  ))

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" % "silencer-plugin" % silencerVersion
  ).map(compilerPlugin))

  val crossDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "upickle" % upickleVersion,
    "io.udash" % "udash-core-shared" % udashCoreVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashCoreVersion,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion
  ))

  val frontendJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmoshereJSVersion / s"$atmoshereJSVersion/atmosphere.js"
  ))

  val backendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmoshereVersion
  ))

  val crossTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val backendTestDeps = Def.setting(Seq(
    "org.scalamock" %% "scalamock-scalatest-support" % scalamockVersion
  ).map(_ % Test))
}