package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom

class TextAreaDemoComponent extends Component {
  import JsDom.all._

  val text: Property[String] = Property("")

  override def getTemplate: Modifier = div(id := "text-area-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.row)(
        div(BootstrapStyles.Grid.colMd4)(
          TextArea.debounced(text, BootstrapStyles.Form.formControl)
        ),
        div(BootstrapStyles.Grid.colMd4)(
          TextArea.debounced(text, BootstrapStyles.Form.formControl)
        ),
        div(BootstrapStyles.Grid.colMd4)(
          TextArea.debounced(text, BootstrapStyles.Form.formControl)
        )
      )
    )
  )
}
