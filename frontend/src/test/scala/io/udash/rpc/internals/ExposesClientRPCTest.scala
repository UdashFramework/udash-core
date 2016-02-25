package io.udash.rpc.internals

import io.udash.rpc._
import io.udash.testing.UdashFrontendTest
import upickle.Js

class ExposesClientRPCTest extends UdashFrontendTest {
  "ExposesClientRPC" should {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val impl = TestClientRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new ExposesClientRPC[TestClientRPC](impl)

      rpc.handleRpcFire(RPCFire(RawInvocation("handle", List()), List()))
      calls.result() should contain("handle")

      rpc.handleRpcFire(RPCFire(RawInvocation("proc", List(List())), List(RawInvocation("innerRpc", List(List(Js.Str("arg0")))))))
      calls.result() should contain("innerRpc.proc")
    }
  }
}
