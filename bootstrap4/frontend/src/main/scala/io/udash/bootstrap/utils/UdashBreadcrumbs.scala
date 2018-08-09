package io.udash.bootstrap
package utils

import io.udash._
import io.udash.bootstrap.ComponentId
import io.udash.properties.{HasModelPropertyCreator, seq}
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Anchor

final class UdashBreadcrumbs[ItemType, ElemType <: ReadableProperty[ItemType]] private
                            (val pages: seq.ReadableSeqProperty[ItemType, ElemType], override val componentId: ComponentId)
                            (itemFactory: (ElemType) => dom.Element, isSelected: (ItemType) => Boolean)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element = {
    import scalatags.JsDom.all._
    ol(id := componentId, BootstrapStyles.Navigation.breadcrumb)(
      repeat(pages)(page => {
        li(BootstrapStyles.active.styleIf(page.transform(isSelected)))(
          itemFactory(page)
        ).render
      })
    ).render
  }
}

object UdashBreadcrumbs {
  import scalatags.JsDom.all._

  /** Default breadcrumb model. */
  trait Breadcrumb {
    def name: String
    def url: Url
  }
  object Breadcrumb extends HasModelPropertyCreator[Breadcrumb]

  case class DefaultBreadcrumb(override val name: String, override val url: Url) extends Breadcrumb
  object DefaultBreadcrumb extends HasModelPropertyCreator[DefaultBreadcrumb]

  private def bindHref(page: CastableProperty[Breadcrumb]) =
    href.bind(page.asModel.subProp(_.url.value))

  /** Default breadcrumb model factory. */
  val defaultPageFactory: CastableProperty[Breadcrumb] => Anchor =
    page => a(bindHref(page))(bind(page.asModel.subProp(_.name))).render

  /**
    * Creates breadcrumbs component, synchronised with provided SeqProperty.
    * More: <a href="http://getbootstrap.com/javascript/#breadcrumbs">Bootstrap Docs</a>.
    *
    * @param pages       SeqProperty containing breadcrumbs data.
    * @param componentId Id of the root DOM node.
    * @param itemFactory Creates DOM representation for provided item.
    * @param isSelected  Decides whether an element is selected.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashBreadcrumbs` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (pages: seq.ReadableSeqProperty[ItemType, ElemType], componentId: ComponentId = ComponentId.newId())
           (itemFactory: (ElemType) => dom.Element, isSelected: (ItemType) => Boolean = (_: ItemType) => false): UdashBreadcrumbs[ItemType, ElemType] =
    new UdashBreadcrumbs(pages, componentId)(itemFactory, isSelected)
}
