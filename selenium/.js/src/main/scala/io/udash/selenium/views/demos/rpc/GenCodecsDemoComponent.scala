package io.udash.selenium.views.demos.rpc

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{ButtonStyle, UdashButton}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.selenium.Launcher
import io.udash.selenium.rpc.demos.rpc.GenCodecServerRPC
import io.udash.selenium.rpc.demos.rpc.GenCodecServerRPC.{DemoCaseClass, DemoClass, Fruit}
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait GenCodecsDemoModel {
  
  def int: Option[Int]
  def double: Option[Double]
  def string: Option[String]
  def seq: Option[Seq[String]]
  def map: Option[Seq[(String, Int)]]
  def caseClass: Option[DemoCaseClass]
  def clsInt: Option[Int]
  def clsString: Option[String]
  def clsVar: Option[Int]
  def sealedTrait: Option[Fruit]
}
object GenCodecsDemoModel extends HasModelPropertyCreator[GenCodecsDemoModel] {
  implicit val blank: Blank[GenCodecsDemoModel] = Blank.Simple(new GenCodecsDemoModel {
    override def int: Option[Int] = None
    override def double: Option[Double] = None
    override def string: Option[String] = None
    override def seq: Option[Seq[String]] = None
    override def map: Option[Seq[(String, Int)]] = None
    override def caseClass: Option[GenCodecServerRPC.DemoCaseClass] = None
    override def clsInt: Option[Int] = None
    override def clsString: Option[String] = None
    override def clsVar: Option[Int] = None
    override def sealedTrait: Option[GenCodecServerRPC.Fruit] = None
  })
}

class GenCodecsDemoComponent extends CssView with CrossLogging {

  def getTemplate: Modifier = GenCodecsDemoViewFactory()

  object GenCodecsDemoViewFactory {
    def apply(): Modifier = {
      val model = ModelProperty.blank[GenCodecsDemoModel]
      val presenter = new GenCodecsDemoPresenter(model)
      new GenCodecsDemoView(model, presenter).render
    }
  }

  class GenCodecsDemoPresenter(model: ModelProperty[GenCodecsDemoModel]) {
    def randomString(l: Int): String =
      BigInt.probablePrime(32, Random).toString(16)

    def onButtonClick(btn: UdashButton) = {
      btn.disabled.set(true)
      val demoRpc: GenCodecServerRPC = Launcher.serverRpc.demos().gencodecsDemo()
      demoRpc.sendInt(Random.nextInt()) onComplete {
        case Success(response) => model.subProp(_.int).set(Some(response))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendDouble(Random.nextLong().toDouble * 2e20) onComplete {
        case Success(response) => model.subProp(_.double).set(Some(response))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendString(randomString(10)) onComplete {
        case Success(response) => model.subProp(_.string).set(Some(response))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSeq(Seq(randomString(5), randomString(5))) onComplete {
        case Success(response) => model.subProp(_.seq).set(Some(response))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendMap(Map(randomString(5) -> Random.nextInt(), randomString(5) -> Random.nextInt())) onComplete {
        case Success(response) => model.subProp(_.map).set(Some(response.toSeq))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendCaseClass(DemoCaseClass(Random.nextInt(), randomString(5), 42)) onComplete {
        case Success(response) => model.subProp(_.caseClass).set(Some(response))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendClass(new DemoClass(Random.nextInt(), randomString(5))) onComplete {
        case Success(response) =>
          model.subProp(_.clsInt).set(Some(response.i))
          model.subProp(_.clsString).set(Some(response.s))
          model.subProp(_.clsVar).set(Some(response._v))
        case Failure(ex) => logger.error(ex.getMessage)
      }
      demoRpc.sendSealedTrait(Seq(Fruit.Apple, Fruit.Orange, Fruit.Banana)(Random.nextInt(3))) onComplete {
        case Success(response) => model.subProp(_.sealedTrait).set(Some(response))
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

    def render: Modifier = span(
      loadIdButton.render,
      h3("Results:"),
      p(
        ul(
          li("Int: ", produce(model.subProp(_.int))(response => span(id := "gencodec-demo-int", response).render)),
          li("Double: ", produce(model.subProp(_.double))(response => span(id := "gencodec-demo-double", response).render)),
          li("String: ", produce(model.subProp(_.string))(response => span(id := "gencodec-demo-string", response).render)),
          li("Seq[String]: ", produce(model.subProp(_.seq))(response => span(id := "gencodec-demo-seq", response.map(_.toString)).render)),
          li("Map[String, Int]: ", produce(model.subProp(_.map))(response => span(id := "gencodec-demo-map", response.map(_.toString)).render)),
          li("DemoCaseClass: ", produce(model.subProp(_.caseClass))(response => span(id := "gencodec-demo-caseClass", response.map(_.toString)).render)),
          li("DemoClass Int: ", produce(model.subProp(_.clsInt))(response => span(id := "gencodec-demo-cls-int", response).render)),
          li("DemoClass String: ", produce(model.subProp(_.clsString))(response => span(id := "gencodec-demo-cls-string", response).render)),
          li("DemoClass Var: ", produce(model.subProp(_.clsVar))(response => span(id := "gencodec-demo-cls-var", response).render)),
          li("Fruit: ", produce(model.subProp(_.sealedTrait))(response => span(id := "gencodec-demo-sealedTrait", response.map(_.toString)).render))
        )
      )
    )
  }
}
