package io.udash.web.homepage.styles.partials

import io.udash.web.commons.styles.components.CodeBlockStyles
import io.udash.web.commons.styles.utils._

import scala.language.postfixOps
import scalacss.Compose
import scalacss.Defaults._

object HomepageStyles extends StyleSheet.Inline with CodeBlockStyles {
  import dsl._

  val section = style(
    position.relative,
    width(100 %%),
    overflow.hidden
  )

  val sectionIntro = style(
    section,
    height(100 vh),
    color.white,
    backgroundColor.black,
    backgroundImage := "url(assets/images/intro_bg.jpg)",
    backgroundSize := "cover",

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      height.auto,
      paddingTop(200 px),
      paddingBottom(100 px)
    ),

    MediaQueries.tabletLandscape(
      style(
        height.auto,
        paddingTop(200 px),
        paddingBottom(100 px)
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        paddingTop(150 px).important,
        paddingBottom(50 px)

      )
    )
  )

  val introInner = style(
    StyleUtils.relativeMiddle,
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
      style(
        top(auto),
        transform := "translateY(0)"
      )
    )
  )(Compose.trust)

  val introScala = style(
    StyleUtils.transition(),
    padding(.2 rem, 2.4 rem, .3 rem, 1 rem),
    color.white,
    fontSize(1.6 rem),
    backgroundColor(StyleConstants.Colors.RedDark),

    &.hover (
      textDecoration := "none"
    ),

    MediaQueries.desktop(
      style(
        &.hover (
          backgroundColor(StyleConstants.Colors.Red),

          unsafeChild(s".${introScalaIcon.htmlClass}")(
            transform := "translateX(2px) translateY(-2px)"
          )
        )
      )
    )
  )

  lazy val introScalaIcon = style(
    StyleUtils.transition(),
    position.relative,
    width(10 px),
    display.inlineBlock,
    marginRight(1 rem),

    unsafeChild("svg") (
      svgFill := c"#fff"
    )
  )

  val introHead = style(
    UdashFonts.acumin(FontWeight.SemiBold),
    fontSize(8.8 rem),
    lineHeight(1.1),
    marginTop(5 rem),
    marginBottom(7.5 rem),
    transform := "translate3d(0, 0, 1)",
    textShadow := "0 0 15px black",

    &.after(
      content := "\" \"",
      position.absolute,
      bottom(-40 px),
      right(-30 px),
      width(400 px),
      height(213 px),
      backgroundImage := "url(assets/images/intro_bird.png)",
      backgroundSize := "100%",

      media.minHeight(1 px).maxHeight(850 px)(
        width(400 * .7 px),
        height(213 * .7 px),
        bottom(-20 px)
      ),

      MediaQueries.tabletLandscape(
        style(
          width(400 * .7 px),
          height(213 * .7 px),
          bottom(-20 px)
        )
      ),

      MediaQueries.tabletLandscape(
        style(
          display.none
        )
      )
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(850 px)(
      marginTop(2 rem),
      marginBottom(3 rem)
    ),

    media.minHeight(751 px).maxHeight(850 px)(
      marginTop(2 rem),
      marginBottom(3 rem),
      fontSize(8 rem)
    ),

    media.minHeight(651 px).maxHeight(750 px)(
      fontSize(6 rem)
    ),

    media.minHeight(StyleConstants.Sizes.MinSiteHeight px).maxHeight(650 px)(
      fontSize(5 rem)
    ),

    media.minHeight(1 px).maxHeight(StyleConstants.Sizes.MinSiteHeight - 1 px)(
      fontSize(6 rem),
      marginTop(4 rem),
      marginBottom(6 rem)
    ),

    MediaQueries.tabletLandscape(
      style(
        fontSize(8 rem).important
      )
    ),

    MediaQueries.tabletLandscape(
      style(
        marginTop(3 rem).important,
        marginBottom(6 rem).important,
        fontSize(6 rem).important
      )
    ),

    MediaQueries.phone(
      style(
        fontSize(4 rem).important,
        lineHeight(1.2)
      )
    )
  )(Compose.trust)

  val boxList = style(
    position.relative,
    textAlign.center
  )

  val boxListItem = style(
    StyleUtils.border(),
    position.relative,
    display.inlineBlock,
    verticalAlign.top
  )

  val boxListHead = style(
    UdashFonts.acumin(FontWeight.Medium),
    position.relative,
    display.block,
    paddingBottom(5.5 rem),
    margin(`0`),

    &.after(
      UdashFonts.acumin(FontWeight.Light),
      content := "\"—\"",
      position.absolute,
      width(100 %%),
      bottom(`0`),
      left(`0`),
      fontSize(2.6 rem),
      textAlign.center
    )
  )

  val boxListDescription = style(
    marginTop(.5 rem)
  )

  val featuresListItem = style(
    boxListItem,
    width :=! "calc((100% - 30px) / 3)",
    minHeight(50 rem),
    padding(25 rem, 2 rem, 2 rem , 2 rem),

    &.nthChild(2)(
      margin(`0`, 15 px)
    ),

    MediaQueries.tabletLandscape(
      style(
        width :=! "calc((100% - 15px) / 2)",
        margin(`0`, `0`, 15 px, `0`),

        &.nthChild(2)(
          margin(`0`, `0`, 15 px, `0`)
        ),

        &.firstChild(
          marginRight(15 px)
        )
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        width(100 %%),
        minHeight(`0`),
        padding(20 rem, 2 rem, 4 rem , 2 rem),
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
  )

  val featuresListIcon = style(
    display.block,
    position.absolute,
    width(100 %%),
    top(7.5 rem),
    left(`0`),

    MediaQueries.tabletPortrait(
      style(
        top(3 rem)
      )
    )
  )

  val featuresListHead = style(
    boxListHead,
    fontSize(3.2 rem)
  )

  val featuresListHeadInner = style(
    UdashFonts.acumin(FontWeight.ExtraLight, FontStyle.Italic),
    position.absolute,
    top(4.2 rem),
    display.block,
    width(100 %%),
    fontSize(1.4 rem)
  )

  val moreListItem = style(
    boxListItem,
    width :=! "calc(100% / 3)",
    minHeight(36 rem),
    padding(10.5 rem, 2 rem, 2 rem , 2 rem),

    &.nthChild(2)(
      borderLeftStyle.none,
      borderRightStyle.none
    ),

    MediaQueries.tabletLandscape(
      style(
        width(50 %%),
        padding(4 rem, 2 rem, 4 rem , 2 rem),
        minHeight(`0`),

        &.nthChild(2)(
          borderRightStyle.solid
        ),

        &.lastChild(
          width(100 %%),
          borderTopStyle.none
      )
      )
    ),

    MediaQueries.tabletPortrait(
      style(
        width(100 %%),
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
  )

  val moreListHead = style(
    boxListHead,
    fontSize(4 rem)
  )

  val moreListDescription = style(
    boxListDescription,
    lineHeight(1.6)
  )

  val sectionDemo = style(
    section,
    paddingBottom(15 rem),
    textAlign.center,

    MediaQueries.tabletLandscape(
      style(
        paddingBottom(10 rem)
      )
    ),

    MediaQueries.phone(
      style(
        paddingBottom(5 rem)
      )
    )
  )

  val demoDescription = style(
    UdashFonts.acumin(FontWeight.ExtraLight),
    fontSize(3.2 rem),
    marginBottom(3.5 rem)
  )

  val codeWrapper = style(
    marginTop(1.5 rem),
    marginBottom(1.5 rem),
    paddingTop(1 rem),
    paddingBottom(1 rem)
  )

  val codeBlock = style(
    counterReset := "code",
    listStyleType := "decimal",
    listStylePosition.outside,
    fontFamily :=! "Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace",
    color(StyleConstants.Colors.Grey),
    paddingLeft(4 rem),
    fontSize(1.4 rem),


    unsafeChild(":not(pre) > code[class*=\"language-\"]") (
      paddingRight(30 px),
      whiteSpace.pre
    )
  )

  val sectionError = style(
    sectionIntro,
    height :=! s"calc(100vh - 120px)",

    MediaQueries.tabletPortrait(
      style(
        paddingTop(80 px).important
      )
    )
  )(Compose.trust)

  val errorInner = style(
    introInner,
    top(50 %%)
  )(Compose.trust)

  val errorHead = style(
    introHead,
    margin(`0`),

    &.after(
      bottom(-110 px),

      media.minHeight(1 px).maxHeight(850 px)(
        bottom(-80 px)
      ),

      MediaQueries.tabletLandscape(
        style(
          bottom(-80 px)
        )
      )
    )

  )(Compose.trust)
}
