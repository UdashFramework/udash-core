package io.udash.web.guide.views.rpc.demos

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.logging.CrossLogging
import io.udash.web.commons.views.Component
import io.udash.web.guide.Context
import io.udash.web.guide.demos.rpc.GenCodecServerRPC
import io.udash.web.guide.styles.partials.GuideStyles

import scala.util.{Failure, Random, Success}
import scalatags.JsDom
import scalatags.JsDom.all._

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
object GenCodecsDemoModel extends HasModelPropertyCreator[GenCodecsDemoModel]

class GenCodecsDemoComponent extends Component with CrossLogging {
  import Context._
  import io.udash.web.guide.demos.rpc.GenCodecServerRPC._

  override def getTemplate: Modifier = GenCodecsDemoViewFactory()

  object GenCodecsDemoViewFactory {
    def apply(): Modifier = {
      val GenCodecs = ModelProperty.empty[GenCodecsDemoModel]
      val presenter = new GenCodecsDemoPresenter(GenCodecs)
      new GenCodecsDemoView(GenCodecs, presenter).render
    }
  }

  class GenCodecsDemoPresenter(model: ModelProperty[GenCodecsDemoModel]) {
    def randomString(l: Int): String =
      BigInt.probablePrime(32, Random).toString(16)

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      val demoRpc: GenCodecServerRPC = Context.serverRpc.demos().gencodecsDemo()
      demoRpc.sendInt(Random.nextInt()) onComplete {
        case Success(response) => model.subProp(_.int).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendDouble(Random.nextLong().toDouble * 2e20) onComplete {
        case Success(response) => model.subProp(_.double).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendString(randomString(10)) onComplete {
        case Success(response) => model.subProp(_.string).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSeq(Seq(randomString(5), randomString(5))) onComplete {
        case Success(response) => model.subProp(_.seq).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendMap(Map(randomString(5) -> Random.nextInt(), randomString(5) -> Random.nextInt())) onComplete {
        case Success(response) => model.subProp(_.map).set(response.toSeq)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendCaseClass(DemoCaseClass(Random.nextInt(), randomString(5), 42)) onComplete {
        case Success(response) => model.subProp(_.caseClass).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendClass(new DemoClass(Random.nextInt(), randomString(5))) onComplete {
        case Success(response) =>
          model.subProp(_.clsInt).set(response.i)
          model.subProp(_.clsString).set(response.s)
          model.subProp(_.clsVar).set(response._v)
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSealedTrait(Seq(Fruit.Apple, Fruit.Orange, Fruit.Banana)(Random.nextInt(3))) onComplete {
        case Success(response) => model.subProp(_.sealedTrait).set(response)
        case Failure(ex) => logger.error(ex.getMessage)
      }
    }
  }

  class GenCodecsDemoView(model: ModelProperty[GenCodecsDemoModel], presenter: GenCodecsDemoPresenter) {
    import JsDom.all._

    val loadIdButton = UdashButton(
      buttonStyle = ButtonStyle.Primary,
      componentId = ComponentId("gencodec-demo")
    )("Send request")

    loadIdButton.listen {
      case UdashButton.ButtonClickEvent(btn, _) =>
        presenter.onButtonClick(btn)
    }

    def render: Modifier = span(GuideStyles.frame, GuideStyles.useBootstrap)(
      loadIdButton.render,
      h3("Results:"),
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
    )
  }
}
