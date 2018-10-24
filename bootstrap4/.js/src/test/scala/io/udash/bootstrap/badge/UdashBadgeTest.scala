package io.udash.bootstrap.badge

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest

class UdashBadgeTest extends UdashFrontendTest {

  "UdashBadge component" should {
    "update styles" in {
      val style = Property[BootstrapStyles.Color](BootstrapStyles.Color.Secondary)
      val pill = Property[Boolean](true)
      val badge = UdashBadge(style, pill)("badge")
      val el = badge.render

      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Secondary).className)
      el.classList should contain(BootstrapStyles.Badge.pill.className)

      style.set(BootstrapStyles.Color.Info)
      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className)
      el.classList should contain(BootstrapStyles.Badge.pill.className)

      pill.set(false)
      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className)
      el.classList shouldNot contain(BootstrapStyles.Badge.pill.className)

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
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Secondary).className)
      el.classList should contain(BootstrapStyles.Badge.pill.className)
      el.getAttribute("href") should be(href.get)

      style.set(BootstrapStyles.Color.Info)
      el.classList.length should be(3)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className)
      el.classList should contain(BootstrapStyles.Badge.pill.className)
      el.getAttribute("href") should be(href.get)

      pill.set(false)
      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className)
      el.classList shouldNot contain(BootstrapStyles.Badge.pill.className)
      el.getAttribute("href") should be(href.get)

      href.set("https://guide.udash.io/")
      el.classList.length should be(2)
      el.classList should contain(BootstrapStyles.Badge.badge.className)
      el.classList should contain(BootstrapStyles.Badge.color(BootstrapStyles.Color.Info).className)
      el.classList shouldNot contain(BootstrapStyles.Badge.pill.className)
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
