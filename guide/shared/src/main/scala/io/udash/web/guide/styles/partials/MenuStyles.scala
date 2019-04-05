package io.udash.web.guide.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{FontWeight, StyleConstants, UdashFonts}
import io.udash.web.guide.styles.utils.{GuideStyleUtils, MediaQueries}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object MenuStyles extends CssBase {
  import dsl._

  val guideMenu: CssStyle = style(
    UdashFonts.roboto(FontWeight.Bold),
    GuideStyleUtils.border(),
    width(100 %%),

    MediaQueries.phone(
      width(320 - StyleConstants.Sizes.GuideHeaderHeightMobile px),
      height(100 %%),
      overflowY.auto
    )
  )

  private val menuLink: CssStyle = mixin(
    GuideStyleUtils.transition(),
    position.relative,
    display.block,
    width(100 %%),
    padding(1.25 rem, `0`, 1.25 rem, 2.8125 rem),
    fontSize(1.375 rem),
    color.black,
    textAlign.left,

    &.visited (
      color.black
    ),

    &.hover (
      color.black,
      textDecoration := none
    ),

    MediaQueries.tabletLandscape(
      fontSize(1.125 rem)
    )
  )

  val link: CssStyle = style(
    menuLink,

    &.attr(Attributes.data(Attributes.Active), "true") (
      color(StyleConstants.Colors.Red),

      unsafeChild(s".${linkText.className}") (
        linkTextHover,
        &.after (
          borderColor(StyleConstants.Colors.Red)
        )
      )
    )
  )

  val subToggle: CssStyle = style(
    menuLink,

    &.attr(Attributes.data(Attributes.Active), "true") (
      unsafeChild(s".${icon.className}") (
        transform := "rotate(90deg)"
      )
    )
  )

  lazy val linkText: CssStyle = style(
    position.relative
  )

  val item: CssStyle = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  val subItem: CssStyle = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  lazy val icon: CssStyle = style(
    GuideStyleUtils.transition(duration = 100 milliseconds),
    position.absolute,
    display.block,
    width(7 px),
    top(50 %%),
    left(1.5625 rem),
    transform := "translateX(-50%) translateY(-50%)",
    transformOrigin := "50% 25%"
  )

  val subList: CssStyle = style(
    display.none,
    paddingLeft(3.125 rem),
    paddingBottom(.9375 rem),

    unsafeChild(s".${item.className}") (
      border.none.important,
      paddingTop(.625 rem),
      paddingBottom(.625 rem)
    ),

    unsafeChild(s".${link.className}") (
      UdashFonts.roboto(FontWeight.Bold),
      padding(`0`, `0`, `0`,.9375 rem),
      fontSize(1 rem),

      &.before(
        position.absolute,
        left(`0`),
        top(`0`),
        content := "\"•\""
      ),

      &.hover(
        unsafeChild(s".${linkText.className}") (
          linkTextHover
        )
      ),

      MediaQueries.tabletLandscape(
        fontSize(.875 rem)
      )
    ),

    unsafeChild(s".${linkText.className}") (
      &.after(
        GuideStyleUtils.transition(transform),
        position.absolute,
        top(100 %%),
        left(`0`),
        content := "\" \"",
        width(100 %%),
        borderBottomColor.black,
        borderBottomWidth(1 px),
        borderBottomStyle.solid,
        transform := "scaleX(0)",
        transformOrigin := "100% 50%"
      )
    )
  )

  val btnMobile: CssStyle = style(
    GuideStyleUtils.border(),
    borderLeft.none,
    position.absolute,
    top(`0`),
    left :=! "calc(100% - 1px)",
    backgroundColor.white,

    MediaQueries.tabletLandscape(
      display.block
    )

  )

  private lazy val linkTextHover: CssStyle = mixin(
    &.after (
      transformOrigin := "0 50%",
      transform := "scaleX(1)"
    )
  )
}
