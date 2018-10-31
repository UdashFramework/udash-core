package io.udash.web.commons.views

import io.udash._
import io.udash.web.guide.markdown.{MarkdownPage, MarkdownPageRPC}
import io.udash.web.guide.styles.MarkdownStyles
import org.scalajs.dom.Element

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait MarkdownPageState extends State {
  def page: MarkdownPage
}

class MarkdownPageViewFactory[T <: MarkdownPageState](rpc: MarkdownPageRPC) extends ViewFactory[T] {
  override def create(): (MarkdownView, MarkdownPresenter[T]) = {
    val content: Property[String] = Property("")
    (new MarkdownView(content), new MarkdownPresenter[T](content, rpc))
  }
}

class MarkdownPresenter[T <: MarkdownPageState](
  content: Property[String],
  rpc: MarkdownPageRPC
) extends Presenter[T] {
  override def handleState(state: T): Unit = {
    rpc.loadContent(state.page).onComplete {
      case Success(rawHtml) => content.set(rawHtml)
      case Failure(exception) => content.set(exception.toString)
    }
  }
}

class MarkdownView(content: Property[String]) extends View {
  import scalatags.JsDom.all._
  import io.udash.css.CssView._

  private val container: Element = div(MarkdownStyles.markdownPage)().render

  content.listen(container.innerHTML = _, initUpdate = true)

  override val getTemplate: Modifier = container
}
