package io.udash.web.guide.demos.rpc

import com.avsystem.commons.serialization.{GenCodec, HasGenCodec, Input, Output}
import io.udash.rpc._

import scala.concurrent.Future

object GenCodecServerRPC extends DefaultServerRpcCompanion[GenCodecServerRPC] {
  case class DemoCaseClass(i: Int, s: String, intAsDouble: Double)
  object DemoCaseClass extends HasGenCodec[DemoCaseClass]

  sealed trait Fruit
  object Fruit {
    case object Apple extends Fruit
    case object Orange extends Fruit
    case object Banana extends Fruit

    implicit val genCodec: GenCodec[Fruit] = GenCodec.materialize
  }

  class DemoClass(val i: Int, val s: String) {
    var _v: Int = 5
  }

  object DemoClass {
    implicit val demoClassCodec: GenCodec[DemoClass] = new GenCodec[DemoClass] {
      override def read(input: Input): DemoClass = {
        val list = input.readList()
        val i = list.nextElement().readSimple().readInt()
        val s = list.nextElement().readSimple().readString()
        val _v = list.nextElement().readSimple().readInt()
        val demo = new DemoClass(i, s)
        demo._v = _v
        demo
      }

      override def write(output: Output, value: DemoClass): Unit = {
        val values = output.writeList()
        values.writeElement().writeSimple().writeInt(value.i)
        values.writeElement().writeSimple().writeString(value.s)
        values.writeElement().writeSimple().writeInt(value._v)
        values.finish()
      }
    }
  }
}

trait GenCodecServerRPC {
  import GenCodecServerRPC._

  def sendInt(el: Int): Future[Int]
  def sendDouble(el: Double): Future[Double]
  def sendString(el: String): Future[String]
  def sendSeq(el: Seq[String]): Future[Seq[String]]
  def sendMap(el: Map[String, Int]): Future[Map[String, Int]]
  def sendCaseClass(el: DemoCaseClass): Future[DemoCaseClass]
  def sendClass(el: DemoClass): Future[DemoClass]
  def sendSealedTrait(el: Fruit): Future[Fruit]
}
