package io.udash.css

import scala.language.postfixOps

object SecondStylesheetExample extends CssBase {
  import dsl._

  val test: CssStyle = style(
    margin(12 px, auto),
    textAlign.left,
    cursor.pointer,

    &.hover(
      cursor.zoomIn
    ),

    media.not.handheld.landscape.maxWidth(640 px)(
      width(400 px)
    )
  )
}
