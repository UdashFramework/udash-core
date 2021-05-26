import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin.autoImport._
import org.scalajs.jsdependencies.sbtplugin.JSModuleID
import sbt.Keys.scalaVersion
import sbt._

object Dependencies {
  val versionOfScala = "2.13.5" //update .github/workflows/ci.yml as well
  val collectionCompatVersion = "2.4.4"

  val jqueryWrapperVersion = "3.0.4"
  val jqueryVersion = "3.3.1"

  val scalaJsDomVersion = "1.1.0"
  val scalaTagsVersion = "0.9.4"
  val scalaCssVersion = "0.7.0"

  val servletVersion = "4.0.1"
  val avsCommonsVersion = "2.2.1"

  val atmosphereJSVersion = "3.0.4"
  val atmosphereVersion = "2.7.2"

  val upickleVersion = "1.3.15" // Tests only
  val circeVersion = "0.14.1" // Tests only
  val circeDerivationVersion = "0.13.0-M5" // Tests only
  val monixVersion = "3.4.0" // Tests only

  val sttpVersion = "2.2.9"

  val scalaLoggingVersion = "3.9.3"

  val jettyVersion = "9.4.41.v20210516"
  val typesafeConfigVersion = "1.4.1"
  val flexmarkVersion = "0.62.2"
  val logbackVersion = "1.2.3"
  val janinoVersion = "3.1.4"
  val fontAwesomeVersion = "5.10.1"
  val svg4everybodyVersion = "2.1.9"

  val scalatestVersion = "3.2.9"
  val bootstrapVersion = "3.3.7-1"
  val bootstrapDatepickerVersion = "4.17.47"
  val bootstrap4Version = "4.1.3"
  val bootstrap4DatepickerVersion = "5.1.2"
  val momentJsVersion = "2.24.0"

  val seleniumVersion = "3.141.59"
  val scalaJsBenchmarkVersion = "0.6.0"

  val compilerPlugins = Def.setting(Seq(
    "com.avsystem.commons" %% "commons-analyzer" % avsCommonsVersion
  ).map(compilerPlugin))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))

  val macroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion,
  ))

  val utilsCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion,
    "org.scala-lang.modules" %%% "scala-collection-compat" % collectionCompatVersion,
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
    "com.softwaremill.sttp.client" %%% "core" % sttpVersion,
    "io.monix" %%% "monix" % monixVersion % Test,
    "io.circe" %%% "circe-core" % circeVersion % Test,
    "io.circe" %%% "circe-parser" % circeVersion % Test,
    "io.circe" %%% "circe-derivation" % circeDerivationVersion % Test,
  ))

  val restJvmDeps = Def.setting(restCrossDeps.value ++ Seq(
    "com.softwaremill.sttp.client" %% "async-http-client-backend-future" % sttpVersion,
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

  val bootstrap4SjsDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % jqueryWrapperVersion,
  ))

  private val jqueryResource = s"$jqueryVersion/jquery.js"
  private val momentResource = s"$momentJsVersion/moment.js"
  private val bootstrap4Resource = "js/bootstrap.bundle.js"

  val bootstrap4JsDeps = Def.setting(Seq[JSModuleID](
    "org.webjars" % "jquery" % jqueryVersion / jqueryResource
      minified s"$jqueryVersion/jquery.min.js",
    "org.webjars" % "bootstrap" % bootstrap4Version / bootstrap4Resource
      minified "js/bootstrap.bundle.min.js" dependsOn jqueryResource,
    "org.webjars" % "momentjs" % s"$momentJsVersion" / momentResource minified s"$momentJsVersion/min/moment.min.js",
    "org.webjars" % "tempusdominus-bootstrap-4" % bootstrap4DatepickerVersion / "js/tempusdominus-bootstrap-4.js"
      minified "js/tempusdominus-bootstrap-4.min.js" dependsOn(bootstrap4Resource, momentResource)
  ))

  val benchmarksSjsDeps = Def.setting(Seq(
    "com.github.japgolly.scalajs-benchmark" %%% "benchmark" % scalaJsBenchmarkVersion,
    "io.circe" %%% "circe-core" % circeVersion,
    "io.circe" %%% "circe-generic" % circeVersion,
    "io.circe" %%% "circe-parser" % circeVersion,
    "com.lihaoyi" %%% "upickle" % upickleVersion,
  ))

  val backendDeps = Def.setting(Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.codehaus.janino" % "janino" % janinoVersion, //conditional processing in logback

    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-rewrite" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion,

    "com.typesafe" % "config" % typesafeConfigVersion,

    "com.vladsch.flexmark" % "flexmark-all" % flexmarkVersion,
    "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % Test,
  ))

  val seleniumJsDeps = Def.setting(Seq[JSModuleID]())

  val guideJsDeps = Def.setting(Seq[JSModuleID](
    ProvidedJS / "prism.js",
  ))

  val guideFrontendDeps = Def.setting(Seq(
    "org.webjars" % "font-awesome" % fontAwesomeVersion,
  ))

  val homepageJsDeps = Def.setting(Seq[JSModuleID](
    "org.webjars.npm" % "svg4everybody" % svg4everybodyVersion / s"$svg4everybodyVersion/dist/svg4everybody.js",
  ))

}
