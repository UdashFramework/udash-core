package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.css.CssView
import scalatags.JsDom

class TextAreaDemoComponent extends CssView {
  import JsDom.all._

  val text: Property[String] = Property("")

  def getTemplate: Modifier = div(id := "text-area-demo")(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(BootstrapStyles.Grid.colMd4)(
          TextArea(text)(BootstrapStyles.Form.formControl)
        ),
        div(BootstrapStyles.Grid.colMd4)(
          TextArea(text)(BootstrapStyles.Form.formControl)
        ),
        div(BootstrapStyles.Grid.colMd4)(
          TextArea(text)(BootstrapStyles.Form.formControl)
        )
      )
    )
  )
}
