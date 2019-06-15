package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.badge.UdashBadge
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.window
import scalatags.JsDom

object BadgesDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val counter = Property(0)
    window.setInterval(() => counter.set(counter.get + 1), 3000)

    div(
      UdashButton(
        buttonStyle = BootstrapStyles.Color.Primary.toProperty,
        size = Some(BootstrapStyles.Size.Large).toProperty
      )(_ => Seq[Modifier](
        "Button ",
        UdashBadge()(nested => nested(bind(counter))
        ).render
      ))
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

