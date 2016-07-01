package io.udash.web.guide.views

import io.udash._
import io.udash.web.commons.components.Footer
import io.udash.web.commons.styles.{GlobalStyles, StyleRegistry}
import io.udash.web.guide.RootState
import io.udash.web.guide.components.Header
import io.udash.web.guide.styles.GuideDefaultStyles
import io.udash.web.guide.styles.partials.{GuideStyles, HeaderStyles, MenuStyles}
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLStyleElement

import scalatags.JsDom.TypedTag

object RootViewPresenter extends DefaultViewPresenterFactory[RootState.type](() => new RootView)

class RootView extends ViewContainer {
  import scalacss.Defaults._
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._

  protected val child = div().render
  private val defaultStyles = GuideDefaultStyles.get

  private val content = div(
    StyleRegistry.styleSheet,
    Header.getTemplate,
    child,
    Footer.getTemplate
  ).render

  override def getTemplate: Element = content
}