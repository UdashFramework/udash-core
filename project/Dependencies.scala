import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val servletVersion = "3.1.0"

  val silencerVersion = "0.3"
  val avsCommonsVersion = "1.14.0"

  val udashCoreVersion = "0.2.0-rc.1"

  val atmoshereJSVersion = "2.3.0"

  val upickleVersion = "0.3.9"
  val atmoshereVersion = "2.4.3"
  val scalaLoggingVersion = "3.1.0"

  val jawnParserVersion = "0.8.4"

  val scalatestVersion = "3.0.0-M15"
  val scalamockVersion = "3.2.2"

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" % "silencer-lib" % silencerVersion
  ))

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" % "silencer-plugin" % silencerVersion
  ).map(compilerPlugin))

  val crossDeps = Def.setting(Seq(
    "io.udash" % "udash-core-shared" % udashCoreVersion,
    "com.avsystem.commons" %%% "commons-shared" % avsCommonsVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashCoreVersion
  ))

  val frontendJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmoshereJSVersion / s"$atmoshereJSVersion/atmosphere.js"
  ))

  val sharedJVMDeps = Def.setting(Seq(
    "org.spire-math" %% "jawn-parser" % jawnParserVersion
  ))

  val backendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmoshereVersion
  ))

  val crossTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion,
    "com.lihaoyi" %%% "upickle" % upickleVersion
  ).map(_ % Test))

  val backendTestDeps = Def.setting(Seq(
    "org.scalamock" %% "scalamock-scalatest-support" % scalamockVersion
  ).map(_ % Test))
}