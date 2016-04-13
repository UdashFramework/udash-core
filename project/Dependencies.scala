import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val versionOfScala = "2.11.8"
  val jettyVersion = "9.3.8.v20160314"

  val udashCoreVersion = "0.2.0-rc.2"
  val udashRpcVersion = "0.2.0-rc.2"
  val udashJQueryVersion = "1.0.0-rc.2"

//  val scalaTagsVersion = "0.5.4-avs.1"
  val scalaCssVersion = "0.4.1"

  val scalaLoggingVersion = "3.1.0"
  val logbackVersion = "1.1.3"

  val avsystemCommonsVersion = "1.14.0"
  val typesafeConfigVersion = "1.3.0"
  val springVersion = "4.2.5.RELEASE"

  val bootstrapVersion = "3.3.1-1"

  val seleniumVersion = "2.53.0"
  val scalatestVersion = "3.0.0-M15"
  val scalamockVersion = "3.2.2"

  val crossDeps = Def.setting(Seq(
    "io.udash" % "udash-core-shared" % udashCoreVersion,
    "io.udash" % "udash-rpc-shared" % udashRpcVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashCoreVersion,
    "io.udash" %%% "udash-rpc-frontend" % udashRpcVersion,
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
//    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion exclude("org.scala-js", "scalajs-dom_sjs0.6_2.11"),
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % scalaCssVersion
  ))

  val frontendJSDeps = Def.setting(Seq(
    "org.webjars" % "bootstrap-sass" % bootstrapVersion / "3.3.1/javascripts/bootstrap.js" dependsOn "jquery.js"
  ))

  val backendDeps = Def.setting(Seq(
    "io.udash" % "udash-rpc-backend" % udashRpcVersion,

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
    "org.scalatest" %% "scalatest" % scalatestVersion,
    "org.scalamock" %% "scalamock-scalatest-support" % scalamockVersion
  ).map(_ % Test))
}
