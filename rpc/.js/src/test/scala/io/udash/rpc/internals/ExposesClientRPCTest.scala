package io.udash.rpc.internals

import io.udash.rpc._
import io.udash.testing.UdashFrontendTest

import scala.collection.mutable

class ExposesClientRPCTest extends UdashFrontendTest with Utils {
  def tests(createExposesClientRPC: mutable.Builder[String, Seq[String]] => ExposesClientRPC[TestClientRPC]) = {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val rpc: ExposesClientRPC[TestClientRPC] = createExposesClientRPC(calls)

      rpc.handleRpcFire(RpcFire(RpcInvocation("handle", List()), List()))
      calls.result() should contain("handle")

      rpc.handleRpcFire(RpcFire(
        RpcInvocation("proc", Nil),
        List(RpcInvocation("innerRpc", List(write("arg0"))))
      ))
      calls.result() should contain("innerRpc.proc")
    }

    "not compile with server RPC trait" in {
      """val rpc = new DefaultExposesClientRPC[TestRPC](null)""" shouldNot typeCheck
    }
  }

  def createDefaultExposesClientRPC(calls: mutable.Builder[String, Seq[String]]): DefaultExposesClientRPC[TestClientRPC] = {
    val impl = TestClientRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
      calls += method
    })
    new DefaultExposesClientRPC[TestClientRPC](impl)
  }

  "DefaultExposesClientRPC" should tests(createDefaultExposesClientRPC)
}
