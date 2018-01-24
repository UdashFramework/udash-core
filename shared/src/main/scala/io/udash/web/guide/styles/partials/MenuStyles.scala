package io.udash.web.guide.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.css.CssBase
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.utils.{FontWeight, StyleConstants, UdashFonts}
import io.udash.web.guide.styles.utils.{MediaQueries, GuideStyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object MenuStyles extends CssBase {
  import dsl._

  val guideMenu = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    GuideStyleUtils.border(),
    width(100 %%),

    MediaQueries.phone(
      width(320 - StyleConstants.Sizes.GuideHeaderHeightMobile px),
      height(100 %%),
      overflowY.auto
    )
  )

  private val menuLink = style(
    GuideStyleUtils.transition(),
    position.relative,
    display.block,
    width(100 %%),
    padding(2 rem, `0`, 2 rem, 4.5 rem),
    fontSize(2.2 rem),
    color.black,
    textAlign.left,

    &.visited (
      color.black
    ),

    &.hover (
      color.black,
      textDecoration := "none"
    ),

    MediaQueries.tabletLandscape(
      fontSize(1.8 rem)
    )
  )

  val link = style(
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

  val subToggle = style(
    menuLink,

    &.attr(Attributes.data(Attributes.Active), "true") (
      unsafeChild(s".${icon.className}") (
        transform := "rotate(90deg)"
      )
    )
  )

  lazy val linkText = style(
    position.relative
  )

  val item = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  val subItem = style(
    borderBottomColor(StyleConstants.Colors.GreyExtra),
    borderBottomStyle.solid,
    borderBottomWidth(1 px),

    &.lastChild (
      border.none
    )
  )

  lazy val icon = style(
    GuideStyleUtils.transition(new FiniteDuration(100, TimeUnit.MILLISECONDS)),
    position.absolute,
    display.block,
    width(7 px),
    top(50 %%),
    left(2.5 rem),
    transform := "translateX(-50%) translateY(-50%)",
    transformOrigin := "50% 25%"
  )

  val subList = style(
    display.none,
    paddingLeft(5 rem),
    paddingBottom(1.5 rem),

    unsafeChild(s".${item.className}") (
      border.none.important,
      paddingTop(1 rem),
      paddingBottom(1 rem)
    ),

    unsafeChild(s".${link.className}") (
      UdashFonts.acumin(FontWeight.Bold),
      padding(`0`, `0`, `0`, 1.5 rem),
      fontSize(1.6 rem),

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
        fontSize(1.4 rem)
      )
    ),

    unsafeChild(s".${linkText.className}") (
      &.after(
        GuideStyleUtils.transition(transform, new FiniteDuration(250, TimeUnit.MILLISECONDS)),
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

  val btnMobile = style(
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

  private lazy val linkTextHover = style(
    &.after (
      transformOrigin := "0 50%",
      transform := "scaleX(1)"
    )
  )
}
