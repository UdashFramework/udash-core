package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import scalatags.JsDom

class FileInputDemoComponent extends CssView {
  import JsDom.all._
  import org.scalajs.dom.File
  val acceptMultipleFiles: Property[Boolean] = Property(true)
  val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)

  val input = FileInput(selectedFiles, acceptMultipleFiles)("files")

  def getTemplate: Modifier = div(id := "file-input-demo")(
    input,
    h4("Selected files"),
    ul(
      repeat(selectedFiles)(file => {
        li(file.get.name).render
      })
    )
  )
}
