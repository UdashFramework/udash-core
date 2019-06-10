package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.modal.UdashModal
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.window
import scalatags.JsDom

object SimpleModalDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val events = SeqProperty.blank[UdashModal.ModalEvent]
    val header = { _: Binding.NestedInterceptor =>
      div("Modal events").render
    }
    val body = { nested: Binding.NestedInterceptor =>
      div(wellStyles, BootstrapStyles.Spacing.margin())(
        ul(nested(repeat(events)(event =>
          li(event.get.toString).render))
        )
      ).render
    }
    val footer = { _: Binding.NestedInterceptor =>
      div(
        UdashButton()(_ => Seq[Modifier](
          UdashModal.CloseButtonAttr, "Close"
        )).render,
        UdashButton(
          BootstrapStyles.Color.Primary.toProperty
        )("Something...").render
      ).render
    }

    val modal = UdashModal(
      Some(BootstrapStyles.Size.Large).toProperty
    )(
      headerFactory = Some(header),
      bodyFactory = Some(body),
      footerFactory = Some(footer)
    )
    modal.listen { case ev => events.append(ev) }

    val openModalButton = UdashButton(
      BootstrapStyles.Color.Primary.toProperty
    )("Show modal...")
    openModalButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        modal.show()
    }
    val openAndCloseButton = UdashButton()(
      "Open and close after 2 seconds..."
    )
    openAndCloseButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        modal.show()
        window.setTimeout(() => modal.hide(), 2000)
    }

    div(
      modal,
      UdashButtonGroup()(
        openModalButton.render,
        openAndCloseButton.render
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

