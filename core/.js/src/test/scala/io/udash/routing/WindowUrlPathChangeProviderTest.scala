package io.udash
package routing

import io.udash.testing.AsyncUdashFrontendTest
import org.scalajs.dom

class WindowUrlPathChangeProviderTest extends AsyncUdashFrontendTest {
  "WindowUrlPathChangeProvider" should {
    val provider = new WindowUrlPathChangeProvider()

    "modify history on fragment change" in {
      val historyLength = dom.window.history.length
      val originalHref = dom.window.location.href
      val originalFragment = provider.currentFragment
      val fragment = "lol"

      provider.changeFragment(Url(fragment), replaceCurrent = false)

      provider.currentFragment.value should endWith(s"/$fragment")
      dom.window.location.pathname should endWith(s"/$fragment")
      retrying {
        //sometimes history takes time to catch up here
        dom.window.history.length shouldBe historyLength + 1
      }

      dom.window.history.back()

      retrying {
        provider.currentFragment shouldBe originalFragment
        dom.window.location.href shouldBe originalHref
      }
    }

    "not modify history on fragment change" in {
      val historyLength = dom.window.history.length
      val fragment = "lol"

      provider.changeFragment(Url(fragment), replaceCurrent = true)

      retrying {
        provider.currentFragment.value should endWith(s"/$fragment")
        dom.window.location.pathname should endWith(s"/$fragment")
        dom.window.history.length shouldBe historyLength
      }
    }
  }
}
