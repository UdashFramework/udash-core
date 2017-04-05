import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val versionOfScala = "2.12.1"
  val jettyVersion = "9.3.11.v20160721"

  val udashVersion = "0.5.0-SNAPSHOT"
  val udashJQueryVersion = "1.0.1"

  val scalaCssVersion = "0.5.3-RC1"

  val scalaLoggingVersion = "3.5.0"
  val logbackVersion = "1.2.3"

  val avsystemCommonsVersion = "1.20.0"
  val typesafeConfigVersion = "1.3.1"
  val springVersion = "4.3.7.RELEASE"
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
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % scalaCssVersion
  ))

  val frontendJSDeps = Def.setting(Seq(
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
