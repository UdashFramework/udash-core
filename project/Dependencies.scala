import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {
  val versionOfScala = "2.12.2"
  val silencerVersion = "0.5"

  val jqueryWrapperVersion = "1.0.1"

  val scalaJsDomVersion = "0.9.2"
  val scalaTagsVersion = "0.6.5"

  val servletVersion = "3.1.0"
  val avsCommonsVersion = "1.20.6"

  val atmosphereJSVersion = "2.3.2"
  val atmosphereVersion = "2.4.12"

  val upickleVersion = "0.4.4"
  val jawnParserVersion = "0.10.4"

  val scalaHttpClientVersion = "2.0.1"

  val scalaLoggingVersion = "3.5.0"

  val scalatestVersion = "3.0.3"
  val bootstrapVersion = "3.3.7-1"
  val bootstrapDatepickerVersion = "4.17.43"
  val momentJsVersion = "2.18.1"

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" %% "silencer-plugin" % silencerVersion
  ).map(compilerPlugin))

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" %% "silencer-lib" % silencerVersion,
    "com.avsystem.commons" %%% "commons-shared" % avsCommonsVersion
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

  val rpcCrossTestDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "upickle" % upickleVersion
  ).map(_ % Test))

  val rpcFrontendJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmosphereJSVersion / s"$atmosphereJSVersion/atmosphere.js" minified s"$atmosphereJSVersion/atmosphere-min.js"
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
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion exclude ("org.webjars", "momentjs")
  ))

  val bootstrapFrontendJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    "org.webjars.bower" % "momentjs" % s"$momentJsVersion" / s"$momentJsVersion/min/moment-with-locales.js" minified s"$momentJsVersion/min/moment-with-locales.min.js",
    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion / s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.js"
      minified s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.min.js" dependsOn "bootstrap.js" dependsOn "moment-with-locales.js"
  ))

  val chartsFrontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion
  ))
}
