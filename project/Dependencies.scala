import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val versionOfScala = "2.12.8"
  val jettyVersion = "9.4.14.v20181114"

  val udashVersion = "0.8.0-M11"
  val udashJQueryVersion = "2.0.1"
  val jqueryVersion = "3.3.1"
  val highchartsVersion = "5.0.14"

  val scalaLoggingVersion = "3.9.2"
  val logbackVersion = "1.2.3"

  val avsystemCommonsVersion = "1.34.7"
  val typesafeConfigVersion = "1.3.3"
  val springVersion = "4.3.22.RELEASE"

  val seleniumVersion = "3.12.0"
  val scalatestVersion = "3.0.5"

  val crossDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core" % udashVersion,
    "io.udash" %%% "udash-rest" % udashVersion,
    "io.udash" %%% "udash-rpc" % udashVersion,
    "io.udash" %%% "udash-i18n" % udashVersion,
    "io.udash" %%% "udash-css" % udashVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
    "io.udash" %%% "udash-bootstrap4" % udashVersion,
    "io.udash" %%% "udash-charts" % udashVersion,
  ))

  val frontendJsDeps = Def.setting(Seq(
    "org.webjars" % "jquery" % jqueryVersion / s"$jqueryVersion/jquery.js" minified s"$jqueryVersion/jquery.min.js",

    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/highcharts.src.js"
      minified s"$highchartsVersion/highcharts.js" dependsOn "jquery.js",
    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/highcharts-3d.src.js"
      minified s"$highchartsVersion/highcharts-3d.js" dependsOn s"$highchartsVersion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/highcharts-more.src.js"
      minified s"$highchartsVersion/highcharts-more.js" dependsOn s"$highchartsVersion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/modules/exporting.src.js"
      minified s"$highchartsVersion/modules/exporting.js" dependsOn s"$highchartsVersion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/modules/drilldown.src.js"
      minified s"$highchartsVersion/modules/drilldown.js" dependsOn s"$highchartsVersion/highcharts.src.js",
    "org.webjars" % "highcharts" % highchartsVersion / s"$highchartsVersion/modules/heatmap.src.js"
      minified s"$highchartsVersion/modules/heatmap.js" dependsOn s"$highchartsVersion/highcharts.src.js"
  ))

  val guideJsDeps = Def.setting(Seq.empty[org.scalajs.sbtplugin.JSModuleID])

  val homepageJsDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    ProvidedJS / "jquery.mCustomScrollbar.concat.min.js"
  ))

  val backendDeps = Def.setting(Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,

    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-rewrite" % jettyVersion,
    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion,

    "com.typesafe" % "config" % typesafeConfigVersion,
    "org.springframework" % "spring-beans" % springVersion,
    "com.avsystem.commons" %% "commons-spring" % avsystemCommonsVersion
  ))

  val seleniumDeps = Def.setting(Seq(
    "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion
  ))

  val testDeps = Def.setting(Seq(
    /* Tests */
    "org.scalatest" %% "scalatest" % scalatestVersion
  ).map(_ % Test))
}
