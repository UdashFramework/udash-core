package io.udash.rpc.internals

import io.udash.rpc._
import io.udash.testing.UdashBackendTest

import scala.collection.mutable

class ExposesServerRPCTest extends UdashBackendTest {

  def tests[T <: ExposesServerRPC[TestRPC]](createRpc: (mutable.Builder[String, Seq[String]]) => T) = {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      import rpc.framework._
      rpc.handleRpcFire(
        RPCFire(
          RawInvocation("handle", List()),
          List()
        )
      )
      calls.result() should contain("handle")


      rpc.handleRpcFire(
        RPCFire(
          RawInvocation("proc", List(List())),
          List(RawInvocation("innerRpc", List(List(write[String]("arg0")))))
        )
      )
      calls.result() should contain("innerRpc.proc")
    }

    "handle RPC calls" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      import rpc.framework._
      rpc.handleRpcCall(
        RPCCall(
          RawInvocation("doStuff", List(List(write[Boolean](true)))),
          List(),
          "callId1"
        )
      )
      calls.result() should contain("doStuff")

      rpc.handleRpcCall(
        RPCCall(
          RawInvocation("func", List(List(write[Int](5)))),
          List(RawInvocation("innerRpc", List(List(write[String]("arg0"))))),
          "callId2"
        )
      )
      calls.result() should contain("innerRpc.func")
    }

    "work with client RPC" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      import rpc.framework._
      rpc.handleRpcFire(
        RPCFire(
          RawInvocation("handle", List()),
          List()
        )
      )
      calls.result() should contain("handle")


      rpc.handleRpcFire(
        RPCFire(
          RawInvocation("proc", List(List())),
          List(RawInvocation("innerRpc", List(List(write[String]("arg0")))))
        )
      )
      calls.result() should contain("innerRpc.proc")
    }
  }

  def createDefaultRpc(calls: mutable.Builder[String, Seq[String]]): DefaultExposesServerRPC[TestRPC] = {
    val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
      calls += method
    })
    val rpc = new DefaultExposesServerRPC[TestRPC](impl)
    rpc
  }

  final class UPickleExposesServerRPC[ServerRPCType]
    (local: ServerRPCType)(implicit protected val localRpcAsRaw: UPickleUdashRPCFramework.AsRawRPC[ServerRPCType])
    extends ExposesServerRPC(local) {

    override val framework = UPickleUdashRPCFramework
  }

  def createCustomRpc(calls: mutable.Builder[String, Seq[String]]): UPickleExposesServerRPC[TestRPC] = {
    val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
      calls += method
    })
    val rpc = new UPickleExposesServerRPC[TestRPC](impl)
    rpc
  }

  "DefaultExposesServerRPC" should tests(createDefaultRpc)
  "CustomExposesServerRPC" should tests(createCustomRpc)
}
