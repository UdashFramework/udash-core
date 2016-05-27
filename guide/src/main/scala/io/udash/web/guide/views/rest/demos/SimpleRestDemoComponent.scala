package io.udash.web.guide.views.rest.demos

import io.udash.web.guide.Context
import io.udash._
import io.udash.web.guide.demos.rest.RestExampleClass
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom

class SimpleRestDemoComponent extends Component {
  import Context._

  override def getTemplate: Element = SimpleRestDemoViewPresenter()

  trait ExampleModel {
    def string: String
    def int: Int
    def cls: RestExampleClass
  }

  object SimpleRestDemoViewPresenter {
    def apply(): Element = {
      val responsesModel = ModelProperty[ExampleModel]

      val presenter = new SimpleRestDemoPresenter(responsesModel)
      new SimpleRestDemoView(responsesModel, presenter).render
    }
  }

  class SimpleRestDemoPresenter(model: ModelProperty[ExampleModel]) {
    def sendStringRequest(target: JQuery) = {
      target.attr("disabled", "true")
      Context.restServer.simple().string() onComplete {
        case Success(response) =>
          model.subProp(_.string).set(response)
        case Failure(ex) =>
          model.subProp(_.string).set(s"Error: $ex!")
      }
    }

    def sendIntRequest(target: JQuery) = {
      target.attr("disabled", "true")
      Context.restServer.simple().int() onComplete {
        case Success(response) =>
          model.subProp(_.int).set(response)
        case Failure(ex) =>
          model.subProp(_.int).set(-1)
      }
    }

    def sendClassRequest(target: JQuery) = {
      target.attr("disabled", "true")
      Context.restServer.simple().cls() onComplete {
        case Success(response) =>
          model.subProp(_.cls).set(response)
        case Failure(ex) =>
          model.subProp(_.cls).set(null)
      }
    }
  }

  class SimpleRestDemoView(model: ModelProperty[ExampleModel], presenter: SimpleRestDemoPresenter) {
    import JsDom.all._
    import scalacss.Defaults._
    import scalacss.ScalatagsCss._

    // TODO migrate to bootstrap components
    def render: Element = span(GuideStyles.frame, id := "simple-rest-demo")(
      button(id := "simple-rest-demo-string-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendStringRequest(jQ(ev.target))
          true
        })
      )("Get string"),
      button(id := "simple-rest-demo-int-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendIntRequest(jQ(ev.target))
          true
        })
      )("Get integer"),
      button(id := "simple-rest-demo-class-btn", BootstrapStyles.Button.btn, BootstrapStyles.Button.btnPrimary)(
        onclick :+= ((ev: MouseEvent) => {
          presenter.sendClassRequest(jQ(ev.target))
          true
        })
      )("Get class"),
      div("String: ", bind(model.subProp(_.string))),
      div("Int: ", bind(model.subProp(_.int))),
      div("Class: ", bind(model.subProp(_.cls)))
    ).render
  }
}
