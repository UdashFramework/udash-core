logLevel := Level.Warn

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "1.1.1"
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.20.1")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")
addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.3")
addSbtPlugin("com.github.sbt" % "sbt-less" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.3")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.8")