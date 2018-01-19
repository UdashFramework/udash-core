package io.udash.web.styles

import io.udash.css.CssFileRenderer
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.{FooterStyles, MobileMenuStyles}
import io.udash.web.commons.styles.utils.CommonStyleUtils
import io.udash.web.guide.styles.GuideDefaultStyles
import io.udash.web.guide.styles.demo.{ExampleKeyframes, ExampleStyles}
import io.udash.web.guide.styles.partials.{GuideStyles, HeaderStyles => GuideHeaderStyles, MenuStyles => GuideMenuStyles}
import io.udash.web.guide.styles.utils.GuideStyleUtils
import io.udash.web.homepage.styles.HomepageDefaultStyles
import io.udash.web.homepage.styles.partials.{HomepageStyles, ButtonsStyle => HomeButtonsStyle, DemoStyles => HomeDemoStyles, HeaderStyles => HomeHeaderStyles}

import scalacss.internal.{Renderer, StringRenderer}

class CssRenderer(renderPretty: Boolean) {
  private val renderer: Renderer[String] =
    if (renderPretty) StringRenderer.defaultPretty
    else StringRenderer.formatTiny

  def renderHomepage(path: String): Unit = {
    new CssFileRenderer(path,
      Seq(
        GlobalStyles,
        FooterStyles,
        MobileMenuStyles,
        CommonStyleUtils,
        HomepageDefaultStyles,
        HomeButtonsStyle,
        HomeDemoStyles,
        HomeHeaderStyles,
        HomepageStyles
      ), createMain = true
    ).render()(renderer)
  }

  def renderGuide(path: String): Unit = {
    new CssFileRenderer(path,
      Seq(
        CommonStyleUtils,
        GlobalStyles,
        FooterStyles,
        MobileMenuStyles,
        GuideDefaultStyles,
        GuideStyleUtils,
        GuideMenuStyles,
        GuideHeaderStyles,
        GuideStyles,
        ExampleKeyframes,
        ExampleStyles
      ), createMain = true
    ).render()(renderer)
  }
}

object HomepageCssRenderer {
  def main(args: Array[String]): Unit = {
    require(args.length == 2, " Expected two arguments: target path and pretty print flag")
    new CssRenderer(java.lang.Boolean.parseBoolean(args(1))).renderHomepage(args(0))
  }
}

object GuideCssRenderer {
  def main(args: Array[String]): Unit = {
    require(args.length == 2, " Expected two arguments: target path and pretty print flag")
    new CssRenderer(java.lang.Boolean.parseBoolean(args(1))).renderGuide(args(0))
  }
}