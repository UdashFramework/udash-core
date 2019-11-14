package io.udash.web.guide.views.frontend.demos

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object FileInputDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import org.scalajs.dom.File
    import scalatags.JsDom.all._

    val acceptMultipleFiles = Property(true)
    val selectedFiles = SeqProperty.blank[File]

    div(
      FileInput(selectedFiles, acceptMultipleFiles)("files"),
      h4("Selected files"),
      ul(repeat(selectedFiles)(file => {
        li(file.get.name).render
      }))
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (div(id := "file-input-demo", GuideStyles.frame, GuideStyles.useBootstrap)(rendered), source.linesIterator)
  }
}