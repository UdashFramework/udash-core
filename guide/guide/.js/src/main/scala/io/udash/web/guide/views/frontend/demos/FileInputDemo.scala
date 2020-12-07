package io.udash.web.guide.views.frontend.demos

import io.udash.css.CssView
import io.udash.utils.FileService
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object FileInputDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import org.scalajs.dom.File
    import scalatags.JsDom.all._

    import scala.concurrent.ExecutionContext.Implicits.global

    val acceptMultipleFiles = true.toProperty
    val selectedFiles = SeqProperty.blank[File]

    div(
      FileInput(selectedFiles, acceptMultipleFiles)("files"),
      h4("Selected files"),
      ul(repeat(selectedFiles)(file => {
        val content = Property(Array.empty[Byte])

        FileService.asBytesArray(file.get) foreach { bytes =>
          content.set(bytes)
        }

        val name = file.get.name
        li(showIfElse(content.transform(_.isEmpty))(
          span(name).render,
          {
            val url = FileService.createURL(content.get)
            val download = a(href := url.value, attr("download") := name)(name)
            val revoke = a(href := "#", onclick := { () =>
              content.set(Array.empty[Byte])
              url.close()
            })("revoke")

            Seq(download, span(" or "), revoke).render
          }
        )).render
      }))
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (div(id := "file-input-demo", GuideStyles.frame, GuideStyles.useBootstrap)(rendered), source.linesIterator)
  }
}