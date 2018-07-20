import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys.scalaVersion
import sbt._

object Dependencies {
  val versionOfScala = "2.12.6"
  val silencerVersion = "1.0"

  val jqueryWrapperVersion = "1.2.0"

  val scalaJsDomVersion = "0.9.6"
  val scalaTagsVersion = "0.6.7"
  val scalaCssVersion = "0.5.5"

  val servletVersion = "3.1.0"
  val avsCommonsVersion = "1.28.1"

  val atmosphereJSVersion = "2.3.5"
  val atmosphereVersion = "2.4.24"

  val upickleVersion = "0.6.6" // Tests only
  val circeVersion = "0.9.3" // Tests only

  val scalaHttpClientVersion = "2.1.0"

  val scalaLoggingVersion = "3.9.0"

  val jettyVersion = "9.4.11.v20180605" // Tests only

  val scalatestVersion = "3.0.5"
  val bootstrapVersion = "3.3.7-1"
  val bootstrapDatepickerVersion = "4.17.47"
  val bootstrap4Version = "4.1.1"
  val momentJsVersion = "2.22.0"

  val scalaJsBenchmarkVersion = "0.2.5"

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" %% "silencer-plugin" % silencerVersion,
    "com.avsystem.commons" %% "commons-analyzer" % avsCommonsVersion
  ).map(compilerPlugin))

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" %% "silencer-lib" % silencerVersion,
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion
  ))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val coreMacroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ))

  val coreCrossDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  ))

  val coreCrossJVMDeps = Def.setting(Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  ))

  val coreFrontendDeps = Def.setting(Seq(
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion % Test
  ))

  val rpcCrossTestDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "upickle" % upickleVersion,
    "io.circe" %%% "circe-core" % circeVersion,
    "io.circe" %%% "circe-parser" % circeVersion,
  ).map(_ % Test))

  val rpcFrontendJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmosphereJSVersion / s"$atmosphereJSVersion/atmosphere.js" minified s"$atmosphereJSVersion/atmosphere-min.js"
  ))

  val rpcBackendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmosphereVersion
  ))

  val restMacroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion
  ))

  val restCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion,
    "fr.hmil" %%% "roshttp" % scalaHttpClientVersion
  ))

  val restBackendDeps = Def.setting(Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.eclipse.jetty" % "jetty-server" % jettyVersion % Test,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion % Test
  ))

  val cssMacroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion
  ))

  val cssFrontendDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  ))

  val bootstrapFrontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion exclude("org.webjars", "momentjs")
  ))

  val bootstrapFrontendJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    "org.webjars.bower" % "momentjs" % s"$momentJsVersion" / s"$momentJsVersion/min/moment-with-locales.js" minified s"$momentJsVersion/min/moment-with-locales.min.js",
    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion / s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.js"
      minified s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.min.js" dependsOn "bootstrap.js" dependsOn "moment-with-locales.js"
  ))

  val bootstrap4FrontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
  ))

  val bootstrap4FrontendJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "bootstrap" % bootstrap4Version / "bootstrap.bundle.js" minified "bootstrap.bundle.min.js" dependsOn "jquery.js",
    "org.webjars.bower" % "momentjs" % s"$momentJsVersion" / s"$momentJsVersion/min/moment-with-locales.js" minified s"$momentJsVersion/min/moment-with-locales.min.js",
  ))

  val chartsFrontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion
  ))

  val benchmarksFrontendDeps = Def.setting(Seq(
    "com.github.japgolly.scalajs-benchmark" %%% "benchmark" % scalaJsBenchmarkVersion,
    "io.circe" %%% "circe-core" % circeVersion,
    "io.circe" %%% "circe-generic" % circeVersion,
    "io.circe" %%% "circe-parser" % circeVersion,
    "com.lihaoyi" %%% "upickle" % upickleVersion,
  ))
}
