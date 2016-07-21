package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom
import io.udash.web.commons.views.Component

class TextAreaDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val text: Property[String] = Property("")

  override def getTemplate: Modifier = div(id := "text-area-demo", GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
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
