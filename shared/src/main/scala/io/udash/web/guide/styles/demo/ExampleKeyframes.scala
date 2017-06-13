package io.udash.web.guide.styles.demo

import io.udash.css.{CssBase, CssStyle}

object ExampleKeyframes extends CssBase {
  import scala.language.postfixOps
  import dsl._

  val colorPulse: CssStyle = keyframes(
    0d -> keyframe(
      color(c"#000000"),
      backgroundColor(c"#FFFFFF")
    ),

    50d -> keyframe(
      color(c"#FFFFFF"),
      backgroundColor(c"#D9534F")
    ),

    100d -> keyframe(
      color(c"#000000"),
      backgroundColor(c"#FFFFFF")
    )
  )
}
