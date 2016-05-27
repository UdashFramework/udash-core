package io.udash.web.guide.views.rpc.demos

import io.udash.web.guide.Context
import io.udash._
import io.udash.web.guide.demos.rpc.GenCodecServerRPC
import io.udash.web.guide.styles.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.utils.Logger
import io.udash.wrappers.jquery._
import org.scalajs.dom._

import scala.util.{Failure, Random, Success}
import scalatags.JsDom

trait GenCodecsDemoModel {
  import io.udash.web.guide.demos.rpc.GenCodecServerRPC._
  
  def int: Int
  def double: Double
  def string: String
  def seq: Seq[String]
  def map: Seq[(String, Int)]
  def caseClass: DemoCaseClass
  def clsInt: Int
  def clsString: String
  def clsVar: Int
  def sealedTrait: Fruit
}

class GenCodecsDemoComponent extends Component {
  import io.udash.web.guide.demos.rpc.GenCodecServerRPC._
  import Context._
  
  override def getTemplate: Element = GenCodecsDemoViewPresenter()

  object GenCodecsDemoViewPresenter {
    def apply(): Element = {
      val GenCodecs = ModelProperty[GenCodecsDemoModel]
      val presenter = new GenCodecsDemoPresenter(GenCodecs)
      new GenCodecsDemoView(GenCodecs, presenter).render
    }
  }

  class GenCodecsDemoPresenter(model: ModelProperty[GenCodecsDemoModel]) extends StrictLogging {
    def onButtonClick(target: JQuery) = {
      target.attr("disabled", "true")
      val demoRpc: GenCodecServerRPC = Context.serverRpc.demos().gencodecsDemo()
      demoRpc.sendInt(Random.nextInt()) onComplete {
        case Success(response) => model.subProp(_.int).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendDouble(Random.nextLong().toDouble * 2e20) onComplete {
        case Success(response) => model.subProp(_.double).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendString(Random.nextString(10)) onComplete {
        case Success(response) => model.subProp(_.string).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSeq(Seq(Random.nextString(5), Random.nextString(5))) onComplete {
        case Success(response) => model.subProp(_.seq).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendMap(Map(Random.nextString(5) -> Random.nextInt(), Random.nextString(5) -> Random.nextInt())) onComplete {
        case Success(response) => model.subProp(_.map).set(response.toSeq)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendCaseClass(DemoCaseClass(Random.nextInt(), Random.nextString(5), 42)) onComplete {
        case Success(response) => model.subProp(_.caseClass).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendClass(new DemoClass(Random.nextInt(), Random.nextString(5))) onComplete {
        case Success(response) =>
          model.subProp(_.clsInt).set(response.i)
          model.subProp(_.clsString).set(response.s)
          model.subProp(_.clsVar).set(response._v)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSealedTrait(Seq(Apple, Orange, Banana)(Random.nextInt(3))) onComplete {
        case Success(response) => model.subProp(_.sealedTrait).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
    }
  }

  class GenCodecsDemoView(model: ModelProperty[GenCodecsDemoModel], presenter: GenCodecsDemoPresenter) {
    import JsDom.all._
    import scalacss.ScalatagsCss._

    def render: Element = span(GuideStyles.frame)(
      button(id := "gencodec-demo", BootstrapStyles.btn, BootstrapStyles.btnPrimary)(onclick :+= ((ev: MouseEvent) => {
        presenter.onButtonClick(jQ(ev.target))
        true
      }))("Send request"),
      p(
        ul(
          li("Int: ", produce(model.subProp(_.int))(response => span(id := "gencodec-demo-int", response).render)),
          li("Double: ", produce(model.subProp(_.double))(response => span(id := "gencodec-demo-double", response).render)),
          li("String: ", produce(model.subProp(_.string))(response => span(id := "gencodec-demo-string", response).render)),
          li("Seq[String]: ", produce(model.subProp(_.seq))(response => span(id := "gencodec-demo-seq", response).render)),
          li("Map[String, Int]: ", produce(model.subProp(_.map))(response => span(id := "gencodec-demo-map", response.toString()).render)),
          li("DemoCaseClass: ", produce(model.subProp(_.caseClass))(response => span(id := "gencodec-demo-caseClass", response.toString()).render)),
          li("DemoClass Int: ", produce(model.subProp(_.clsInt))(response => span(id := "gencodec-demo-cls-int", response.toString()).render)),
          li("DemoClass String: ", produce(model.subProp(_.clsString))(response => span(id := "gencodec-demo-cls-string", response.toString()).render)),
          li("DemoClass Var: ", produce(model.subProp(_.clsVar))(response => span(id := "gencodec-demo-cls-var", response.toString()).render)),
          li("Fruit: ", produce(model.subProp(_.sealedTrait))(response => span(id := "gencodec-demo-sealedTrait", response.toString()).render))
        )
      )
    ).render
  }
}
