package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.collapse.UdashCollapse
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.window
import scalatags.JsDom

object SimpleCollapseDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val collapse = UdashCollapse()(
      div(wellStyles)(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    )
    collapse.listen { case ev => events.append(ev) }

    val toggleButton = UdashButton(
      buttonStyle = BootstrapStyles.Color.Primary.toProperty
    )(_ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle..."))
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

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

