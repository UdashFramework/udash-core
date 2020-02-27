package io.udash.bootstrap
package card

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.nav.UdashNav
import io.udash.bootstrap.utils.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.css.CssView._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class UdashCard private(
  backgroundColor: ReadableProperty[Option[BootstrapStyles.Color]],
  borderColor: ReadableProperty[Option[BootstrapStyles.Color]],
  textAlignment: ReadableProperty[Option[BootstrapStyles.Align]],
  textColor: ReadableProperty[Option[BootstrapStyles.Color]],
  override val componentId: ComponentId
)(content: UdashCard#CardElementsFactory => Modifier) extends UdashBootstrapComponent {
  class CardElementsFactory {
    /** Use this method to bond the external binging's lifecycle with the lifecycle of this form. */
    def externalBinding[T <: Binding](binding: T): T = UdashCard.this.nestedInterceptor(binding)

    /** Creates header of a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#header-and-footer">Bootstrap Docs</a>. */
    def header(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.header)(content(externalBinding))
    }

    /** Creates footer of a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#header-and-footer">Bootstrap Docs</a>. */
    def footer(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.footer)(content(externalBinding))
    }

    /** Creates body of a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#body">Bootstrap Docs</a>. */
    def body(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.body)(content(externalBinding))
    }

    /** Creates title of a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#titles-text-and-links">Bootstrap Docs</a>. */
    def title(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.title)(content(externalBinding))
    }

    /** Creates subtitle of a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#titles-text-and-links">Bootstrap Docs</a>. */
    def subtitle(content: Binding.NestedInterceptor => Modifier): Modifier = {
      div(BootstrapStyles.Card.subtitle)(content(externalBinding))
    }

    /** Creates text paragraph for a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#titles-text-and-links">Bootstrap Docs</a>. */
    def text(content: Binding.NestedInterceptor => Modifier): Modifier = {
      p(BootstrapStyles.Card.text)(content(externalBinding))
    }

    /** Creates link with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#titles-text-and-links">Bootstrap Docs</a>. */
    def link(link: ReadableProperty[String])(content: Binding.NestedInterceptor => Modifier): Modifier = {
      a(externalBinding(href.bind(link)), BootstrapStyles.Card.link)(content(externalBinding))
    }

    /** Creates top image for a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#images-1">Bootstrap Docs</a>. */
    def imgTop(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      additionalModifiers: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      img(
        externalBinding(src.bind(imageSrc)),
        externalBinding(alt.bind(alternativeText)),
        BootstrapStyles.Card.imageTop
      )(additionalModifiers(externalBinding))
    }

    /** Creates bottom image for a card with a provided content.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#images-1">Bootstrap Docs</a>. */
    def imgBottom(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      additionalModifiers: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      img(
        externalBinding(src.bind(imageSrc)),
        externalBinding(alt.bind(alternativeText)),
        BootstrapStyles.Card.imageBottom
      )(additionalModifiers(externalBinding))
    }

    /** Wraps provided content into an element with `card-img-overlay` style.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#images-1">Bootstrap Docs</a>. */
    def imgOverlay(imageSrc: ReadableProperty[String], alternativeText: ReadableProperty[String])(
      content: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      div(BootstrapStyles.Card.imageOverlay)(content(externalBinding))
    }

    /** Puts the provided list group into the card with additional `list-group-flush` style.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#list-groups">Bootstrap Docs</a>. */
    def listGroup(list: Binding.NestedInterceptor => UdashListGroup[_, _]): Modifier = {
      list(externalBinding).render.styles(BootstrapStyles.ListGroup.flush)
    }

    /** Puts the provided navigation tabs into the card with additional `card-header-tabs` style.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#navigation">Bootstrap Docs</a>. */
    def navigationTabs(navigation: Binding.NestedInterceptor => UdashNav[_, _]): Modifier = {
      navigation(externalBinding).render.styles(BootstrapStyles.Card.navTabs)
    }

    /** Puts the provided navigation tabs into the card with additional `card-header-pills` style.
      * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#navigation">Bootstrap Docs</a>. */
    def navigationPills(navigation: Binding.NestedInterceptor => UdashNav[_, _]): Modifier = {
      navigation(externalBinding).render.styles(BootstrapStyles.Card.navPills)
    }
  }

  override val render: Element = div(
    BootstrapStyles.Card.card, componentId,
    nestedInterceptor((BootstrapStyles.Text.align(_: BootstrapStyles.Align, BootstrapStyles.ResponsiveBreakpoint.All)).reactiveOptionApply(textAlignment)),
    nestedInterceptor((BootstrapStyles.Text.color _).reactiveOptionApply(textColor)),
    nestedInterceptor((BootstrapStyles.Background.color _).reactiveOptionApply(backgroundColor)),
    nestedInterceptor((BootstrapStyles.Border.color _).reactiveOptionApply(borderColor)),
    content(new CardElementsFactory)
  ).render
}

object UdashCard {
  /**
    * Creates a card component.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/card/">Bootstrap Docs</a>.
    *
    * @param backgroundColor A card style, one of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param borderColor     A color of the borders. One of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param textAlignment   Alignment of a content of tthe card. One of the `BootstrapStyles.Align` values.
    * @param textColor       A color of the texts inside the card. One of the standard bootstrap colors `BootstrapStyles.Color`.
    * @param componentId     An id of the root DOM node.
    * @param content         A factory of the card elements. All elements created with the factory will be cleaned up on the card cleanup.
    * @return A `UdashCard` component, call `render` to create a DOM element representing this button.
    */
  def apply(
    backgroundColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    borderColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    textAlignment: ReadableProperty[Option[BootstrapStyles.Align]] = UdashBootstrap.None,
    textColor: ReadableProperty[Option[BootstrapStyles.Color]] = UdashBootstrap.None,
    componentId: ComponentId = ComponentId.generate()
  )(content: UdashCard#CardElementsFactory => Modifier): UdashCard = {
    new UdashCard(backgroundColor, borderColor, textAlignment, textColor, componentId)(content)
  }

  /** Puts the provided cards into a group.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#card-groups">Bootstrap Docs</a>. */
  def group(cards: Modifier*): Element = {
    div(BootstrapStyles.Card.group)(cards).render
  }

  /** Puts the provided cards into a deck.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#card-decks">Bootstrap Docs</a>. */
  def deck(cards: Modifier*): Element = {
    div(BootstrapStyles.Card.deck)(cards).render
  }

  /** Puts the provided cards into a columns layout.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/card/#card-columns">Bootstrap Docs</a>. */
  def columns(cards: Modifier*): Element = {
    div(BootstrapStyles.Card.columns)(cards).render
  }
}