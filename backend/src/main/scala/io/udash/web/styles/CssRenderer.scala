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

object CssRenderer {
  implicit val renderer: Renderer[String] = StringRenderer.defaultPretty

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
    ).render()
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
    ).render()
  }
}
