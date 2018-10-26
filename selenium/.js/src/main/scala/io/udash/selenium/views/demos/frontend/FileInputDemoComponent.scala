package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.UdashForm
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import scalatags.JsDom

class FileInputDemoComponent extends CssView {
  import JsDom.all._
  import org.scalajs.dom.File

  private val acceptMultipleFiles: Property[Boolean] = Property(true)
  private val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)

  def getTemplate: Modifier = div(id := "file-input-demo")(
    UdashForm(
      inputValidationTrigger = UdashForm.ValidationTrigger.None,
      selectValidationTrigger = UdashForm.ValidationTrigger.None
    ) { factory =>
      factory.input.fileInput(selectedFiles, acceptMultipleFiles)(
        "files", labelContent = _ => Some("Select files...")
      )
    },
    h4("Selected files", BootstrapStyles.Spacing.margin(BootstrapStyles.Side.Top)),
    ul(
      repeat(selectedFiles) { file =>
        li(file.get.name).render
      }
    )
  )
}
