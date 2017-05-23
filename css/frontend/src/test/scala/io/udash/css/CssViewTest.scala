package io.udash.css

import io.udash.testing.UdashFrontendTest

class CssViewTest extends UdashFrontendTest {
  import scalatags.JsDom.all._
  import CssView._

  "CssView" should {
    "provide tools for rendering HTML with Udash CSS elements" in {
      val el = div(StylesheetExample.test1, StylesheetExample.test2, StylesheetExample.indent(2))("Test").render

      el.textContent should be("Test")
      el.classList.length should be(3)
      el.classList.contains("io-udash-css-StylesheetExample-test1") should be(true)
      el.classList.contains("io-udash-css-StylesheetExample-test2") should be(true)
      el.classList.contains("StylesheetExample-indent-2") should be(true)
    }
  }
}
