package io.udash.bootstrap.nav

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import scalatags.JsDom.all._

class UdashNavTest extends UdashFrontendTest {

  "UdashNav component" should {
    "render provided elements" in {
      val items = SeqProperty[String]("a", "b", "c")
      val nav = UdashNav(items)(
        (item, nested) => span(nested(bind(item))).render
      )
      val el = nav.render
      el.childNodes.length should be(3)
      el.textContent should be("abc")

      items.elemProperties(1).set("x")
      el.childNodes.length should be(3)
      el.textContent should be("axc")

      items.append("d")
      el.childNodes.length should be(4)
      el.textContent should be("axcd")

      val removed = items.elemProperties(1)
      items.remove(1, 1)
      el.childNodes.length should be(3)
      el.textContent should be("acd")
      removed.listenersCount() should be(0)

      nav.kill()
      ensureNoListeners(items)
    }

    "clean up styling properties listeners" in {
      val items = SeqProperty[String]("a", "b", "c")

      val align: ReadableProperty[BootstrapStyles.Align] = Property(BootstrapStyles.Align.Right)
      val vertical: ReadableProperty[Boolean] = Property(true)
      val fill: ReadableProperty[Boolean] = Property(false)
      val justified: ReadableProperty[Boolean] = Property(true)
      val tabs: ReadableProperty[Boolean] = Property(true)
      val pills: ReadableProperty[Boolean] = Property(false)

      val nav = UdashNav(items, align, vertical, fill, justified, tabs, pills)(
        (item, nested) => span(nested(bind(item))).render
      )
      val el = nav.render
      el.childNodes.length should be(3)
      el.textContent should be("abc")

      nav.kill()
      align.listenersCount() should be(0)
      vertical.listenersCount() should be(0)
      fill.listenersCount() should be(0)
      justified.listenersCount() should be(0)
      tabs.listenersCount() should be(0)
      pills.listenersCount() should be(0)
    }
  }
}
