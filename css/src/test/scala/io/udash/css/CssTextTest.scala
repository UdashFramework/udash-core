package io.udash
package css

import io.udash.testing.UdashSharedTest

class CssTextTest extends UdashSharedTest {

  import CssText._
  import StylesheetExample._
  import scalatags.Text.all._

  "CssText" should {
    "provide tools for rendering styles with Scalatags text nodes" in {
      div(test1, test2, indent(2))("Test").render shouldBe
        "<div class=\" io-udash-css-StylesheetExample-test1 io-udash-css-StylesheetExample-test2 StylesheetExample-indent-2\">Test</div>"
    }
  }
}
