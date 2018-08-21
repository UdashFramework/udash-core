package io.udash.bootstrap.badge

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest

class UdashBadgeTest extends UdashFrontendTest {

  import scalatags.JsDom.all._

  "UdashBadge component" should {
    "update styles" in {
      val style = Property[BootstrapStyles.Color](BootstrapStyles.Color.Secondary)
      val pill = Property[Boolean](true)
      val badge = UdashBadge(style, pill)("badge")
      val el = badge.render

      el.classList.length should be(3)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Secondary).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(true)

      style.set(BootstrapStyles.Color.Info)
      el.classList.length should be(3)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(true)

      pill.set(false)
      el.classList.length should be(2)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(false)

      style.listenersCount() should be(1)
      pill.listenersCount() should be(1)

      badge.kill()
      style.listenersCount() should be(0)
      pill.listenersCount() should be(0)
    }

    "create link component" in {
      val style = Property[BootstrapStyles.Color](BootstrapStyles.Color.Secondary)
      val pill = Property[Boolean](true)
      val href = Property("https://udash.io")
      val badge = UdashBadge.link(href, style, pill)("badge")
      val el = badge.render

      el.classList.length should be(3)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Secondary).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(true)
      el.getAttribute("href") should be(href.get)

      style.set(BootstrapStyles.Color.Info)
      el.classList.length should be(3)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(true)
      el.getAttribute("href") should be(href.get)

      pill.set(false)
      el.classList.length should be(2)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(false)
      el.getAttribute("href") should be(href.get)

      href.set("https://guide.udash.io/")
      el.classList.length should be(2)
      el.classList.contains(BootstrapStyles.Badge.badge.className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className) should be(true)
      el.classList.contains(BootstrapStyles.Badge.pill.className) should be(false)
      el.getAttribute("href") should be(href.get)

      href.listenersCount() should be(1)
      style.listenersCount() should be(1)
      pill.listenersCount() should be(1)

      badge.kill()
      href.listenersCount() should be(0)
      style.listenersCount() should be(0)
      pill.listenersCount() should be(0)
    }
  }
}
