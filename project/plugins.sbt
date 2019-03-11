logLevel := Level.Warn

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.3.0"

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.1.0-M9")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")