import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import sbt.Keys._
import sbt._

object UdashHomepageBuild extends Build {
  val HomepageStaticFilesDir = "UdashStatic/homepage"

  def copyIndex(file: File, to: File) = {
    val newFile = Path(to.toPath.toString + "/index.html")
    IO.copyFile(file, newFile.asFile)
  }

  val compileStaticsHomepage = taskKey[Seq[File]]("Homepage static files manager.")

  val compileStaticsHomepageForRelease = Def.taskDyn {
    val outDir = crossTarget.value / HomepageStaticFilesDir / "WebContent"
    if (!isSnapshot.value) {
      Def.task {
        val indexFile = sourceDirectory.value / "main/assets/index.prod.html"
        copyIndex(indexFile, outDir)
        (fullOptJS in Compile).value
        (packageMinifiedJSDependencies in Compile).value
        (packageScalaJSLauncher in Compile).value
      }
    } else {
      Def.task {
        val indexFile = sourceDirectory.value / "main/assets/index.dev.html"
        copyIndex(indexFile, outDir)
        (fastOptJS in Compile).value
        (packageJSDependencies in Compile).value
        (packageScalaJSLauncher in Compile).value
      }
    }
  }

  val copyStaticsHomepage = taskKey[Unit]("Copy homepage static files into backend target.")}