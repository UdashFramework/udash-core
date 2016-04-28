import sbt._

object UdashBuild extends Build {
  val CompileAndTest = "test->test;compile->compile"

  val publishedJS = taskKey[Attributed[File]]("JS file that gets packaged into JAR")
  val publishedJSDependencies = taskKey[File]("JS dependencies file that gets packaged into JAR")
}
