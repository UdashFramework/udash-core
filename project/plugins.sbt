logLevel := Level.Warn

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.3.0"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.4.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.1.0-M13")
addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.0.0")