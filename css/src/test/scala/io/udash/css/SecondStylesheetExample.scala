package io.udash.css


object SecondStylesheetExample extends CssBase {
  import dsl._

  def utils(x: Int): CssStyle = mixin(
    margin(x px, auto),
    textAlign.left,
    cursor.pointer
  )

  val test: CssStyle = style(
    utils(12),

    &.hover(
      cursor.zoomIn
    ),

    media.not.handheld.landscape.maxWidth(640 px)(
      width(400 px)
    )
  )
}
