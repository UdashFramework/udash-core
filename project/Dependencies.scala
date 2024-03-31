import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin.autoImport.*
import org.scalajs.jsdependencies.sbtplugin.JSModuleID
import sbt.*
import sbt.Keys.scalaVersion

object Dependencies {
  val versionOfScala = "2.13.13" //update .github/workflows/ci.yml as well

  val jqueryWrapperVersion = "3.3.0"

  val scalaJsDomVersion = "2.8.0"
  val scalaTagsVersion = "0.12.0"
  val scalaCssVersion = "1.0.0"

  val servletVersion = "4.0.1"
  val avsCommonsVersion = "2.14.0"

  val atmosphereJSVersion = "3.1.3"
  val atmosphereVersion = "2.7.11"

  val upickleVersion = "3.2.0" // Tests only
  val circeVersion = "0.14.6" // Tests only
  val circeDerivationVersion = "0.13.0-M5" // Tests only
  val monixVersion = "3.4.1" // udash-rest only

  val sttpVersion = "3.9.5"

  val scalaLoggingVersion = "3.9.5"

  val jettyVersion = "10.0.20"
  val typesafeConfigVersion = "1.4.3"
  val flexmarkVersion = "0.64.8"
  val logbackVersion = "1.3.14"
  val janinoVersion = "3.1.12"
  val fontAwesomeVersion = "5.10.1"
  val svg4everybodyVersion = "2.1.9"

  val scalatestVersion = "3.2.18"
  val scalaJsSecureRandomVersion = "1.0.0" // Tests only
  val bootstrap4Version = "4.1.3"
  val bootstrap4DatepickerVersion = "5.39.0"
  val momentJsVersion = "2.29.4"

  val seleniumVersion = "4.19.1"
  val webDriverManagerVersion = "5.7.0"
  val scalaJsBenchmarkVersion = "0.10.0"

  val compilerPlugins = Def.setting(Seq(
    "com.avsystem.commons" %% "commons-analyzer" % avsCommonsVersion
  ).map(compilerPlugin))

  val commonTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion,
  ).map(_ % Test))

  val commonJsTestDeps = Def.setting(Seq(
    "org.scala-js" %%% "scalajs-fake-insecure-java-securerandom" % scalaJsSecureRandomVersion, //ScalaTest uses SecureRandom
  ).map(_ % Test))

  val macroDeps = Def.setting(Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion,
  ))

  val utilsCrossDeps = Def.setting(Seq(
    "com.avsystem.commons" %%% "commons-core" % avsCommonsVersion,
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
    "com.softwaremill.sttp.client3" %%% "monix" % sttpVersion,
    "io.monix" %%% "monix" % monixVersion,
    "io.circe" %%% "circe-core" % circeVersion % Test,
    "io.circe" %%% "circe-parser" % circeVersion % Test,
    "io.circe" %%% "circe-derivation" % circeDerivationVersion % Test,
  ))

  val restJvmDeps = Def.setting(restCrossDeps.value ++ Seq(
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

  private val momentResource = s"$momentJsVersion/moment.js"
  private val bootstrap4Resource = "js/bootstrap.bundle.js"

  val bootstrap4JsDeps = Def.setting(Seq[JSModuleID](
    "org.webjars" % "bootstrap" % bootstrap4Version / bootstrap4Resource
      minified "js/bootstrap.bundle.min.js" dependsOn "jquery.js",
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

    "org.eclipse.jetty" % "jetty-rewrite" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-javax-server" % jettyVersion,

    "com.typesafe" % "config" % typesafeConfigVersion,

    "com.vladsch.flexmark" % "flexmark-all" % flexmarkVersion,
  ))

  val seleniumDeps: Seq[ModuleID] = Seq(
    "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
    "io.github.bonigarcia" % "webdrivermanager" % webDriverManagerVersion,
  ).map(_ % Test)

  val guideJsDeps = Def.setting(Seq[JSModuleID](
    ProvidedJS / "prism.js",
  ))

  val guideFrontendDeps = Def.setting(Seq(
    "org.webjars" % "font-awesome" % fontAwesomeVersion,
  ))

  val homepageJsDeps = Def.setting(Seq[JSModuleID](
    "org.webjars.npm" % "svg4everybody" % svg4everybodyVersion / s"$svg4everybodyVersion/dist/svg4everybody.js",
    ProvidedJS / "prism.js",
  ))

}
