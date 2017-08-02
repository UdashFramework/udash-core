logLevel := Level.Warn

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.19")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.1")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.1.3"