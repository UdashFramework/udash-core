package io.udash.bootstrap
package utils

import io.udash._
import io.udash.properties.SeqProperty
import org.scalajs.dom
import org.scalajs.dom.Element

class UdashListGroup[ItemType, ElemType <: Property[ItemType]] private
                    (items: properties.SeqProperty[ItemType, ElemType])
                    (body: (ElemType) => dom.Element)
  extends UdashBootstrapComponent {
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._

  override def render: Element =
    ul(BootstrapStyles.List.listGroup)(
      repeat(items)(item => {
        val el = body(item)
        BootstrapStyles.List.listGroupItem.addTo(el)
        el
      })
    ).render
}

object UdashListGroup {
  def apply[ItemType, ElemType <: Property[ItemType]]
           (items: SeqProperty[ItemType, ElemType])
           (body: (ElemType) => Element): UdashListGroup[ItemType, ElemType] =
    new UdashListGroup(items)(body)
}