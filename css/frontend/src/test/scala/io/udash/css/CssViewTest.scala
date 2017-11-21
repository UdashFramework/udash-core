package io.udash.css

import io.udash.testing.UdashFrontendTest

class CssViewTest extends UdashFrontendTest {

  import CssView._
  import StylesheetExample._

  import scalatags.JsDom.all._

  "CssView" should {
    "provide tools for rendering scalatags with Udash CSS elements" in {
      val el = div(test1, test2, indent(2))("Test").render

      el.textContent should be("Test")
      el.classList.length should be(3)
      el.classList.contains("io-udash-css-StylesheetExample-test1") should be(true)
      el.classList.contains("io-udash-css-StylesheetExample-test2") should be(true)
      el.classList.contains("StylesheetExample-indent-2") should be(true)

      test1.isInstanceOf[CssStyleName] should be(true)
      test2.isInstanceOf[CssStyleName] should be(true)
      indent(2).isInstanceOf[CssStyleName] should be(true)
    }
  }

  "CssPrefixedStyleName" should {
    "properly add both primary and prefix class to elements" in {
      val el = div(prefixedTest1, prefixedTest2)("Test").render

      el.textContent should be("Test")
      el.classList.length should be(3)
      el.classList.contains("pref") should be(true)
      el.classList.contains("pref-suff1") should be(true)
      el.classList.contains("pref-suff2") should be(true)
    }

    "remove only primary class when other style with the same prefix is present" in {
      val el = div(prefixedTest1, prefixedTest2)("Test").render

      prefixedTest1.removeFrom(el)

      el.classList.length should be(2)
      el.classList.contains("pref") should be(true)
      el.classList.contains("pref-suff2") should be(true)
    }

    "remove both primary and prefix class when there is no other style with the same prefix" in {
      val el = div(prefixedTest1)("Test").render

      prefixedTest1.removeFrom(el)

      el.classList.length should be(0)
    }
  }
}
