package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object BadgesDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.badge.UdashBadge
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.utils.BootstrapStyles._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val counter = Property(0)
    window.setInterval(() => counter.set(counter.get + 1), 3000)

    div(
      UdashButton(
        buttonStyle = Color.Primary.toProperty,
        size = Some(Size.Large).toProperty
      )(_ => Seq[Modifier](
        "Button ",
        UdashBadge()(nested => nested(bind(counter))
        ).render
      ))
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

