package io.udash.css

import scala.collection.mutable

/** Styles created as statements in the stylesheet body instead of being assigned to a `val`, e.g. to register
  * `unsafeRoot` rules. The compiler treats such statements as owned by a synthetic "local dummy" symbol.
  */
object ClassBodyStylesheetExample extends CssBase {
  import dsl._

  val statementStyles: mutable.ListBuffer[CssStyle] = mutable.ListBuffer.empty

  statementStyles += style(
    unsafeChild("button")(
      display.inlineBlock
    )
  )

  statementStyles += keyframes(
    0d -> keyframe(height(10.px)),
    100d -> keyframe(height(20.px)),
  )
}
