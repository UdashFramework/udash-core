package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

class TextAreaDemoComponent extends Component {
  import JsDom.all._

  val text: Property[String] = Property("")

  override def getTemplate: Modifier = div(id := "text-area-demo", GuideStyles.frame, GuideStyles.useBootstrap)(
    form(BootstrapStyles.containerFluid)(
      div(BootstrapStyles.Grid.row)(
        div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(BootstrapStyles.Form.control)
        ),
        div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(BootstrapStyles.Form.control)
        ),
        div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(BootstrapStyles.Form.control)
        )
      )
    )
  )
}
