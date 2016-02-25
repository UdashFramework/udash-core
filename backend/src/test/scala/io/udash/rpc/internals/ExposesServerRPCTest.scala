package io.udash.rpc.internals

import io.udash.rpc
import io.udash.rpc._
import io.udash.testing.UdashBackendTest
import upickle.Js

class ExposesServerRPCTest extends UdashBackendTest {

  "ExposesServerRPC" should {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new ExposesServerRPC[TestRPC](impl)

      rpc.handleRpcFire(RPCFire(RawInvocation("handle", List()), List()))
      calls.result() should contain("handle")

      rpc.handleRpcFire(RPCFire(RawInvocation("proc", List(List())), List(RawInvocation("innerRpc", List(List(Js.Str("arg0")))))))
      calls.result() should contain("innerRpc.proc")
    }

    "handle RPC calls" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new ExposesServerRPC[TestRPC](impl)

      rpc.handleRpcCall(RPCCall(RawInvocation("doStuff", List(List(Js.True))), List(), "callId1"))
      calls.result() should contain("doStuff")

      rpc.handleRpcCall(RPCCall(RawInvocation("func", List(List(Js.Num(5)))), List(RawInvocation("innerRpc", List(List(Js.Str("arg0"))))), "callId2"))
      calls.result() should contain("innerRpc.func")
    }
  }
}
