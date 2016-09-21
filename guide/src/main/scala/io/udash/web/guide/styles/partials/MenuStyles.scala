package io.udash.web.guide.styles.partials

import java.util.concurrent.TimeUnit

import io.udash.web.commons.styles.UdashStylesheet
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.styles.components.MobileMenuStyles
import io.udash.web.commons.styles.utils.{FontWeight, StyleConstants, UdashFonts}
import io.udash.web.guide.components.GuideMenu
import io.udash.web.guide.styles.utils.{MediaQueries, StyleUtils}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scalacss.internal.Compose
import scalacss.Defaults._

object MenuStyles extends UdashStylesheet {
  import dsl._

  val guideMenu = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    StyleUtils.border(),
    width(100 %%),

    MediaQueries.phone(
      style(
        width(320 - StyleConstants.Sizes.GuideHeaderHeightMobile px),
        height(100 %%),
        overflowY.auto
      )
    )
  )

  private val menuLink = style(
    StyleUtils.transition(),
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
      style(
        fontSize(1.8 rem)
      )
    )
  )

  val link = style(
    menuLink,

    &.attr(Attributes.data(Attributes.Active), "true") (
      color(StyleConstants.Colors.Red),

      unsafeChild(s".${linkText.htmlClass}") (
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
      unsafeChild(s".${icon.htmlClass}") (
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
    StyleUtils.transition(new FiniteDuration(100, TimeUnit.MILLISECONDS)),
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

    unsafeChild(s".${item.htmlClass}") (
      border.none.important,
      paddingTop(1 rem),
      paddingBottom(1 rem)
    ),

    unsafeChild(s".${link.htmlClass}") (
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
        unsafeChild(s".${linkText.htmlClass}") (
          linkTextHover
        )
      ),

      MediaQueries.tabletLandscape(
        style(
          fontSize(1.4 rem)
        )
      )
    ),

    unsafeChild(s".${linkText.htmlClass}") (
      &.after(
        StyleUtils.transition(transform, new FiniteDuration(250, TimeUnit.MILLISECONDS)),
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
    StyleUtils.border(),
    borderLeft.none,
    position.absolute,
    top(`0`),
    left :=! "calc(100% - 1px)",
    backgroundColor.white,

    MediaQueries.tabletLandscape(
      style(
        display.block
      )
    )

  )(Compose.trust)

  private lazy val linkTextHover = style(
    &.after (
      transformOrigin := "0 50%",
      transform := "scaleX(1)"
    )
  )
}
