package io.udash
package rpc

import io.udash.rpc.serialization.DefaultExceptionCodecRegistry
import io.udash.rpc.testing.{RpcExposerTest, TestRpcExposer}
import io.udash.rpc.utils.TimeoutConfig
import io.udash.testing.TestRpcServer

import scala.collection.mutable.ArrayBuffer

class RpcServerTest extends RpcExposerTest("RpcServer") {
  override def createRpcExposer(calls: ArrayBuffer[String]): TestRpcExposer = {
    new TestRpcServer[TestRpc, TestRpc](
      (_, _) => TestRpc.rpcImpl((name: String, _, _) => calls.append(name)),
      new DefaultExceptionCodecRegistry, TimeoutConfig.Default,
      TimeoutConfig.Default.callResponseTimeout
    )
  }

  // TODO
//  def createDefaultRpc(calls: mutable.Builder[String, Seq[String]]): DefaultRpcServer[TestRPC, TestRPC] = {
//    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
//      calls += method
//    })
//    new DefaultRpcServer[TestRPC, TestRPC](impl)
//  }

//  final class UPickleDefaultRpcServer[ServerRPCType]
//  (local: ServerRPCType)(implicit protected val localRpcAsRaw: ServerRawRpc.AsRawRpc[ServerRPCType])
//    extends DefaultRpcServer(local) {
//  }
//
//  def createCustomRpc(calls: mutable.Builder[String, Seq[String]]): UPickleDefaultRpcServer[TestRPC] = {
//    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
//      calls += method
//    })
//    new UPickleDefaultRpcServer[TestRPC](impl)
//  }

//  val loggedCalls = ListBuffer.empty[String]

//  def createLoggingRpc(calls: mutable.Builder[String, Seq[String]]): DefaultRpcServer[TestRPC] = {
//    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
//      calls += method
//    })
//    new DefaultRpcServer[TestRPC](impl) with CallLogging[TestRPC] {
//      override protected val metadata: RpcMetadata[TestRPC] = TestRPC.metadata
//
//      override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
//        loggedCalls += s"$rpcName $methodName $args"
//    }
//  }

//  "DefaultExposesServerRPC" should tests(createDefaultRpc)
//  "CustomExposesServerRPC" should tests(createCustomRpc)

//  "LoggingExposesServerRPC" should {
//    import io.udash.rpc.InnerRPC
//    val calls = Seq.newBuilder[String]
//    val rpc: DefaultRpcServer[TestRPC] = createLoggingRpc(calls)
//
//    "not log calls of regular RPC methods" in {
//      rpc.handleRpcCall(
//        RpcCall(
//          RpcInvocation("doStuff", List(write[Boolean](true))),
//          List(),
//          "callId1"
//        )
//      )
//      rpc.handleRpcCall(
//        RpcCall(
//          RpcInvocation("doStuff", List(write[Boolean](false))),
//          List(),
//          "callId1"
//        )
//      )
//      rpc.handleRpcFire(
//        RpcFire(
//          RpcInvocation("proc", List()),
//          List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
//        )
//      )
//      loggedCalls shouldBe empty
//    }
//
//    "log calls of annotated RPC methods" in {
//      rpc.handleRpcCall(
//        RpcCall(
//          RpcInvocation("func", List(write[Int](5))),
//          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
//          "callId2"
//        )
//      )
//      rpc.handleRpcCall(
//        RpcCall(
//          RpcInvocation("func", List(write[Int](10))),
//          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
//          "callId2"
//        )
//      )
//      rpc.handleRpcFire(
//        RpcFire(
//          RpcInvocation("fireSomething", List(write[Int](13))),
//          List()
//        )
//      )
//      loggedCalls.toList shouldBe List(
//        s"${classOf[InnerRPC].getSimpleName} func List(5)",
//        s"${classOf[InnerRPC].getSimpleName} func List(10)",
//        s"${classOf[TestRPC].getSimpleName} fireSomething List(13)"
//      )
//    }
//  }
}
