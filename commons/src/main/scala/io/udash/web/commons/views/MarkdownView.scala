package io.udash.web.commons.views

import com.avsystem.commons._
import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.web.guide.markdown.{MarkdownPage, MarkdownPageRPC}
import io.udash.web.guide.styles.MarkdownStyles

import scala.util.{Failure, Success}

trait MarkdownPageState extends State {
  def page: MarkdownPage
}

final case class MarkdownPageViewFactory[T <: MarkdownPageState]()(
  rpc: MarkdownPageRPC
) extends AbstractCase with ViewFactory[T] {
  override def create(): (MarkdownView, MarkdownPresenter[T]) = {
    val content: Property[String] = Property("")
    (new MarkdownView(content), new MarkdownPresenter[T](content, rpc))
  }
}

final class MarkdownPresenter[T <: MarkdownPageState](
  content: Property[String],
  rpc: MarkdownPageRPC
) extends Presenter[T] {
  override def handleState(state: T): Unit = {
    rpc.loadContent(state.page).onCompleteNow {
      case Success(rawHtml) => content.set(rawHtml)
      case Failure(exception) => content.set(exception.toString)
    }
  }
}

final class MarkdownView(content: ReadableProperty[String]) extends View {
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  override val getTemplate: Modifier =
    produce(content)(content => div(MarkdownStyles.markdownPage)(raw(content)).render)
}
