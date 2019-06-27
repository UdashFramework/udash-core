package io.udash.web.commons.views

import com.avsystem.commons._
import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.alert.UdashAlert
import io.udash.web.guide.markdown.{MarkdownPage, MarkdownPageRPC}
import io.udash.web.guide.styles.MarkdownStyles

import scala.util.{Failure, Success}

trait MarkdownPageState extends State {
  def page: MarkdownPage
}

final case class MarkdownModel(
  content: String = "",
  error: String = ""
)
object MarkdownModel extends HasModelPropertyCreator[MarkdownModel] {
  implicit val blank: Blank[MarkdownModel] = Blank.Simple(apply())
}

final case class MarkdownPageViewFactory[T <: MarkdownPageState]()(
  rpc: MarkdownPageRPC
) extends AbstractCase with ViewFactory[T] {
  override def create(): (MarkdownView, MarkdownPresenter[T]) = {
    val model: ModelProperty[MarkdownModel] = ModelProperty.blank
    (new MarkdownView(model), new MarkdownPresenter[T](model, rpc))
  }
}

final class MarkdownPresenter[T <: MarkdownPageState](
  model: ModelProperty[MarkdownModel],
  rpc: MarkdownPageRPC
) extends Presenter[T] {
  override def handleState(state: T): Unit = {
    model.set(MarkdownModel.blank.value)
    rpc.loadContent(state.page).onCompleteNow {
      case Success(rawHtml) => model.subProp(_.content).set(rawHtml)
      case Failure(exception) => model.subProp(_.error).set(exception.toString)
    }
  }
}

final class MarkdownView(model: ReadableModelProperty[MarkdownModel]) extends View {
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  override val getTemplate: Modifier = ISeq(
    produce(model.roSubProp(_.error)) { error =>
      error.opt.filter(_.nonEmpty).map(e =>
        div(cls := "bootstrap")(
          h1("Oops! Something went wrong :("),
          p("An error occurred during rendering your page:"),
          UdashAlert(alertStyle = BootstrapStyles.Color.Danger.toProperty)(e).render
        ).render
      ).toList
    },
    produce(model.roSubProp(_.content)) { content =>
      content.opt.filter(_.nonEmpty).map(c =>
        div(MarkdownStyles.markdownPage)(raw(c)).render
      ).toList
    }
  )
}
