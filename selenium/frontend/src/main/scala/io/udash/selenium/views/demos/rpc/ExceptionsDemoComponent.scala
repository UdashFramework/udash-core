package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.css.CssView
import io.udash.i18n._
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.GuideExceptions
import org.scalajs.dom.ext.LocalStorage
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.concurrent.duration.DurationLong
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class ExceptionsDemoModel(
  exception: String,
  translatableException: TranslationKey0,
  unknownException: String
)
object ExceptionsDemoModel extends HasModelPropertyCreator[ExceptionsDemoModel]

class ExceptionsDemoComponent extends CssView {
  def getTemplate: Modifier = ExceptionsDemoViewFactory()

  object ExceptionsDemoViewFactory {
    def apply(): Modifier = {
      val model = ModelProperty[ExceptionsDemoModel](ExceptionsDemoModel("", TranslationKey.untranslatable(""), ""))
      val presenter = new ExceptionsDemoPresenter(model)
      new ExceptionsDemoView(model, presenter).render
    }
  }

  class ExceptionsDemoPresenter(model: ModelProperty[ExceptionsDemoModel]) {
    def exceptionCall(): Unit = {
      Launcher.serverRpc.demos().exceptions().example() onComplete {
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
      Launcher.serverRpc.demos().exceptions().exampleWithTranslatableError() onComplete {
        case Success(_) => throw new RuntimeException("It should fail!")
        case Failure(ex) => model.subProp(_.translatableException).set(ex match {
          case ex: GuideExceptions.TranslatableExampleException => ex.trKey
          case _ => null
        })
      }
    }

    def unknownExceptionCall(): Unit = {
      Launcher.serverRpc.demos().exceptions().unknownError() onComplete {
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

    implicit val translationProvider: TranslationProvider = new RemoteTranslationProvider(
      Launcher.serverRpc.demos().translations(), Some(LocalStorage), 6 hours
    )
    implicit val lang: Lang = Lang("en")

    private val exceptionButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("exception-demo")
    )("Call registered exception!")

    private val translatableExceptionButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("translatable-exception-demo")
    )("Call registered translatable exception!")

    private val unknownExceptionButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("unknown-exception-demo")
    )("Call unknown exception!")

    exceptionButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        btn.disabled.set(true)
        presenter.exceptionCall()
    }

    translatableExceptionButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        btn.disabled.set(true)
        presenter.translatableExceptionCall()
    }

    unknownExceptionButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        btn.disabled.set(true)
        presenter.unknownExceptionCall()
    }

    def render: Modifier = span(
      UdashInputGroup()(
        UdashInputGroup.addon(
          "Result: ",
          produce(model.subProp(_.exception))(v => span(id := "exception-demo-response", v).render)
        ),
        UdashInputGroup.buttons(exceptionButton.render)
      ).render, br,
      UdashInputGroup()(
        UdashInputGroup.addon(
          "Result: ",
          produce(model.subProp(_.translatableException))(v => span(id := "translatable-exception-demo-response")(translated(v())).render)
        ),
        UdashInputGroup.buttons(translatableExceptionButton.render)
      ).render, br,
      UdashInputGroup()(
        UdashInputGroup.addon(
          "Result: ",
          produce(model.subProp(_.unknownException))(v => span(id := "unknown-exception-demo-response", v).render)
        ),
        UdashInputGroup.buttons(unknownExceptionButton.render)
      ).render
    )
  }
}
