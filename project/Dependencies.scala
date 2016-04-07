import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {
  val udashCoreVersion = "0.1.1"
//  val scalaTagsVersion = "0.5.4"

  val jettyVersion = "9.3.7.v20160115"

  val logbackVersion = "1.1.3"
  val scalaLoggingVersion = "3.1.0"
  val avsystemCommonsVersion = "1.13.1"
  val typesafeConfigVersion = "1.3.0"
  val springVersion = "4.2.2.RELEASE"

  val crossDeps = Def.setting(Seq[ModuleID](
    "io.udash" % "udash-core-shared" % udashCoreVersion
  ))

  val frontendDeps = Def.setting(Seq[ModuleID](
    "io.udash" %%% "udash-core-frontend" % udashCoreVersion exclude("com.lihaoyi", "scalatags_sjs0.6_2.11"),
//    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion exclude("org.scala-js", "scalajs-dom_sjs0.6_2.11"),
    "com.github.japgolly.scalacss" %%% "core" % "0.4.0",
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.4.0" exclude("com.lihaoyi", "scalatags_sjs0.6_2.11")
  ))

  val frontendJSDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    ProvidedJS / "jquery.mCustomScrollbar.concat.min.js"
  ))

  val backendDeps = Def.setting(Seq[ModuleID](
    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,

    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,

    "com.typesafe" % "config" % typesafeConfigVersion,
    "org.springframework" % "spring-beans" % springVersion,
    "com.avsystem.commons" %% "commons-spring" % avsystemCommonsVersion
  ))
}