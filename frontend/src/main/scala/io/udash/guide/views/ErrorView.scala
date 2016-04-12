package io.udash.guide.views

import io.udash._
import io.udash.guide.ErrorState
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom._

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._
import scalacss.Defaults._

object ErrorViewPresenter extends DefaultViewPresenterFactory[ErrorState.type](() => new ErrorView)

class ErrorView extends View {
  override def getTemplate: Element = div(
    h3("Error - url not found!")
  ).render
  override def renderChild(view: View): Unit = ()
}