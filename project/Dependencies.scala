import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {
  val versionOfScala = "2.11.8"
  val silencerVersion = "0.3"

  val jqueryWrapperVersion = "1.0.0-rc.2"

  val scalaJsDomVersion = "0.9.0"
  val scalaTagsVersion = "0.5.4"

  val servletVersion = "3.1.0"
  val avsCommonsVersion = "1.14.0"

  val atmoshereJSVersion = "2.3.0"
  val atmoshereVersion = "2.4.3"

  val upickleVersion = "0.3.9"
  val jawnParserVersion = "0.8.4"

  val scalaLoggingVersion = "3.1.0"

  val scalatestVersion = "3.0.0-M15"
  val scalamockVersion = "3.2.2"

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" % "silencer-plugin" % silencerVersion
  ).map(compilerPlugin))

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" % "silencer-lib" % silencerVersion
  ))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val coreCrossDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  ))

  val coreFrontendDeps = Def.setting(Seq(
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion % Test
  ))

  val rpcCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-shared" % avsCommonsVersion
  ))

  val rpcCrossTestDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "upickle" % upickleVersion
  ).map(_ % Test))

  val rpcFrontendJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmoshereJSVersion / s"$atmoshereJSVersion/atmosphere.js"
  ))

  val rpcSharedJVMDeps = Def.setting(Seq(
    "org.spire-math" %% "jawn-parser" % jawnParserVersion
  ))

  val rpcBackendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmoshereVersion
  ))

  val rpcBackendTestDeps = Def.setting(Seq(
    "org.scalamock" %% "scalamock-scalatest-support" % scalamockVersion
  ).map(_ % Test))
}