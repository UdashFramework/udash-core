package io.udash.web.homepage.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{CommonStyleUtils, MediaQueries, StyleConstants, UdashFonts}

import scala.language.postfixOps

object DemoStyles extends CssBase {
  import dsl._

  val demoComponent: CssStyle = style(
    UdashFonts.roboto(),
    position.relative,
    height(682 px),
    marginBottom(3.125 rem),
    fontSize(.875 rem),

    MediaQueries.tabletLandscape(
      height.auto
    ),

    unsafeChild("input")(
      UdashFonts.roboto()
    )
  )

  val laptopImage: CssStyle = style(
    position.absolute,
    top(`0`),
    left(50 %%),
    maxWidth.none,
    transform := "translateX(-50%)",

    MediaQueries.tabletLandscape(
      display.none
    )
  )

  val demoBody: CssStyle = style(
    width(990 px),
    height(600 px),
    paddingTop(30.px),
    margin(`0`, auto),

    MediaQueries.tabletLandscape(
      width.auto,
      height.auto,
      paddingTop(0 px),
      backgroundColor.black
    )
  )

  val demoFiddle: CssStyle = style(
    GlobalStyles.col,
    width(100.%%),
    height :=! s"calc(100% - 43px)",

    MediaQueries.tabletLandscape(
      height(400.px)
    )
  )

  val demoTabs: CssStyle = style(
    position.relative,
    paddingTop(15 px),
    textAlign.left,
    borderColor(StyleConstants.Colors.Grey),
    borderWidth(1 px),
    borderBottomStyle.solid,

    MediaQueries.phone(
      border.none
    )
  )

  val demoTabsItem: CssStyle = style(
    GlobalStyles.col,

    MediaQueries.phone(
      display.block,
      margin(`0`),
      textAlign.center
    )
  )

  val demoTabsLink: CssStyle = style(
    position.relative,
    CommonStyleUtils.transition(),
    display.block,
    padding(.3125 rem,.5625 rem),
    color(StyleConstants.Colors.Grey),
    zIndex(2),

    &.attr(Attributes.data(Attributes.Active), "true") (
      color.white,

      &.before(
        opacity(1),
        transform := "scaleY(1)"
      ),

      &.after(
        opacity(1),
        transform := "scaleY(1)"
      )
    ),
    MediaQueries.desktop(
      &.hover(
        color.white,
        textDecoration := none
      )
    ),

    &.before(
      CommonStyleUtils.transition(),
      content := "\" \"",
      position.absolute,
      left(`0`),
      bottom(-2 px),
      width(100 %%),
      height :=! "calc(100% + 2px)",
      backgroundColor(StyleConstants.Colors.GreySuperDark),
      zIndex(-1),
      opacity(0)
    ),

    &.after(
      CommonStyleUtils.transition(),
      content := "\" \"",
      position.absolute,
      left(`0`),
      bottom(-1 px),
      width(100 %%),
      height(100 %%),
      borderRadius(4 px, 4 px, `0`, `0`),
      borderColor(StyleConstants.Colors.Grey),
      borderWidth(1 px),
      borderTopStyle.solid,
      borderLeftStyle.solid,
      borderRightStyle.solid,
      zIndex(-1),
      opacity(0),
      transform := "scaleY(.7)",
      transformOrigin := "50% 100%",

      MediaQueries.phone(
        borderLeftStyle.none,
        borderRightStyle.none,
        borderBottomStyle.solid
      )
    )
  )

  val demoIOWrapper: CssStyle = style(
    position.relative,
    maxWidth(480 px),
    margin(`0`, auto),
    top(50 %%),
    borderRadius(6 px),
    overflow.hidden,
    transform := "translate3d(0, 0, 1px) translateY(-50%)",

    MediaQueries.tabletLandscape(
      top.auto,
      transform := none
    ),

    MediaQueries.phone(
      width(100 %%)
    )
  )

  val demoBootstrap: CssStyle = style(
    height(300 px)
  )

  val demoInlineField: CssStyle = style(
    display.tableCell,
    minHeight(34 px),
    height(100 %%),
    padding(`0`,.8125 rem),
    border.none,
    verticalAlign.middle,

    MediaQueries.phone(
      display.block,
      width(100 %%),
      height.auto,
      padding(.4375 rem,.8125 rem),
      textAlign.center
    )
  )

  val demoInput: CssStyle = style(
    padding(.4375 rem,.8125 rem),
    border.none
  )

  val demoOutput: CssStyle = style(
    padding(.625 rem,.8125 rem),
    backgroundColor(StyleConstants.Colors.GreyExtra)
  )

  val demoOutpuLabel: CssStyle = style(
    GlobalStyles.col,
    width(100 / 3 %%),
    paddingRight(15 px),
    textAlign.right
  )

  val navItem: CssStyle = style(
    color.black,
    position.relative,
    display.inlineBlock,
    verticalAlign.middle,
    paddingLeft(1.125 rem),
    paddingRight(1.125 rem),

    &.firstChild (
      paddingLeft(0 px)
    ),

    &.lastChild (
      paddingRight(0 px)
    ),

    &.before.not(_.firstChild)(
      CommonStyleUtils.absoluteMiddle,
      content := "\"|\"",
      left(`0`),

      &.hover(
        textDecoration := none
      )
    )
  )

  val underlineLink: CssStyle = style(
    GlobalStyles.underlineLink,
    color.black
  )
}
