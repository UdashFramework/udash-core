import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import sbt.Keys._
import sbt._

object UdashWebBuild {
  def copyIndex(file: File, to: File) = {
    val newFile = Path(to.toPath.toString + "/index.html")
    IO.copyFile(file, newFile.asFile)
  }

  val staticFilesDir = settingKey[String]("Frontend application static files manager.")
  val compileStatics = taskKey[File]("Frontend application static files manager.")
  val copyStatics = taskKey[Unit]("Copy static files into backend target.")

  // Compile proper version of JS depending on build version.
  val compileStaticsForRelease = Def.taskDyn {
    val outDir = target.value / staticFilesDir.value / "WebContent"
    if (!isSnapshot.value) {
      Def.task {
        val indexFile = sourceDirectory.value / s"main/assets/index.prod.html"
        copyIndex(indexFile, outDir)
        (fullOptJS in Compile).value
        (packageMinifiedJSDependencies in Compile).value
      }
    } else {
      Def.task {
        val indexFile = sourceDirectory.value / s"main/assets/index.dev.html"
        copyIndex(indexFile, outDir)
        (fastOptJS in Compile).value
        (packageJSDependencies in Compile).value
      }
    }
  }

  def copyStaticsToBackend(p: Project) = Def.task {
    IO.copyDirectory((target in p).value / (staticFilesDir in p).value, (target in Compile).value / (staticFilesDir in p).value)
  }

  def prepareMappings(p: Project) = Def.task {
    copyStatics.value
    ((target in Compile).value / (staticFilesDir in p).value).***.get map { file =>
      file -> file.getAbsolutePath.stripPrefix((target in Compile).value.getAbsolutePath)
    }
  }
}
