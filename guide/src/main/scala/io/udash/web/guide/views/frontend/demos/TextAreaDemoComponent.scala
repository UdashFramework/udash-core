package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom

class TextAreaDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  val text: Property[String] = Property("")

  override def getTemplate: Element = div(id := "text-area-demo", GuideStyles.frame)(
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
  ).render
}
