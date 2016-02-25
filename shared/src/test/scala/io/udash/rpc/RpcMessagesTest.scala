package io.udash.rpc

import io.udash.rpc.internals.{RPCResponseFailure, RPCResponseSuccess, RPCFire, RPCCall}
import io.udash.testing.UdashSharedTest
import upickle.Js

class RpcMessagesTest extends UdashSharedTest {
  "RPCMessages uPickle Serializers" should {
    "serialize and deserialize call request" in {
      import io.udash.rpc.internals.RPCRequest._

      val inv = RawInvocation("rpcName", List(List(Js.Str("Test"), Js.Num(7)), List(Js.Obj("k" -> Js.Str("v")))))
      val getter1 = RawInvocation("getter1", Nil)
      val getter2 = RawInvocation("getter2", Nil)
      val req = RPCCall(inv, getter1 :: getter2 :: Nil, "call1")

      val serialized = RPCRequestWriter.write(req)
      val deserialized = RPCRequestReader.read(serialized)

      deserialized should be(req)
    }

    "serialize and deserialize fire request" in {
      import io.udash.rpc.internals.RPCRequest._

      val inv = RawInvocation("rpcName", List(List(Js.Str("Test"), Js.Num(7)), List(Js.Obj("k" -> Js.Str("v")))))
      val getter1 = RawInvocation("getter1", Nil)
      val getter2 = RawInvocation("getter2", Nil)
      val req = RPCFire(inv, getter1 :: getter2 :: Nil)

      val serialized = RPCRequestWriter.write(req)
      val deserialized = RPCRequestReader.read(serialized)

      deserialized should be(req)
    }

    "serialize and deserialize success response" in {
      import io.udash.rpc.internals.RPCResponse._

      val res = RPCResponseSuccess(Js.Obj("k" -> Js.Str("v")), "call1")
      val serialized = RPCResponseWriter.write(res)
      val deserialized = RPCResponseReader.read(serialized)

      deserialized should be(res)
    }

    "serialize and deserialize failure response" in {
      import io.udash.rpc.internals.RPCResponse._

      val res = RPCResponseFailure("cause", "msg abc", "call1")
      val serialized = RPCResponseWriter.write(res)
      val deserialized = RPCResponseReader.read(serialized)

      deserialized should be(res)
    }
  }
}
