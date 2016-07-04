import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val versionOfScala = "2.11.8"
  val jettyVersion = "9.3.8.v20160314"

  val udashVersion = "0.3.0-rc.1"
  val udashJQueryVersion = "1.0.0"

  val scalaTagsVersion = "0.5.5"
  val scalaCssVersion = "0.4.1"

  val scalaLoggingVersion = "3.1.0"
  val logbackVersion = "1.1.3"

  val avsystemCommonsVersion = "1.14.0"
  val typesafeConfigVersion = "1.3.0"
  val springVersion = "4.2.5.RELEASE"
  val akkaVersion = "2.4.7"
  val sprayVersion = "1.3.1"

  val bootstrapVersion = "3.3.1-1"

  val seleniumVersion = "2.53.0"
  val scalatestVersion = "3.0.0-M15"
  val scalamockVersion = "3.2.2"

  val crossDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-shared" % udashVersion exclude("com.lihaoyi", "scalatags_2.11"),
    "io.udash" %%% "udash-rest-shared" % udashVersion,
    "io.udash" %%% "udash-rpc-shared" % udashVersion,
    "io.udash" %%% "udash-i18n-shared" % udashVersion,
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashVersion,
    "io.udash" %%% "udash-rpc-frontend" % udashVersion,
    "io.udash" %%% "udash-i18n-frontend" % udashVersion,
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
    "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % scalaCssVersion
  ))

  val frontendJSDeps = Def.setting(Seq(
    "org.webjars" % "bootstrap-sass" % bootstrapVersion / "3.3.1/javascripts/bootstrap.js" dependsOn "jquery.js"
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

    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-servlet" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
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
