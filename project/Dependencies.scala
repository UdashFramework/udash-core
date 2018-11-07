import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys.scalaVersion
import sbt._

object Dependencies {
  val versionOfScala = "2.12.7"
  val silencerVersion = "1.2.1"

  val jqueryWrapperVersion = "3.0.0"
  val jqueryVersion = "3.3.1"

  val scalaJsDomVersion = "0.9.6"
  val scalaTagsVersion = "0.6.7"
  val scalaCssVersion = "0.5.5"

  val servletVersion = "3.1.0"
  val avsCommonsVersion = "1.34.0"

  val atmosphereJSVersion = "2.3.6"
  val atmosphereVersion = "2.4.30"

  val upickleVersion = "0.6.6" // Tests only
  val circeVersion = "0.9.3" // Tests only

  val sttpVersion = "1.3.4"

  val scalaLoggingVersion = "3.9.0"

  val jettyVersion = "9.4.11.v20180605" // Tests only

  val scalatestVersion = "3.0.5"
  val bootstrapVersion = "3.3.7"
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
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion
  ))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val macroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion,
  ))

  val utilsJvmDeps = Def.setting(Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  ))

  val utilsSjsDeps = Def.setting(Seq(
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

  val rpcJsDeps = Def.setting(Seq[(String, String)](
    "@mach25/atmosphere-javascript" -> atmosphereJSVersion
  ))

  private val restCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion,
    "com.softwaremill.sttp" %%% "core" % sttpVersion,
  ))

  val restJvmDeps = Def.setting(restCrossDeps.value ++ Seq(
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % sttpVersion,
    "javax.servlet" % "javax.servlet-api" % servletVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.eclipse.jetty" % "jetty-server" % jettyVersion % Test,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion % Test
  ))

  val restSjsDeps = restCrossDeps

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

  val bootstrapJsDeps = Def.setting(Seq[(String, String)](
    "bootstrap" -> bootstrapVersion,
    "eonasdan-bootstrap-datetimepicker" -> bootstrapDatepickerVersion,
  ))

  val bootstrap4SjsDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
  ))

  val bootstrap4JsDeps = Def.setting(Seq[(String, String)](
    "bootstrap" -> bootstrap4Version,
    "tempusdominus-bootstrap-4" -> bootstrap4DatepickerVersion,
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
