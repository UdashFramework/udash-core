package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import scalatags.JsDom

object FileInputDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    import org.scalajs.dom.File

    val acceptMultipleFiles: Property[Boolean] = Property(true)
    val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)

    val input = FileInput(selectedFiles, acceptMultipleFiles)("files")

    div(
      input,
      h4("Selected files"),
      ul(GuideStyles.defaultList)(
        repeat(selectedFiles)(file => {
          li(file.get.name).render
        })
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "file-input-demo", GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}