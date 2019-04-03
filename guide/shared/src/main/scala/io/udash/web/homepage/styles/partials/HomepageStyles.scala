package io.udash.web.homepage.styles.partials

import io.udash.css.{CssBase, CssStyle}
import io.udash.web.commons.styles.components.CodeBlockStyles
import io.udash.web.commons.styles.utils._
import scalacss.internal.Literal

import scala.language.postfixOps

object HomepageStyles extends CssBase with CodeBlockStyles {
  import dsl._

  val section: CssStyle = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  val sectionIntro: CssStyle = style(
    section,
    height(100 vh),
    color.white,
    backgroundColor.black,
    backgroundImage := "url(/assets/images/intro_bg.jpg)",
    backgroundSize := Literal.cover,

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      height.auto,
      paddingTop(200 px),
      paddingBottom(100 px)
    ),

    MediaQueries.tabletLandscape(
      height.auto,
      paddingTop(200 px),
      paddingBottom(100 px)
    ),

    MediaQueries.tabletPortrait(
      paddingTop(150 px).important,
      paddingBottom(50 px)
    )
  )

  val introInner: CssStyle = style(
    CommonStyleUtils.relativeMiddle,
    top(55 %%),
    transform := "translate3d(0, 0, 1)",

    media.minHeight(1 px).maxHeight(650 px)(
      top(60 %%)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      top(auto),
      transform := "translateY(0)"
    ),

    MediaQueries.tabletLandscape(
      top(auto),
      transform := "translateY(0)"
    )
  )

  val introScala: CssStyle = style(
    CommonStyleUtils.transition(),
    padding(.125 rem, 1.5 rem,.1875 rem,.625 rem),
    color.white,
    fontSize(1 rem),
    backgroundColor(StyleConstants.Colors.RedDark),

    &.hover (
      textDecoration := none
    ),

    MediaQueries.desktop(
      &.hover (
        backgroundColor(StyleConstants.Colors.Red),

        unsafeChild(s".${introScalaIcon.className}")(
          transform := "translateX(2px) translateY(-2px)"
        )
      )
    )
  )

  lazy val introScalaIcon: CssStyle = style(
    CommonStyleUtils.transition(),
    position.relative,
    width(10 px),
    display.inlineBlock,
    marginRight(.625 rem),

    unsafeChild("svg") (
      svgFill := c"#fff"
    )
  )

  val introHead: CssStyle = style(
    UdashFonts.roboto(FontWeight.Bold),
    fontSize(5.5 rem),
    lineHeight(1.1),
    marginTop(3.125 rem),
    marginBottom(4.6875 rem),
    transform := "translate3d(0, 0, 1)",
    textShadow := "0 0 15px black",

    &.after(
      content := "\" \"",
      position.absolute,
      bottom(-40 px),
      right(-30 px),
      width(400 px),
      height(213 px),
      backgroundImage := "url(/assets/images/intro_bird.png)",
      backgroundSize := "100%",

      media.minHeight(1 px).maxHeight(850 px)(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-20 px)
      ),

      MediaQueries.tabletLandscape(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-20 px),
        display.none
      )
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(850 px)(
      marginTop(1.25 rem),
      marginBottom(1.875 rem)
    ),

    media.minHeight(751 px).maxHeight(850 px)(
      marginTop(1.25 rem),
      marginBottom(1.875 rem),
      fontSize(5 rem)
    ),

    media.minHeight(651 px).maxHeight(750 px)(
      fontSize(3.75 rem)
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(650 px)(
      fontSize(3.125 rem)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      fontSize(3.75 rem),
      marginTop(2.5 rem),
      marginBottom(3.75 rem)
    ),

    MediaQueries.tabletLandscape(
      fontSize(5 rem).important
    ),

    MediaQueries.tabletLandscape(
      marginTop(1.875 rem).important,
      marginBottom(3.75 rem).important,
      fontSize(3.75 rem).important
    ),

    MediaQueries.phone(
      fontSize(2.5 rem).important,
      lineHeight(1.2)
    )
  )

  val boxList: CssStyle = style(
    position.relative,
    textAlign.center
  )

  private val boxListItem: CssStyle = mixin(
    CommonStyleUtils.border(),
    position.relative,
    display.inlineBlock,
    verticalAlign.top
  )

  private val boxListHead: CssStyle = mixin(
    UdashFonts.roboto(FontWeight.Medium),
    position.relative,
    display.block,
    paddingBottom(3.4375 rem),
    margin(`0`),

    &.after(
      UdashFonts.roboto(FontWeight.Light),
      content := "\"—\"",
      position.absolute,
      width(100 %%),
      bottom(`0`),
      left(`0`),
      fontSize(1.625 rem),
      textAlign.center
    )
  )

  private val boxListDescription: CssStyle = mixin(
    marginTop(.3125 rem)
  )

  val featuresListItem: CssStyle = style(
    boxListItem,
    width :=! "calc((100% - 30px) / 3)",
    minHeight(31.25 rem),
    padding(15.625 rem, 1.25 rem, 1.25 rem, 1.25 rem),

    &.nthChild(2)(
      margin(`0`, 15 px)
    ),

    MediaQueries.tabletLandscape(
      width :=! "calc((100% - 15px) / 2)",
      margin(`0`, `0`, 15 px, `0`),

      &.nthChild(2)(
        margin(`0`, `0`, 15 px, `0`)
      ),

      &.firstChild(
        marginRight(15 px)
      )
    ),

    MediaQueries.tabletPortrait(
      width(100 %%),
      minHeight(`0`),
      padding(12.5 rem, 1.25 rem, 1.25 rem, 1.25 rem),
      margin(`0`),
      borderBottomStyle.none,

      &.nthChild(2)(
        margin(`0`)
      ),

      &.firstChild(
        margin(`0`)
      ),

      &.lastChild(
        borderBottomStyle.solid
      )
    )
  )

  val featuresListIcon: CssStyle = style(
    display.block,
    position.absolute,
    width(100 %%),
    top(4.6875 rem),
    left(`0`),

    MediaQueries.tabletPortrait(
      top(1.875 rem)
    )
  )

  val featuresListHead: CssStyle = style(
    boxListHead,
    fontSize(2 rem)
  )

  val featuresListHeadInner: CssStyle = style(
    UdashFonts.roboto(FontWeight.Thin, FontStyle.Italic),
    position.absolute,
    top(2.625 rem),
    display.block,
    width(100 %%),
    fontSize(.875 rem)
  )

  val moreList: CssStyle = style(
    boxList,
    paddingBottom(1 px)
  )

  val moreListItem: CssStyle = style(
    CommonStyleUtils.border(),
    marginLeft(-1 px),
    marginBottom(-1 px),
    boxListItem,
    width :=! "calc(100% / 3)",
    minHeight(22.5 rem),
    padding(6.5625 rem, 1.25 rem, 1.25 rem, 1.25 rem),


    MediaQueries.tabletLandscape(
      width(50 %%),

      &.nthChild("odd").lastChild(
        width(100 %%)
      )
    ),

    MediaQueries.tabletPortrait(
      width(100 %%),
      minHeight(`0`),
      padding(2.5 rem, 1.25 rem, 1.25 rem, 1.25 rem),
      borderBottomStyle.none,

      &.nthChild(2)(
        borderLeftStyle.solid
      ),

      &.lastChild(
        borderBottomStyle.solid,
        borderTopStyle.solid
      )
    )
  )


  val moreListItemTwoLineTitle: CssStyle = style(
    paddingTop(57 px)
  )

  val moreListHead: CssStyle = style(
    boxListHead,
    fontSize(2.5 rem)
  )

  val moreListDescription: CssStyle = style(
    boxListDescription,
    lineHeight(1.6)
  )

  val sectionDemo: CssStyle = style(
    section,
    paddingBottom(9.375 rem),
    textAlign.center,

    MediaQueries.tabletLandscape(
      paddingBottom(6.25 rem)
    ),

    MediaQueries.phone(
      paddingBottom(3.125 rem)
    )
  )

  val demoDescription: CssStyle = style(
    UdashFonts.roboto(FontWeight.Thin),
    fontSize(2 rem),
    marginBottom(2.1875 rem)
  )

  val codeWrapper: CssStyle = style(
    marginTop(.9375 rem),
    marginBottom(.9375 rem),
    paddingTop(.625 rem),
    paddingBottom(.625 rem)
  )

  val codeBlock: CssStyle = style(
    counterReset := "code",
    listStyleType := "decimal",
    listStylePosition.outside,
    fontFamily :=! "Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace",
    color(StyleConstants.Colors.Grey),
    paddingLeft(2.5 rem),
    fontSize(.875 rem),


    unsafeChild(":not(pre) > code[class*=\"language-\"]") (
      paddingRight(30 px),
      whiteSpace.pre
    )
  )

  val sectionError: CssStyle = style(
    sectionIntro,
    height :=! s"calc(100vh - 120px)",

    MediaQueries.tabletPortrait(
      paddingTop(80 px).important
    )
  )

  val errorInner: CssStyle = style(
    introInner,
    top(50 %%)
  )

  val errorHead: CssStyle = style(
    introHead,
    margin(`0`),

    &.after(
      bottom(-110 px),

      media.minHeight(1 px).maxHeight(850 px)(
        bottom(-80 px)
      ),

      MediaQueries.tabletLandscape(
        bottom(-80 px)
      )
    )

  )
}
