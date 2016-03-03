package io.udash.rpc.internals

import io.udash.rpc._
import io.udash.testing.UdashFrontendTest

import scala.collection.mutable

class ExposesClientRPCTest extends UdashFrontendTest {
  def tests(createExposesClientRPC: mutable.Builder[String, Seq[String]] => ExposesClientRPC[TestClientRPC]) = {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val rpc: ExposesClientRPC[TestClientRPC] = createExposesClientRPC(calls)

      import rpc.framework._
      rpc.handleRpcFire(RPCFire(RawInvocation("handle", List()), List()))
      calls.result() should contain("handle")

      rpc.handleRpcFire(RPCFire(
        RawInvocation("proc", List(List())),
        List(RawInvocation("innerRpc", List(List(write("arg0")))))
      ))
      calls.result() should contain("innerRpc.proc")
    }

    "not compile with server RPC trait" in {
      """val rpc = new DefaultExposesClientRPC[TestRPC](null)""" shouldNot typeCheck
    }
  }

  def createDefaultExposesClientRPC(calls: mutable.Builder[String, Seq[String]]): DefaultExposesClientRPC[TestClientRPC] = {
    val impl = TestClientRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
      calls += method
    })
    new DefaultExposesClientRPC[TestClientRPC](impl)
  }

  final class UPickleExposesClientRPC[ClientRPCType]
    (local: ClientRPCType)(implicit protected val localRpcAsRaw: UPickleUdashRPCFramework.AsRawClientRPC[ClientRPCType])
    extends ExposesClientRPC(local) {

    override val framework = UPickleUdashRPCFramework
  }

  def createCustomExposesClientRPC(calls: mutable.Builder[String, Seq[String]]): UPickleExposesClientRPC[TestClientRPC] = {
    val impl = TestClientRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
      calls += method
    })
    new UPickleExposesClientRPC[TestClientRPC](impl)
  }

  "DefaultExposesClientRPC" should tests(createDefaultExposesClientRPC)
  "CustomExposesClientRPC" should tests(createCustomExposesClientRPC)
}
