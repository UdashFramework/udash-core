package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object StaticButtonsGroupDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    div(
      UdashButtonGroup(vertical = true.toProperty)(
        UdashButton(Color.Primary.toProperty)("Button 1").render,
        UdashButton()("Button 2").render,
        UdashButton()("Button 3").render
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

