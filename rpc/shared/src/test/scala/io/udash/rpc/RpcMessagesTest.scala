package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, EscapeUtils, ExceptionCodecRegistry}
import io.udash.testing.UdashSharedTest

import scala.util.Random

private case class CustomException(error: String, counter: Int) extends Throwable

private sealed trait SealedExceptions extends Throwable
private case class SealedExceptionsA(a: Int) extends SealedExceptions
private case class SealedExceptionsB(b: Double) extends SealedExceptions

trait RpcMessagesTestScenarios extends UdashSharedTest with Utils {
  val exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry
  exceptionsRegistry.register(GenCodec.materialize[CustomException])
  exceptionsRegistry.register(GenCodec.materialize[SealedExceptions])

  def tests(RPC: UdashRPCFramework) = {
    import RPC._

    implicit val codec: GenCodec[RPCResponse] = RPCResponseCodec(exceptionsRegistry)

    val inv = RawInvocation("r{p[c\"]}Name", List(List(s""""${EscapeUtils.escape("val{lu} [e1\"2]3")}"""")))
    val getter1 = RawInvocation("g{}[]\",\"etter1", List(List("\",a\"", "\"B,,\"", "\"v\""), List("\"xy,z\"")))
    val getter2 = RawInvocation("ge{[[\"a,sd\"]][]}t,ter2", Nil)
    val req = RPCCall(inv, getter1 :: getter2 :: Nil, "\"call1\"")
    val success = RPCResponseSuccess(s""""${EscapeUtils.escape("val{lu} [e1\"2]3")}"""", "\"ca{[]}ll1\"")
    val failure = RPCResponseFailure("\\ca{}[]\"\"use\\\\", "[{msg}: \"abc\"]", "\"ca{[]}ll1\"")
    val exception = RPCResponseException(CustomException("test", 5).getClass.getName, CustomException("test", 5), "\"ca{[]}ll1\"")
    val runtimeException = RPCResponseException(new NullPointerException("test").getClass.getName, new NullPointerException(null), "\"ca{[]}ll1\"")
    val sealedException = RPCResponseException(classOf[SealedExceptions].getName, SealedExceptionsA(2), "\"ca{[]}ll1\"")
    val rpcFail = RPCFailure("ca{,}[]\"\"use", "[{msg}: \"abc\"]")

    "serialize and deserialize call request" in {
      val serialized = write[RPCRequest](req)
      val deserialized = read[RPCRequest](serialized)
      deserialized should be(req)
    }

    "serialize and deserialize fire request" in {
      val serialized = write[RPCRequest](req)
      val deserialized = read[RPCRequest](serialized)
      deserialized should be(req)
    }

    "serialize and deserialize success response" in {
      val serialized = write[RPCResponse](success)
      val deserialized = read[RPCResponse](serialized)
      deserialized should be(success)
    }

    "serialize and deserialize failure response" in {
      val serialized = write[RPCResponse](failure)
      val deserialized = read[RPCResponse](serialized)
      deserialized should be(failure)
    }

    "serialize and deserialize exception response" in {
      val serialized = write[RPCResponse](exception)
      val deserialized = read[RPCResponse](serialized)
      deserialized should be(exception)

      val serialized2 = write[RPCResponse](runtimeException)
      val deserialized2 = read[RPCResponse](serialized2)
      (deserialized2.asInstanceOf[RPCResponseException].exception match {
        case _: RuntimeException => true
        case _ => false
      }) should be(true)

      val serialized3 = write[RPCResponse](sealedException)
      val deserialized3 = read[RPCResponse](serialized3)
      deserialized3 should be(sealedException)
    }

//    "serialize and deserialize exception stacktrace" in {
//      val serialized = write[RPCResponse](exception)
//      val deserialized = read[RPCResponse](serialized)
//      deserialized.asInstanceOf[RPCResponseException].exception.getStackTrace should be(exception.exception.getStackTrace)
//
//      val serialized2 = write[RPCResponse](runtimeException)
//      val deserialized2 = read[RPCResponse](serialized2)
//      deserialized2.asInstanceOf[RPCResponseException].exception.getStackTrace should be(runtimeException.exception.getStackTrace)
//
//      val serialized3 = write[RPCResponse](sealedException)
//      val deserialized3 = read[RPCResponse](serialized3)
//      deserialized3.asInstanceOf[RPCResponseException].exception.getStackTrace should be(sealedException.exception.getStackTrace)
//    }

    "serialize and deserialize rpc failure msg" in {
      val serialized = write[RPCFailure](rpcFail)
      val deserialized = read[RPCFailure](serialized)

      deserialized should be(rpcFail)
    }

    "serialize and deserialize integers" in {
      val test = 5
      val serialized = write(test)
      val deserialized = read[Int](serialized)

      deserialized should be(test)
    }

    "serialize and deserialize booleans" in {
      val test = true
      val serialized = write(test)
      val deserialized = read[Boolean](serialized)

      deserialized should be(test)
    }

    "serialize and deserialize strings" in {
      val test = "a።bc\u0676ąቢść➔Ĳ"
      val serialized = write(test)
      val deserialized = read[String](serialized)

      deserialized should be(test)
    }

    "serialize and deserialize simple case classes" in {
      val test: TestCC = TestCC(5, 123L, 432, true, "bla", 'a' :: 'b' :: Nil)
      val serialized = write[TestCC](test)
      val deserialized = read[TestCC](serialized)

      deserialized should be(test)
    }

    "serialize and deserialize nested case classes" in {
      val test: TestCC = TestCC(5, 123L, 432, true, "bla", 'a' :: 'b' :: Nil)
      val test2: TestCC = TestCC(-35, 1L, 432, true, "blsddf sdg  \"{,}[,]\"a", 'a' :: 'b' :: Nil)
      val nested: NestedTestCC = NestedTestCC(-123, test, test2)
      val serialized = write(nested)
      val deserialized = read[NestedTestCC](serialized)

      deserialized should be(nested)
    }

    "serialize all types" in {
      val item = completeItem()
      val serialized = write(item)
      val deserialized = read[CompleteItem](serialized)

      deserialized.unit should be(item.unit)
      deserialized.string should be(item.string)
      deserialized.char should be(item.char)
      deserialized.boolean should be(item.boolean)
      deserialized.byte should be(item.byte)
      deserialized.short should be(item.short)
      deserialized.int should be(item.int)
      deserialized.long should be(item.long)
      deserialized.float should be(item.float)
      deserialized.double should be(item.double)
      deserialized.binary should be(item.binary)
      deserialized.list should be(item.list)
      deserialized.set should be(item.set)
      deserialized.obj should be(item.obj)
      deserialized.map should be(item.map)
    }

    "handle plain numbers in JSON as Int, Long and Double" in {
      val json = "123"
      RPC.read[Int](json) should be(123)
      RPC.read[Long](json) should be(123)
      RPC.read[Double](json) should be(123)

      val maxIntPlusOne: Long = Int.MaxValue.toLong + 1
      val jsonLong = maxIntPlusOne.toString
      intercept[ReadFailure](RPC.read[Int](jsonLong))
      RPC.read[Long](jsonLong) should be(maxIntPlusOne)
      RPC.read[Double](jsonLong) should be(maxIntPlusOne)

      val jsonLongMax = Long.MaxValue.toString
      intercept[ReadFailure](RPC.read[Int](jsonLong))
      RPC.read[Long](jsonLongMax) should be(Long.MaxValue)
      RPC.read[Double](jsonLongMax) should be(Long.MaxValue)

      val jsonDouble = Double.MaxValue.toString
      intercept[ReadFailure](RPC.read[Int](jsonDouble))
      intercept[ReadFailure](RPC.read[Long](jsonDouble))
      RPC.read[Double](jsonDouble) should be(Double.MaxValue)

      val jsonDouble2 = "123.00"
      RPC.read[Int](jsonDouble2) should be(123)
      RPC.read[Long](jsonDouble2) should be(123)
      RPC.read[Double](jsonDouble2) should be(123.0)

      val brokenDouble = "312,321"
      intercept[ReadFailure](RPC.read[Int](brokenDouble))
      intercept[ReadFailure](RPC.read[Long](brokenDouble))
      intercept[ReadFailure](RPC.read[Double](brokenDouble))
    }

    "work with skipping" in {
      case class TwoItems(i1: CompleteItem, i2: CompleteItem)
      implicit val skippingCodec = new GenCodec[TwoItems] {
        override def read(input: Input): TwoItems = {
          val obj = input.readObject()
          obj.nextField().skip()
          val i2 = GenCodec.read[CompleteItem](obj.nextField())
          TwoItems(null, i2)
        }


        override def write(output: Output, value: TwoItems): Unit = {
          val obj = output.writeObject()
          GenCodec.write[CompleteItem](obj.writeField("i1"), value.i1)
          GenCodec.write[CompleteItem](obj.writeField("i2"), value.i2)
          obj.finish()
        }
      }

      val item = TwoItems(completeItem(), completeItem())
      val serialized = write(item)
      val deserialized = read[TwoItems](serialized)

      deserialized.i1 should be(null)
      deserialized.i2.unit should be(item.i2.unit)
      deserialized.i2.string should be(item.i2.string)
      deserialized.i2.char should be(item.i2.char)
      deserialized.i2.boolean should be(item.i2.boolean)
      deserialized.i2.byte should be(item.i2.byte)
      deserialized.i2.short should be(item.i2.short)
      deserialized.i2.int should be(item.i2.int)
      deserialized.i2.long should be(item.i2.long)
      deserialized.i2.float should be(item.i2.float)
      deserialized.i2.double should be(item.i2.double)
      deserialized.i2.binary should be(item.i2.binary)
      deserialized.i2.list should be(item.i2.list)
      deserialized.i2.set should be(item.i2.set)
      deserialized.i2.obj should be(item.i2.obj)
      deserialized.i2.map should be(item.i2.map)
    }
  }

  def hugeTests(RPC: UdashRPCFramework) = {
    import RPC._
    "serialize and deserialize huge case classes" in {
      def cc() = TestCC(Random.nextInt(), Random.nextLong(), Random.nextInt(), Random.nextBoolean(), Random.nextString(Random.nextInt(300)), List.fill(Random.nextInt(300))('a'))
      def ncc() = NestedTestCC(Random.nextInt(), cc(), cc())
      def dncc(counter: Int = 0): DeepNestedTestCC =
        if (counter < 500) DeepNestedTestCC(ncc(), dncc(counter + 1))
        else DeepNestedTestCC(ncc(), null)

      val test: DeepNestedTestCC = dncc()
      val serialized = write(test)
      val deserialized = read[DeepNestedTestCC](serialized)

      deserialized should be(test)
    }
  }
}

class UPickleRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages client uPickle serializers" should tests(ClientUPickleUdashRPCFramework)
  "RPCMessages server uPickle serializers" should tests(ServerUPickleUdashRPCFramework)
}