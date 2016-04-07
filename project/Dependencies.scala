import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies extends Build {

  val versionOfScala = "2.11.8"
  val silencerVersion = "0.3"

  val udashCoreVersion = "0.1.1"
  val udashRPCVersion = "0.1.0"

  val scalaJsDomVersion = "0.9.0"

  val scalatestVersion = "3.0.0-M15"

  val compilerPlugins = Def.setting(Seq(
    "com.github.ghik" % "silencer-plugin" % silencerVersion
  ).map(compilerPlugin))

  val crossDeps = Def.setting(Seq(
    "io.udash" % "udash-core-shared" % udashCoreVersion,
    "io.udash" % "udash-rpc-shared" % udashRPCVersion
  ))

  val frontendDeps = Def.setting(Seq(
    "io.udash" %%% "udash-core-frontend" % udashCoreVersion,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion
  ))

  val crossTestDeps = Def.setting(Seq(
    "org.scalatest" %%% "scalatest" % scalatestVersion
  ).map(_ % Test))
}