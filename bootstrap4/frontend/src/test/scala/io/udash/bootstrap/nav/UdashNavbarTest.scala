package io.udash.bootstrap.nav

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest

class UdashNavbarTest extends UdashFrontendTest {

  "UdashNavbar component" should {
    "render provided elements and cleanup provided navigation only if it was wrapped with interceptor" in {
      val items = SeqProperty[String]("a", "b", "c")
      val nav = UdashNav(items)(
        (item, nested) => nested(bind(item))
      )
      val navbar = UdashNavbar() { nested =>
        nested(nav)
        nav
      }
      val el = navbar.render
      el.textContent should be("abc")

      items.elemProperties(1).set("x")
      el.textContent should be("axc")

      items.append("d")
      el.textContent should be("axcd")

      val removed = items.elemProperties(1)
      items.remove(1, 1)
      el.textContent should be("acd")
      removed.listenersCount() should be(0)

      navbar.kill()
      items.listenersCount() should be(0)
      items.structureListenersCount() should be(0)
      items.elemProperties.foreach(_.listenersCount() should be(0))
    }
    "render provided elements and cleanup provided navigation only if it was wrapped with interceptor (without wrap)" in {
      val items = SeqProperty[String]("a", "b", "c")
      val nav = UdashNav(items)(
        (item, nested) => nested(bind(item))
      )
      val navbar = UdashNavbar()(_ => nav)
      val el = navbar.render
      el.textContent should be("abc")

      items.elemProperties(1).set("x")
      el.textContent should be("axc")

      items.append("d")
      el.textContent should be("axcd")

      val removed = items.elemProperties(1)
      items.remove(1, 1)
      el.textContent should be("acd")
      removed.listenersCount() should be(0)

      navbar.kill()
      items.structureListenersCount() shouldNot be(0)
      items.elemProperties.foreach(_.listenersCount() shouldNot be(0))

      nav.kill()
      items.listenersCount() should be(0)
      items.structureListenersCount() should be(0)
      items.elemProperties.foreach(_.listenersCount() should be(0))
    }

    "clean up styling properties listeners" in {
      val items = SeqProperty[String]("a", "b", "c")
      val nav = UdashNav(items)((item, nested) => nested(bind(item)))

      val expandBreakpoint: ReadableProperty[BootstrapStyles.ResponsiveBreakpoint] = Property(BootstrapStyles.ResponsiveBreakpoint.Small)
      val darkStyle: ReadableProperty[Boolean] = Property(true)
      val backgroundStyle: ReadableProperty[BootstrapStyles.Color] = Property(BootstrapStyles.Color.Secondary)
      val position: ReadableProperty[UdashNavbar.Position] = Property(UdashNavbar.Position.StickyTop)

      val navbar = UdashNavbar(expandBreakpoint, darkStyle, backgroundStyle, position)(_ => nav)
      val el = navbar.render
      el.childNodes.length should be(3)
      el.textContent should be("abc")

      navbar.kill()
      expandBreakpoint.listenersCount() should be(0)
      darkStyle.listenersCount() should be(0)
      backgroundStyle.listenersCount() should be(0)
      position.listenersCount() should be(0)
    }
  }
}
