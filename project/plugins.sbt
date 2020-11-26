logLevel := Level.Warn

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "1.1.0"
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.3.1")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")
addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")

// Deployment configuration
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.5")