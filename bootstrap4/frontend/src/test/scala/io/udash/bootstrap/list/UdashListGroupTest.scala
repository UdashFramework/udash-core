package io.udash.bootstrap.list

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import scalatags.JsDom.all._

class UdashListGroupTest extends UdashFrontendTest {

  "UdashListGroup component" should {
    "update items list and clean up listeners properly" in {
      val items = SeqProperty("a", "b", "c")
      val flush = Property[Boolean](false)
      val group = UdashListGroup(items, flush)(
        (item, nested) => div(nested(bind(item))).render
      )
      val el = group.render
      el.childNodes.length should be(3)
      el.textContent should be("abc")

      el.classList.contains(BootstrapStyles.ListGroup.flush.className) should be(false)

      flush.set(true)
      el.classList.contains(BootstrapStyles.ListGroup.flush.className) should be(true)

      items.elemProperties(1).set("x")
      el.childNodes.length should be(3)
      el.textContent should be("axc")

      val removedItem = items.elemProperties(1)
      items.remove(1, 1)
      removedItem.listenersCount() should be(0)
      el.childNodes.length should be(2)
      el.textContent should be("ac")

      group.kill()
      flush.listenersCount() should be(0)
      items.listenersCount() should be(0)
      items.structureListenersCount() should be(0)
      items.elemProperties.foreach(_.listenersCount() should be(0))
    }
  }
}
