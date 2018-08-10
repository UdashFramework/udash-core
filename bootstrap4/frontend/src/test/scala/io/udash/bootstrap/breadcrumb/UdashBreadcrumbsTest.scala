package io.udash.bootstrap.breadcrumb

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import scalatags.JsDom.all._

class UdashBreadcrumbsTest extends UdashFrontendTest {

  "UdashBreadcrumbs component" should {
    "work with custom elements" in {
      class CustomBreadcrumb(val name: String, val id: String)

      val pages = SeqProperty(
        new CustomBreadcrumb("A", "id1"),
        new CustomBreadcrumb("B", "id2"),
        new CustomBreadcrumb("C", "id3"),
      )
      val breadcrumbs = UdashBreadcrumbs(pages)(
        itemFactory = (item, nested) => span(
          nested(id.bind(item.transform(_.id))),
          nested(bind(item.transform(_.name)))
        ).render,
        isActive = _.id.charAt(2).toInt % 2 == 0
      )
      val el = breadcrumbs.render

      el.getElementsByTagName("span").length should be(3)
      el.getElementsByClassName(BootstrapStyles.active.className).length should be(1)
      el.textContent should be("ABC")

      pages.append(new CustomBreadcrumb("D", "id4"))

      el.getElementsByTagName("span").length should be(4)
      el.getElementsByClassName(BootstrapStyles.active.className).length should be(2)
      el.textContent should be("ABCD")

      pages.remove(1, 1)

      el.getElementsByTagName("span").length should be(3)
      el.getElementsByClassName(BootstrapStyles.active.className).length should be(1)
      el.textContent should be("ACD")

      pages.elemProperties(1).set(new CustomBreadcrumb("X", "id8"))

      el.getElementsByTagName("span").length should be(3)
      el.getElementsByClassName(BootstrapStyles.active.className).length should be(2)
      el.textContent should be("AXD")

      breadcrumbs.kill()
      pages.listenersCount() should be(0)
      pages.structureListenersCount() should be(0)
      pages.elemProperties.foreach(_.listenersCount() should be(0))
    }

    "work with default elements" in {
      val pages = SeqProperty(
        new UdashBreadcrumbs.Breadcrumb("Home", "https://udash.io/"),
        new UdashBreadcrumbs.Breadcrumb("Guide", "https://guide.udash.io/"),
        new UdashBreadcrumbs.Breadcrumb("RPC", "https://guide.udash.io/rpc"),
      )
      val breadcrumbs = UdashBreadcrumbs.default(pages)()
      val el = breadcrumbs.render

      el.getElementsByTagName("a").length should be(3)
      el.textContent should be("HomeGuideRPC")

      pages.append(new UdashBreadcrumbs.Breadcrumb("Frontend", "https://guide.udash.io/rpc/frontend"))

      el.getElementsByTagName("a").length should be(4)
      el.textContent should be("HomeGuideRPCFrontend")

      pages.remove(1, 1)

      el.getElementsByTagName("a").length should be(3)
      el.textContent should be("HomeRPCFrontend")

      pages.elemProperties(1).set(new UdashBreadcrumbs.Breadcrumb("X", "http://google.com"))

      el.getElementsByTagName("a").length should be(3)
      el.textContent should be("HomeXFrontend")

      breadcrumbs.kill()
      pages.listenersCount() should be(0)
      pages.structureListenersCount() should be(0)
      pages.elemProperties.foreach(_.listenersCount() should be(0))
    }

    "work with strings" in {
      val pages = SeqProperty("A", "B", "C")
      val breadcrumbs = UdashBreadcrumbs.text(pages)()
      val el = breadcrumbs.render

      el.textContent should be("ABC")

      pages.append("D")
      el.textContent should be("ABCD")

      pages.remove(1, 1)
      el.textContent should be("ACD")

      pages.elemProperties(1).set("X")
      el.textContent should be("AXD")

      breadcrumbs.kill()
      pages.listenersCount() should be(0)
      pages.structureListenersCount() should be(0)
      pages.elemProperties.foreach(_.listenersCount() should be(0))
    }
  }
}
