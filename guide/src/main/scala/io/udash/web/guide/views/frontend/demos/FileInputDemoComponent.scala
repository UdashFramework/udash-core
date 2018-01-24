package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom

class FileInputDemoComponent extends Component {
  import org.scalajs.dom.File

  import JsDom.all._
  val acceptMultipleFiles: Property[Boolean] = Property(true)
  val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)

  val input = FileInput("files", acceptMultipleFiles, selectedFiles)()

  override def getTemplate: Modifier = div(id := "file-input-demo", GuideStyles.frame)(
    input,
    h4("Selected files"),
    ul(GuideStyles.defaultList)(
      repeat(selectedFiles)(file => {
        li(file.get.name).render
      })
    )
  )
}
