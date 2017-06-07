import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val versionOfScala = "2.12.2"
  val jettyVersion = "9.3.11.v20160721"

  val udashVersion = "0.5.0"
  val udashJQueryVersion = "1.0.1"
  val highchartsVarsion = "5.0.10"

  val scalaCssVersion = "0.5.3"

  val scalaLoggingVersion = "3.5.0"
  val logbackVersion = "1.2.3"

  val avsystemCommonsVersion = "1.20.3"
  val typesafeConfigVersion = "1.3.1"
  val springVersion = "4.3.8.RELEASE"
  val akkaVersion = "2.4.17"
  val akkaHttpVersion = "10.0.5"
  val akkaHttpCorsVersion = "0.2.1"

  val seleniumVersion = "2.53.1"
  val scalatestVersion = "3.0.1"

  val crossDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-shared" % udashVersion,
    "io.udash" %%% "udash-rest-shared" % udashVersion,
    "io.udash" %%% "udash-rpc-shared" % udashVersion,
    "io.udash" %%% "udash-i18n-shared" % udashVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashVersion,
    "io.udash" %%% "udash-rpc-frontend" % udashVersion,
    "io.udash" %%% "udash-i18n-frontend" % udashVersion,
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
    "io.udash" %%% "udash-bootstrap" % udashVersion,
    "io.udash" %%% "udash-charts" % udashVersion,
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % scalaCssVersion
  ))

  val frontendJSDeps = Def.setting(Seq(
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/highcharts.src.js" minified s"$highchartsVarsion/highcharts.js" dependsOn "jquery.js",
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/highcharts-3d.src.js" minified s"$highchartsVarsion/highcharts-3d.js" dependsOn s"$highchartsVarsion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/highcharts-more.src.js" minified s"$highchartsVarsion/highcharts-more.js" dependsOn s"$highchartsVarsion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/modules/exporting.src.js" minified s"$highchartsVarsion/modules/exporting.js" dependsOn s"$highchartsVarsion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/modules/drilldown.src.js" minified s"$highchartsVarsion/modules/drilldown.js" dependsOn s"$highchartsVarsion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVarsion / s"$highchartsVarsion/modules/heatmap.src.js" minified s"$highchartsVarsion/modules/heatmap.js" dependsOn s"$highchartsVarsion/highcharts.src.js"
  ))

  val homepageJSDeps = Def.setting(Seq(
    ProvidedJS / "jquery.mCustomScrollbar.concat.min.js"
  ))

  val backendDeps = Def.setting(Seq(
    "io.udash" %% "udash-rpc-backend" % udashVersion,
    "io.udash" %% "udash-i18n-backend" % udashVersion,

    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,

    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion,

    "com.typesafe" % "config" % typesafeConfigVersion,
    "org.springframework" % "spring-beans" % springVersion,
    "com.avsystem.commons" %% "commons-spring" % avsystemCommonsVersion,

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "ch.megard" %% "akka-http-cors" % akkaHttpCorsVersion
  ))

  val seleniumDeps = Def.setting(Seq(
    "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion
  ))

  val testDeps = Def.setting(Seq(
    /* Tests */
    "org.scalatest" %% "scalatest" % scalatestVersion
  ).map(_ % Test))
}
