package io.udash.bootstrap.utils

import io.udash.bootstrap.{BootstrapStyles, UdashBootstrapComponent}
import io.udash.properties.SeqProperty
import io.udash.{properties, _}
import org.scalajs.dom
import org.scalajs.dom.Element

class UdashListGroup[ItemType, ElemType <: Property[ItemType]] private
                    (items: properties.SeqProperty[ItemType, ElemType])
                    (body: (ElemType) => dom.Element)
  extends UdashBootstrapComponent {
  import scalatags.JsDom.all._
  import scalacss.ScalatagsCss._

  override def render: Element =
    ul(BootstrapStyles.List.listGroup)(
      repeat(items)(item => {
        val el = body(item)
        BootstrapStyles.List.listGroupItem.applyTo(el)
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