package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.bootstrap.button.UdashButtonOptions
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object SimpleCollapseDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles._
    import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
    import io.udash.bootstrap.collapse.UdashCollapse
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val collapse = UdashCollapse()(
      div(Card.card, Card.body, Background.color(Color.Light))(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    )
    collapse.listen { case ev => events.append(ev) }

    val toggleButton = UdashButton()(_ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle..."))
    val openAndCloseButton = UdashButton()(
      "Open and close after 2 seconds..."
    )
    openAndCloseButton.listen { case _ =>
      collapse.show()
      window.setTimeout(() => collapse.hide(), 2000)
    }

    div(
      UdashButtonGroup(justified = true.toProperty)(
        toggleButton.render,
        openAndCloseButton.render
      ),
      collapse
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (rendered.setup(_.applyTags(GuideStyles.frame)), source)
}

