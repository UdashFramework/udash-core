package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object TextAreaDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    val text: Property[String] = Property("")

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
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "text-area-demo", GuideStyles.frame, GuideStyles.useBootstrap)(rendered), source.linesIterator.drop(1))
  }
}
