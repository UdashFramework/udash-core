package io.udash.bootstrap
package utils

import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.seq
import io.udash.{properties, _}
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Anchor

import scala.concurrent.ExecutionContext

class UdashBreadcrumbs[ItemType, ElemType <: Property[ItemType]] private
                      (val pages: seq.SeqProperty[ItemType, ElemType], override val componentId: ComponentId)
                      (itemFactory: (ElemType) => dom.Element,
                       isSelected: (ItemType) => Boolean)(implicit ec: ExecutionContext) extends UdashBootstrapComponent {
  override lazy val render: Element = {
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
  case class DefaultBreadcrumb(override val name: String, override val url: Url) extends Breadcrumb

  private def bindHref(page: CastableProperty[Breadcrumb]) =
    bindAttribute(page.asModel.subProp(_.url))((url, el) => el.setAttribute("href", url.value))

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
  def apply[ItemType, ElemType <: Property[ItemType]]
           (pages: seq.SeqProperty[ItemType, ElemType], componentId: ComponentId = UdashBootstrap.newId())
           (itemFactory: (ElemType) => dom.Element,
            isSelected: (ItemType) => Boolean = (_: ItemType) => false)(implicit ec: ExecutionContext): UdashBreadcrumbs[ItemType, ElemType] =
    new UdashBreadcrumbs(pages, componentId)(itemFactory, isSelected)
}
