package io.udash.css

import java.io.{File, PrintWriter}

import scalacss.internal.Renderer

class CssFileRenderer(dirPath: String, styles: Seq[_ <: CssBase], createMain: Boolean) {
  def render()(implicit renderer: Renderer[String]): Unit = {
    val dir = new File(dirPath)
    dir.mkdirs()

    val mainFile: Option[File] = if (createMain) Some(new File(s"${dir.getAbsolutePath}/main.css")) else None
    mainFile.foreach(_.createNewFile())

    val mainWriter = mainFile.map(f => new PrintWriter(f, "UTF-8"))

    styles.foreach { style =>
      val name = style.getClass.getName
      val f = new File(s"${dir.getAbsolutePath}/$name.css")
      f.createNewFile()
      new PrintWriter(f, "UTF-8") {
        write(style.render)
        flush()
        close()
      }

      mainWriter.foreach(_.append(s"""@import "$name.css";\n"""))
    }

    mainWriter.foreach(_.flush())
    mainWriter.foreach(_.close())
  }
}
