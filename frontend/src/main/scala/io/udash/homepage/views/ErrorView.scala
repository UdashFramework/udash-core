package io.udash.homepage.views

import io.udash._
import io.udash.homepage.IndexState
import org.scalajs.dom.Element

object ErrorViewPresenter extends DefaultViewPresenterFactory[IndexState](() => new ErrorView)

class ErrorView extends View {
  import scalatags.JsDom.all._

  private val content = h3(
    "URL not found!"
  ).render

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}