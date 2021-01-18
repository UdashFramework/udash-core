package io.udash.web.guide.styles

import io.udash.css.{CssBase, CssStyle}

import scala.language.postfixOps

object MarkdownStyles extends CssBase {
  import dsl._

  val markdownPage: CssStyle = style(
    unsafeChild("li") (
      position.relative,
      paddingLeft(2 rem),
      margin(.5 rem, `0`, .5 rem, 4.5 rem),

      &.before(
        position.absolute,
        left(`0`),
        top(`0`),
        content.string("•"),
      )
    ),

    unsafeChild("iframe")(
      marginTop(2.5 rem),

      &.firstChild (
        marginTop(`0`)
      )
    ),

    unsafeChild("pre")(
      marginTop(2.5 rem),

      &.firstChild (
        marginTop(`0`)
      )
    ),

    unsafeChild("code") (
      backgroundColor(c"#f5f2f0"),
      fontFamily :=! "Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace"
    ),
  )
}
