package io.udash.web.guide.views

import io.udash._
import io.udash.web.guide.RootState
import io.udash.web.guide.components.{Footer, GuideMenu, Header}
import io.udash.web.guide.styles.partials.{FooterStyles, GuideStyles, HeaderStyles, MenuStyles}
import io.udash.web.guide.styles.GlobalStyles
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLStyleElement

import scalatags.JsDom.tags2._
import scalatags.JsDom.TypedTag

object RootViewPresenter extends DefaultViewPresenterFactory[RootState.type](() => new RootView)

class RootView extends ViewContainer {
  import scalacss.Defaults._
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._

  protected val child = div().render

  private val content = div(
    GlobalStyles.render[TypedTag[HTMLStyleElement]],
    GuideStyles.render[TypedTag[HTMLStyleElement]],
    MenuStyles.render[TypedTag[HTMLStyleElement]],
    HeaderStyles.render[TypedTag[HTMLStyleElement]],
    FooterStyles.render[TypedTag[HTMLStyleElement]],

    Header.getTemplate,
    child,
    Footer.getTemplate
  ).render

  override def getTemplate: Element = content
}