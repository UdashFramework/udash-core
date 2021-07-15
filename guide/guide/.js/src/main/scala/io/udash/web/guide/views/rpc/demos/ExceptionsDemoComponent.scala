package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.i18n._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, GuideExceptions}
import org.scalajs.dom.ext.LocalStorage
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.duration.DurationLong
import scala.language.postfixOps
import scala.util.{Failure, Success}

case class ExceptionsDemoModel(
  exception: String,
  translatableException: TranslationKey0,
  unknownException: String
)
object ExceptionsDemoModel extends HasModelPropertyCreator[ExceptionsDemoModel]

class ExceptionsDemoComponent extends Component {
  import Context._
  override def getTemplate: Modifier = ExceptionsDemoViewFactory()

  object ExceptionsDemoViewFactory {
    def apply(): Modifier = {
      val model = ModelProperty[ExceptionsDemoModel](ExceptionsDemoModel("", TranslationKey.untranslatable(""), ""))
      val presenter = new ExceptionsDemoPresenter(model)
      new ExceptionsDemoView(model, presenter).render
    }
  }

  class ExceptionsDemoPresenter(model: ModelProperty[ExceptionsDemoModel]) {
    def exceptionCall(): Unit = {
      Context.serverRpc.demos.exceptions.example() onComplete {
        case Success(_) => throw new RuntimeException("It should fail!")
        case Failure(ex) => model.subProp(_.exception).set(ex match {
          case ex: GuideExceptions.ExampleException =>
            ex.printStackTrace()
            s"ExampleException: ${ex.msg}"
          case _ => s"UnknownException: ${ex.getMessage}"
        })
      }
    }

    def translatableExceptionCall(): Unit = {
      Context.serverRpc.demos.exceptions.exampleWithTranslatableError() onComplete {
        case Success(_) => throw new RuntimeException("It should fail!")
        case Failure(ex) => model.subProp(_.translatableException).set(ex match {
          case ex: GuideExceptions.TranslatableExampleException => ex.trKey
          case _ => null
        })
      }
    }

    def unknownExceptionCall(): Unit = {
      Context.serverRpc.demos.exceptions.unknownError() onComplete {
        case Success(_) => throw new RuntimeException("It should fail!")
        case Failure(ex) => model.subProp(_.unknownException).set(ex match {
          case ex: GuideExceptions.ExampleException => s"ExampleException: ${ex.msg}"
          case ex: GuideExceptions.TranslatableExampleException => s"TranslatableExampleException: ${ex.trKey.key}"
          case _ => s"UnknownException: ${ex.getMessage}"
        })
      }
    }
  }

  class ExceptionsDemoView(model: ModelProperty[ExceptionsDemoModel], presenter: ExceptionsDemoPresenter) {
    import JsDom.all._

    implicit val translationProvider: TranslationProvider = new RemoteTranslationProvider(serverRpc.demos.translations, Some(LocalStorage), 6 hours)
    implicit val lang: Lang = Lang("en")

    private val exceptionButtonDisabled = Property(false)
    private val exceptionButton = UdashButton(
      disabled = exceptionButtonDisabled,
      componentId = ComponentId("exception-demo")
    )(_ => "Call registered exception!")

    private val translatableExceptionButton = UdashButton(
      disabled = exceptionButtonDisabled,
      componentId = ComponentId("translatable-exception-demo")
    )(_ => "Call registered translatable exception!")

    private val unknownExceptionButtonDisabled = Property(false)
    private val unknownExceptionButton = UdashButton(
      disabled = unknownExceptionButtonDisabled,
      componentId = ComponentId("unknown-exception-demo")
    )(_ => "Call unknown exception!")

    exceptionButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        exceptionButtonDisabled.set(true)
        presenter.exceptionCall()
    }

    translatableExceptionButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        exceptionButtonDisabled.set(true)
        presenter.translatableExceptionCall()
    }

    unknownExceptionButton.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        unknownExceptionButtonDisabled.set(true)
        presenter.unknownExceptionCall()
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      UdashInputGroup()(
        UdashInputGroup.prependText(
          "Result: ",
          produce(model.subProp(_.exception))(v => span(id := "exception-demo-response", v).render)
        ),
        exceptionButton.render
      ).render, br,
      UdashInputGroup()(
        UdashInputGroup.prependText(
          "Result: ",
          produce(model.subProp(_.translatableException))(v => span(id := "translatable-exception-demo-response")(v.translated()).render)
        ),
        translatableExceptionButton.render
      ).render, br,
      UdashInputGroup()(
        UdashInputGroup.prependText(
          "Result: ",
          produce(model.subProp(_.unknownException))(v => span(id := "unknown-exception-demo-response", v).render)
        ),
        unknownExceptionButton.render
      ).render
    )
  }
}
