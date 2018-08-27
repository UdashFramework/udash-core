package io.udash.bootstrap.card

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.nav.UdashNav
import io.udash.bootstrap.utils.{BootstrapStyles, ComponentId, UdashBootstrapComponent}
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import io.udash.css.CssView._

final class UdashCard(
  backgroundColor: ReadableProperty[Option[BootstrapStyles.Color]],
  borderColor: ReadableProperty[Option[BootstrapStyles.Color]],
  textAlignment: ReadableProperty[Option[BootstrapStyles.Align]],
  textColor: ReadableProperty[Option[BootstrapStyles.Color]],
  override val componentId: ComponentId
)(content: UdashCard#CardElementsFactory => Modifier) extends UdashBootstrapComponent {
  class CardElementsFactory {
    /** Use this method to bond the external binging's lifecycle with the lifecycle of this form. */
    def externalBinding[T <: Binding](binding: T): T = UdashCard.this.nestedInterceptor(binding)

    /** Creates header of a card with a provided content. */
    def header(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.header)(content(externalBinding))
    }

    /** Creates footer of a card with a provided content. */
    def footer(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.footer)(content(externalBinding))
    }

    /** Creates body of a card with a provided content. */
    def body(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.body)(content(externalBinding))
    }

    /** Creates title of a card with a provided content. */
    def title(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.title)(content(externalBinding))
    }

    /** Creates subtitle of a card with a provided content. */
    def subtitle(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.subtitle)(content(externalBinding))
    }

    /** Creates text paragraph for a card with a provided content. */
    def text(content: Binding.NestedInterceptor => Modifier): Modifier = {
      p(BootstrapStyles.Card.text)(content(externalBinding))
    }

    /** Creates link with a provided content. */
    def link(link: ReadableProperty[String])(content: Binding.NestedInterceptor => Modifier): Modifier = {
      a(externalBinding(href.bind(link)), BootstrapStyles.Card.link)(content(externalBinding))
    }

    /** Creates top image for a card with a provided content. */
    def imgTop(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      additionalModifiers: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      img(
        externalBinding(src.bind(imageSrc)),
        externalBinding(alt.bind(alternativeText)),
        BootstrapStyles.Card.imageTop
      )(additionalModifiers(externalBinding))
    }

    /** Creates bottom image for a card with a provided content. */
    def imgBottom(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      additionalModifiers: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      img(
        externalBinding(src.bind(imageSrc)),
        externalBinding(alt.bind(alternativeText)),
        BootstrapStyles.Card.imageBottom
      )(additionalModifiers(externalBinding))
    }

    /** Wraps provided content into an element with `card-img-overlay` style. */
    def imgOverlay(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      content: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      div(BootstrapStyles.Card.imageOverlay)(content(externalBinding))
    }

    /** Puts the provided list group into the card with additional `list-group-flush` style. */
    def listGroup(list: Binding.NestedInterceptor => UdashListGroup[_, _]): Modifier = {
      list(externalBinding).render.styles(BootstrapStyles.ListGroup.flush)
    }

    /** Puts the provided navigation tabs into the card with additional `card-header-tabs` style. */
    def navigationTabs(navigation: Binding.NestedInterceptor => UdashNav[_, _]): Modifier = {
      navigation(externalBinding).render.styles(BootstrapStyles.Card.navTabs)
    }

    /** Puts the provided navigation tabs into the card with additional `card-header-pills` style. */
    def navigationPills(navigation: Binding.NestedInterceptor => UdashNav[_, _]): Modifier = {
      navigation(externalBinding).render.styles(BootstrapStyles.Card.navPills)
    }
  }

  override val render: Element = div(
    BootstrapStyles.Card.card,
    nestedInterceptor((BootstrapStyles.Text.align(_: BootstrapStyles.Align, BootstrapStyles.ResponsiveBreakpoint.All)).reactiveOptionApply(textAlignment)),
    nestedInterceptor((BootstrapStyles.Text.color _).reactiveOptionApply(textColor)),
    nestedInterceptor((BootstrapStyles.Background.color _).reactiveOptionApply(backgroundColor)),
    nestedInterceptor((BootstrapStyles.Border.color _).reactiveOptionApply(borderColor)),
    content(new CardElementsFactory)
  ).render
}

object UdashCard {
  def apply(
    backgroundColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    borderColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    textAlignment: ReadableProperty[Option[BootstrapStyles.Align]] = UdashBootstrap.None,
    textColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    componentId: ComponentId = ComponentId.newId()
  )(content: UdashCard#CardElementsFactory => Modifier): UdashCard = {
    new UdashCard(backgroundColor, borderColor, textAlignment, textColor, componentId)(content)
  }
}