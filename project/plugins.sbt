logLevel := Level.Warn

// https://github.com/scala-js/scala-js/issues/3164 - include `scalajs-env-selenium` before `sbt-scalajs`
libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.2.0"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")