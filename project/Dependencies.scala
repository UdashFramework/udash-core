import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val versionOfScala = "2.12.4"
  val jettyVersion = "9.4.8.v20171121"

  val udashVersion = "0.6.1"
  val udashJQueryVersion = "1.1.0"
  val highchartsVersion = "5.0.10"

  val scalaLoggingVersion = "3.7.2"
  val logbackVersion = "1.2.3"

  val avsystemCommonsVersion = "1.25.5"
  val typesafeConfigVersion = "1.3.1"
  val springVersion = "4.3.8.RELEASE"

  val seleniumVersion = "3.8.1"
  val scalatestVersion = "3.0.3"

  val crossDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-shared" % udashVersion,
    "io.udash" %%% "udash-rest-shared" % udashVersion,
    "io.udash" %%% "udash-rpc-shared" % udashVersion,
    "io.udash" %%% "udash-i18n-shared" % udashVersion,
    "io.udash" %%% "udash-css-shared" % udashVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashVersion,
    "io.udash" %%% "udash-rpc-frontend" % udashVersion,
    "io.udash" %%% "udash-i18n-frontend" % udashVersion,
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
    "io.udash" %%% "udash-bootstrap" % udashVersion,
    "io.udash" %%% "udash-charts" % udashVersion,
    "io.udash" %%% "udash-css-frontend" % udashVersion
  ))

  val frontendJsDeps = Def.setting(Seq(
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
    "io.udash" %% "udash-rpc-backend" % udashVersion,
    "io.udash" %% "udash-rest-backend" % udashVersion,
    "io.udash" %% "udash-i18n-backend" % udashVersion,
    "io.udash" %% "udash-css-backend" % udashVersion,

    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,

    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
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
