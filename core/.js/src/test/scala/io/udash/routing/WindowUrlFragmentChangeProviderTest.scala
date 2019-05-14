package io.udash
package routing

import io.udash.testing.AsyncUdashFrontendTest
import org.scalajs.dom

class WindowUrlFragmentChangeProviderTest extends AsyncUdashFrontendTest {
  "WindowUrlFragmentChangeProvider" should {
    val provider = new WindowUrlFragmentChangeProvider()

    "modify history on fragment change" in {
      val historyLength = dom.window.history.length
      val originalFragment = provider.currentFragment
      val originalHref = dom.window.location.href
      val fragment = "lol"

      provider.changeFragment(Url(fragment), replaceCurrent = false)

      provider.currentFragment.value shouldBe fragment
      dom.window.location.hash shouldBe "#" + fragment
      retrying {
        //sometimes history takes time to catch up here
        dom.window.history.length shouldBe historyLength + 1
      }

      dom.window.history.back()

      provider.currentFragment shouldBe originalFragment
      dom.window.location.href shouldBe originalHref

    }

    "not modify history on fragment change" in {
      val historyLength = dom.window.history.length
      val fragment = "lol"

      provider.changeFragment(Url(fragment), replaceCurrent = true)

      provider.currentFragment.value shouldBe fragment
      dom.window.location.hash shouldBe "#" + fragment
      dom.window.history.length shouldBe historyLength
    }
  }
}
