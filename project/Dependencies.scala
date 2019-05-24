import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys.scalaVersion
import sbt._

object Dependencies {
  val versionOfScala = "2.12.8"
  val silencerVersion = "1.3.3"

  val jqueryWrapperVersion = "3.0.1"
  val jqueryVersion = "3.3.1"

  val scalaJsDomVersion = "0.9.7"
  val scalaTagsVersion = "0.6.8"
  val scalaCssVersion = "0.5.6"

  val servletVersion = "4.0.1"
  val avsCommonsVersion = "1.34.19"

  val atmosphereJSVersion = "2.3.8"
  val atmosphereVersion = "2.5.3"

  val upickleVersion = "0.7.4" // Tests only
  val circeVersion = "0.11.1" // Tests only
  val circeDerivationVersion = "0.12.0-M1" // Tests only
  val monixVersion = "3.0.0-RC2" // Tests only

  val sttpVersion = "1.5.16"

  val scalaLoggingVersion = "3.9.0"

  val jettyVersion = "9.4.18.v20190429" // Tests only

  val scalatestVersion = "3.0.7"
  val bootstrapVersion = "3.3.7-1"
  val bootstrapDatepickerVersion = "4.17.47"
  val bootstrap4Version = "4.1.3"
  val bootstrap4DatepickerVersion = "5.1.2"
  val momentJsVersion = "2.22.2"

  val seleniumVersion = "3.12.0"
  val scalaJsBenchmarkVersion = "0.2.5"

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" %% "silencer-plugin" % silencerVersion,
    "com.avsystem.commons" %% "commons-analyzer" % avsCommonsVersion
  ).map(compilerPlugin))

  val commonDeps = Def.setting(Seq(
    "com.github.ghik" %% "silencer-lib" % silencerVersion % Provided,
  ))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val macroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion,
  ))

  val utilsCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion
  ))

  val utilsJvmDeps = Def.setting(utilsCrossDeps.value ++ Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  ))

  val utilsSjsDeps = Def.setting(utilsCrossDeps.value ++ Seq(
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
  ))

  private val coreCrossDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  ))

  val coreJvmDeps = coreCrossDeps

  val coreSjsDeps = coreCrossDeps

  private val rpcCrossDeps = Def.setting(Seq(
    "com.lihaoyi" %%% "upickle" % upickleVersion % Test,
    "io.circe" %%% "circe-core" % circeVersion % Test,
    "io.circe" %%% "circe-parser" % circeVersion % Test,
  ))

  val rpcJvmDeps = Def.setting(rpcCrossDeps.value ++ Seq(
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "org.atmosphere" % "atmosphere-runtime" % atmosphereVersion
  ))

  val rpcSjsDeps = rpcCrossDeps

  val rpcJsDeps = Def.setting(Seq(
    "org.webjars" % "atmosphere-javascript" % atmosphereJSVersion / s"$atmosphereJSVersion/atmosphere.js"
      minified s"$atmosphereJSVersion/atmosphere-min.js"
  ))

  private val restCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion,
    "com.softwaremill.sttp" %%% "core" % sttpVersion,
    "io.monix" %%% "monix" % monixVersion % Test,
    "io.circe" %%% "circe-core" % circeVersion % Test,
    "io.circe" %%% "circe-parser" % circeVersion % Test,
    "io.circe" %%% "circe-derivation" % circeDerivationVersion % Test,
  ))

  val restJvmDeps = Def.setting(restCrossDeps.value ++ Seq(
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % sttpVersion,
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.eclipse.jetty" % "jetty-server" % jettyVersion % Test,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion % Test
  ))

  val restSjsDeps = restCrossDeps
  
  val restJettyDeps = Def.setting(Seq(
    "org.eclipse.jetty" % "jetty-client" % jettyVersion
  ))

  private val cssCrossDeps = Def.setting(Seq(
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
  ))

  val cssJvmDeps = cssCrossDeps

  val cssSjsDeps = Def.setting(cssCrossDeps.value ++ Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,
  ))

  val bootstrapSjsDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion exclude("org.webjars", "momentjs")
  ))

  val bootstrapJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "jquery" % jqueryVersion / s"$jqueryVersion/jquery.js"
      minified s"$jqueryVersion/jquery.min.js",
    "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js"
      minified "bootstrap.min.js" dependsOn "jquery.js",
    "org.webjars.bower" % "momentjs" % s"$momentJsVersion" / s"$momentJsVersion/min/moment-with-locales.js"
      minified s"$momentJsVersion/min/moment-with-locales.min.js",

    "org.webjars" % "Eonasdan-bootstrap-datetimepicker" % bootstrapDatepickerVersion /
      s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.js"
      minified s"$bootstrapDatepickerVersion/js/bootstrap-datetimepicker.min.js"
      dependsOn "bootstrap.js" dependsOn s"$momentJsVersion/min/moment-with-locales.js",
  ))

  val bootstrap4SjsDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
  ))

  val bootstrap4JsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "jquery" % jqueryVersion / s"$jqueryVersion/jquery.js"
      minified s"$jqueryVersion/jquery.min.js",
    "org.webjars" % "bootstrap" % bootstrap4Version / "js/bootstrap.bundle.js"
      minified "js/bootstrap.bundle.min.js" dependsOn "jquery.js",
    "org.webjars.bower" % "momentjs" % s"$momentJsVersion" / s"$momentJsVersion/min/moment-with-locales.js"
      minified s"$momentJsVersion/min/moment-with-locales.min.js",
    "org.webjars" % "tempusdominus-bootstrap-4" % bootstrap4DatepickerVersion / "js/tempusdominus-bootstrap-4.js"
      minified "js/tempusdominus-bootstrap-4.min.js" dependsOn "bootstrap.bundle.js" dependsOn "moment-with-locales.js"
  ))

  val chartsSjsDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion
  ))

  val benchmarksSjsDeps = Def.setting(Seq(
    "com.github.japgolly.scalajs-benchmark" %%% "benchmark" % scalaJsBenchmarkVersion,
    "io.circe" %%% "circe-core" % circeVersion,
    "io.circe" %%% "circe-generic" % circeVersion,
    "io.circe" %%% "circe-parser" % circeVersion,
    "com.lihaoyi" %%% "upickle" % upickleVersion,
  ))

  val seleniumJvmDeps = Def.setting(Seq(
    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
    "org.eclipse.jetty" % "jetty-rewrite" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion,
    "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
    "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % Test,
  ))
}
