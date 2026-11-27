package io.udash.css

import io.udash.testing.UdashSharedTest
import scalacss.internal.UnicodeRange

object FontFaceStylesheetExample extends CssBase {
  import dsl._

  val font: CssStyle = fontFace(
    _.src("url(font.woff)").fontDisplay.swap.unicodeRange(UnicodeRange(0x0, 0x7f), UnicodeRange(0x100, 0x17f))
  )
}

class FontFaceRenderTest extends UdashSharedTest {
  private implicit val renderer: scalacss.internal.Renderer[String] = scalacss.internal.StringRenderer.defaultPretty

  "CssBase" should {
    "render all font face descriptors" in {
      FontFaceStylesheetExample.render.trim shouldBe """@font-face {
          |  font-family: io-udash-css-FontFaceStylesheetExample-font;
          |  src: url(font.woff);
          |  font-display: swap;
          |  unicode-range: U+0-7f,U+100-17f;
          |}""".stripMargin
    }
  }
}
