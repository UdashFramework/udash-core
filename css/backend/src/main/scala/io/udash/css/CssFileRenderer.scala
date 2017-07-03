package io.udash.css

import java.io.{File, PrintWriter}

import scalacss.internal.Renderer

/**
  * Renders provided styles into files. Creates separate file for each stylesheet.
  * Keeps styles order from provided `Seq`.
  * @param dirPath Target directory for rendered files.
  * @param styles Sequence of stylesheets.
  * @param createMain If true, creates `main.css` file. It imports all other stylesheets.
  */
class CssFileRenderer(dirPath: String, styles: Seq[CssBase], createMain: Boolean) {
  def render()(implicit renderer: Renderer[String]): Unit = {
    val dir = new File(dirPath)
    dir.mkdirs()

    val mainFile: Option[File] = if (createMain) Some(new File(s"${dir.getAbsolutePath}/main.css")) else None
    mainFile.foreach(_.createNewFile())

    val mainWriter = mainFile.map(new PrintWriter(_, "UTF-8"))

    styles.foreach { style =>
      val name = style.getClass.getName
      val f = new File(s"${dir.getAbsolutePath}/$name.css") {
        createNewFile()
      }
      new PrintWriter(f, "UTF-8") {
        write(style.render)
        flush()
        close()
      }

      mainWriter.foreach(_.append(s"""@import "$name.css";\n"""))
    }

    mainWriter.foreach { w =>
      w.flush()
      w.close()
    }
  }
}
