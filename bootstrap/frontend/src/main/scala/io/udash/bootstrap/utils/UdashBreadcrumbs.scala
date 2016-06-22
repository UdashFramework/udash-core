package io.udash.bootstrap
package utils

import io.udash.{properties, _}
import org.scalajs.dom
import org.scalajs.dom.Element

class UdashBreadcrumbs[ItemType, ElemType <: Property[ItemType]] private
                      (val pages: properties.SeqProperty[ItemType, ElemType], val selectedPage: ReadableProperty[Int])
                      (itemFactory: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  lazy val render: Element = {
    import scalacss.ScalatagsCss._
    import scalatags.JsDom.all._

    ol(BootstrapStyles.Navigation.breadcrumb)(
      repeat(pages)(page => {
        def currentIdx: Int = pages.elemProperties.indexOf(page)
        val pageIdx = Property[Int](currentIdx)
        pages.listen(_ => pageIdx.set(currentIdx))
        li(BootstrapStyles.active.styleIf(selectedPage.combine(pageIdx)(_ == _)))(
          itemFactory(page)
        ).render
      })
    ).render
  }
}

object UdashBreadcrumbs {
  import scalatags.JsDom.all._

  trait Breadcrumb {
    def name: String
    def url: Url
  }
  case class DefaultBreadcrumb(override val name: String, override val url: Url) extends Breadcrumb

  private def bindHref(page: CastableProperty[Breadcrumb]) =
    bindAttribute(page.asModel.subProp(_.url))((url, el) => el.setAttribute("href", url.value))
  val defaultPageFactory = (page: CastableProperty[Breadcrumb]) =>
    a(bindHref(page))(bind(page.asModel.subProp(_.name))).render

  def apply[ItemType, ElemType <: Property[ItemType]]
           (pages: properties.SeqProperty[ItemType, ElemType], selectedPage: ReadableProperty[Int])
           (itemFactory: (ElemType) => dom.Element): UdashBreadcrumbs[ItemType, ElemType] =
    new UdashBreadcrumbs(pages, selectedPage)(itemFactory)
}
