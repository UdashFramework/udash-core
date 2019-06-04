logLevel := Level.Warn

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.3.0"

addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.0.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.27")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.20")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.1.0-M13")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")