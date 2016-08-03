logLevel := Level.Warn

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.11")

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.1.3"

// Deployment configuration
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
