package io.udash.css

import io.udash.testing.UdashSharedTest
import scalacss.internal.{Compose, Dsl}

/** Mirrors what udash macros up to 0.26.0 compiled into libraries for stylesheet body statements: class names ending
  * with the compiler's local dummy symbol name, which contains whitespace.
  */
object PrecompiledClassBodyStylesheetExample extends CssBase {
  import dsl._

  private final val LocalDummyName = "io-udash-css-PrecompiledClassBodyStylesheetExample-<local PrecompiledClassBodyStylesheetExample>"

  dsl.elementsBuffer += CssStyleImpl(
    LocalDummyName,
    Dsl.style(unsafeChild("button")(display.inlineBlock))(Compose.trust),
  )

  dsl.elementsBuffer += CssKeyframes(
    LocalDummyName,
    Seq(
      0d -> Dsl.style(height(10.px))(Compose.trust),
      100d -> Dsl.style(height(20.px))(Compose.trust),
    ),
  )
}

class ClassBodyStyleRenderTest extends UdashSharedTest {
  private implicit val renderer: scalacss.internal.Renderer[String] = scalacss.internal.StringRenderer.defaultPretty

  "CssBase" should {
    "render styles created as stylesheet body statements" in {
      val rendered = ClassBodyStylesheetExample.render
      rendered should include(".io-udash-css-ClassBodyStylesheetExample button {")
      rendered should include("@keyframes io-udash-css-ClassBodyStylesheetExample {")
    }

    "render precompiled styles named after the local dummy symbol" in {
      val rendered = PrecompiledClassBodyStylesheetExample.render
      rendered should include(".io-udash-css-PrecompiledClassBodyStylesheetExample button {")
      rendered should include("@keyframes io-udash-css-PrecompiledClassBodyStylesheetExample {")
      rendered should not include "<local"
    }
  }
}
