package io.udash.bootstrap
package button

import io.udash._
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class UdashButtonToolbar[ItemType, ElemType <: Property[ItemType]] private
                        (val items:io.udash.properties.SeqProperty[ItemType, ElemType])
                        (itemFactory: (ElemType) => dom.Element) extends UdashBootstrapComponent {
  lazy val render: dom.Element = {
    div(role := "toolbar", BootstrapStyles.Button.btnToolbar)(
      repeat(items)(itemFactory)
    ).render
  }
}

object UdashButtonToolbar {
  def apply(groups: dom.Element*)(implicit ec: ExecutionContext): UdashButtonToolbar[Int, Property[Int]] = {
    val idxs = SeqProperty[Int](0 until groups.size)
    reactive[Int, Property[Int]](idxs, idx => groups(idx.get))
  }

  def reactive[ItemType, ElemType <: Property[ItemType]]
              (items: io.udash.properties.SeqProperty[ItemType, ElemType],
               itemFactory: (ElemType) => dom.Element): UdashButtonToolbar[ItemType, ElemType] =
    new UdashButtonToolbar[ItemType, ElemType](items)(itemFactory)
}