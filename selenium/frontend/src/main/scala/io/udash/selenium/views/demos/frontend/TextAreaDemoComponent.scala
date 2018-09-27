package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.form.UdashForm
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import scalatags.JsDom

class TextAreaDemoComponent extends CssView {
  import JsDom.all._

  private val text: Property[String] = Property("")

  def getTemplate: Modifier = div(id := "text-area-demo")(
    UdashForm() { factory =>
      div(BootstrapStyles.Grid.row)(
        div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
          factory.input.textArea(text)()
        ),
        div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
          factory.input.textArea(text)()
        ),
        div(BootstrapStyles.Grid.col(4, BootstrapStyles.ResponsiveBreakpoint.Medium))(
          factory.input.textArea(text)()
        )
      )
    }
  )
}
