import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {
  val versionOfScala = "2.11.8"
  val silencerVersion = "0.4"

  val jqueryWrapperVersion = "1.0.0"

  val scalaJsDomVersion = "0.9.1"
  val scalaTagsVersion = "0.6.0"

  val servletVersion = "3.1.0"
  val avsCommonsVersion = "1.17.0"

  val atmosphereJSVersion = "2.3.0"
  val atmosphereVersion = "2.4.5"

  val upickleVersion = "0.4.2"
  val jawnParserVersion = "0.9.0"

  val scalaHttpClientVersion = "1.1.0"

  val scalaLoggingVersion = "3.4.0"

  val scalatestVersion = "3.0.0"
  val bootstrapVersion = "3.3.7"

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
    "org.webjars" % "atmosphere-javascript" % atmosphereJSVersion / s"$atmosphereJSVersion/atmosphere.js"
  ))

  val rpcSharedJVMDeps = Def.setting(Seq(
    "org.spire-math" %% "jawn-parser" % jawnParserVersion
  ))

  val rpcBackendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmosphereVersion
  ))

  val restCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-shared" % avsCommonsVersion,
    "fr.hmil" %%% "roshttp" % scalaHttpClientVersion
  ))

  val bootstrapFrontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion
  ))

  val bootstrapFrontendJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
  ))
}
