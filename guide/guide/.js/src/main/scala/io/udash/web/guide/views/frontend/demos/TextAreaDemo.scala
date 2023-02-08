package io.udash.web.guide.views.frontend.demos

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object TextAreaDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    val text = Property("")

    form(containerFluid)(
      div(Grid.row)(
        div(Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(Form.control)
        ),
        div(Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(Form.control)
        ),
        div(Grid.col(4, ResponsiveBreakpoint.Medium))(
          TextArea(text)(Form.control)
        )
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) = {
    import io.udash.css.CssView._
    (div(id := "text-area-demo", GuideStyles.frame, GuideStyles.useBootstrap)(rendered), source)
  }
}
