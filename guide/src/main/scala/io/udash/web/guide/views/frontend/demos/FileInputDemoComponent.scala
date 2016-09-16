package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom

class FileInputDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._

  import org.scalajs.dom.File
  val acceptMultipleFiles: Property[Boolean] = Property(true)
  val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)

  val input = FileInput("files", acceptMultipleFiles, selectedFiles)()

  override def getTemplate: Modifier = div(id := "file-input-demo", GuideStyles.get.frame)(
    input,
    h4("Selected files"),
    ul(GuideStyles.get.defaultList)(
      repeat(selectedFiles)(file => {
        li(file.get.name).render
      })
    )
  )
}
