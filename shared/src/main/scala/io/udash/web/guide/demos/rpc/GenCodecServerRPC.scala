package io.udash.web.guide.demos.rpc

import com.avsystem.commons.rpc.RPC
import com.avsystem.commons.serialization.{Input, Output}

import scala.concurrent.Future

object GenCodecServerRPC {
  case class DemoCaseClass(i: Int, s: String, intAsDouble: Double)

  class DemoClass(val i: Int, val s: String) {
    var _v: Int = 5
  }

  sealed trait Fruit
  case object Apple extends Fruit
  case object Orange extends Fruit
  case object Banana extends Fruit


  import com.avsystem.commons.serialization.GenCodec
  object DemoClass {
    implicit val DemoClassCodec = new GenCodec[DemoClass] {
      override def read(input: Input): DemoClass = {
        val list = input.readList().get
        val i = list.nextElement().readInt().get
        val s = list.nextElement().readString().get
        val _v = list.nextElement().readInt().get
        val demo = new DemoClass(i, s)
        demo._v = _v
        demo
      }

      override def write(output: Output, value: DemoClass): Unit = {
        val values = output.writeList()
        values.writeElement().writeInt(value.i)
        values.writeElement().writeString(value.s)
        values.writeElement().writeInt(value._v)
        values.finish()
      }
    }
  }
}

@RPC
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
