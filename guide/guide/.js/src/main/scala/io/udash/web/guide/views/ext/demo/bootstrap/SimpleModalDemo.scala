package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object SimpleModalDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles._
    import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
    import io.udash.bootstrap.modal.UdashModal
    import io.udash.bootstrap.modal.UdashModal._
    import io.udash.css.CssView._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val events = SeqProperty.blank[ModalEvent]

    val modal = UdashModal(
      Some(Size.Large).toProperty
    )(
      headerFactory = Some(_ => div("Modal events").render),
      bodyFactory = Some { nested =>
        div(
          Spacing.margin(),
          Card.card, Card.body, Background.color(Color.Light),
        )(
          ul(nested(repeat(events)(event =>
            li(event.get.toString).render
          )))
        ).render
      },
      footerFactory = Some { _ =>
        div(
          UdashButton()(_ => Seq[Modifier](
            UdashModal.CloseButtonAttr, "Close"
          )).render,
          UdashButton(
            Color.Primary.toProperty
          )("Something...").render
        ).render
      }
    )
    modal.listen { case ev => events.append(ev) }

    val openModalButton = UdashButton(
      Color.Primary.toProperty
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
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source)
  }
}

