package io.udash.selenium.rpc.demos.rpc

import com.avsystem.commons.serialization.{GenCodec, HasGenCodec, Input, Output}
import io.udash.rpc.DefaultServerUdashRPCFramework

import scala.concurrent.Future

object GenCodecServerRPC {
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
    implicit val DemoClassCodec = new GenCodec[DemoClass] {
      override def read(input: Input): DemoClass = {
        val list = input.readList()
        val i = list.nextElement().readInt()
        val s = list.nextElement().readString()
        val _v = list.nextElement().readInt()
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

  final def fullRpcInfo: DefaultServerUdashRPCFramework.FullRPCInfo[GenCodecServerRPC] =
    DefaultServerUdashRPCFramework.materializeFullInfo

  implicit def asRealRPC: DefaultServerUdashRPCFramework.AsRealRPC[GenCodecServerRPC] = fullRpcInfo.asRealRPC
  implicit def asRawRPC: DefaultServerUdashRPCFramework.AsRawRPC[GenCodecServerRPC] = fullRpcInfo.asRawRPC
  implicit def metadata: DefaultServerUdashRPCFramework.RPCMetadata[GenCodecServerRPC] = fullRpcInfo.metadata
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
