package io.udash.rpc.internals

import io.udash.rpc._
import io.udash.rpc.utils.CallLogging
import io.udash.testing.UdashRpcBackendTest

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ExposesServerRPCTest extends UdashRpcBackendTest {

  def tests[T <: ExposesServerRPC[TestRPC]](createRpc: mutable.Builder[String, Seq[String]] => T) = {
    "handle RPC fires" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("handle", List()),
          List()
        )
      )
      calls.result() should contain("handle")


      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("proc", Nil),
          List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
        )
      )
      calls.result() should contain("innerRpc.proc")
    }

    "handle RPC calls" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("doStuff", List(write[Boolean](true))),
          List(),
          "callId1"
        )
      )
      calls.result() should contain("doStuff")

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("func", List(write[Int](5))),
          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
          "callId2"
        )
      )
      calls.result() should contain("innerRpc.func")
    }

    "work with client RPC" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("handle", List()),
          List()
        )
      )
      calls.result() should contain("handle")


      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("proc", Nil),
          List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
        )
      )
      calls.result() should contain("innerRpc.proc")
    }

    "handle getter error in rpc calls" in {
      val calls = Seq.newBuilder[String]
      val rpc: T = createRpc(calls)

      val resp1 = rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("proc", Nil),
          List(RpcInvocation("throwingGetter", Nil)),
          "callId1"
        )
      )
      calls.result() should contain("throwingGetter")
      calls.result() shouldNot contain("proc")

      val resp2 = rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("proc", Nil),
          List(RpcInvocation("nullGetter", Nil)),
          "callId2"
        )
      )
      calls.result() should contain("nullGetter")
      calls.result() shouldNot contain("proc")

      eventually {
        resp1.value.get.isFailure should be(true)
        resp2.value.get.isFailure should be(true)
      }
    }
  }

  def createDefaultRpc(calls: mutable.Builder[String, Seq[String]]): DefaultExposesServerRPC[TestRPC] = {
    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
      calls += method
    })
    new DefaultExposesServerRPC[TestRPC](impl)
  }

  final class UPickleExposesServerRPC[ServerRPCType]
  (local: ServerRPCType)(implicit protected val localRpcAsRaw: ServerRawRpc.AsRawRpc[ServerRPCType])
    extends ExposesServerRPC(local) {
  }

  def createCustomRpc(calls: mutable.Builder[String, Seq[String]]): UPickleExposesServerRPC[TestRPC] = {
    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
      calls += method
    })
    new UPickleExposesServerRPC[TestRPC](impl)
  }


  def createLoggingRpc(calls: mutable.Builder[String, Seq[String]], all: Boolean = false): (ExposesServerRPC[TestRPC], ListBuffer[String]) = {
    val loggedCalls = ListBuffer.empty[String]
    val impl = TestRPC.rpcImpl((method: String, args: List[Any], result: Option[Any]) => {
      calls += method
    })
    new DefaultExposesServerRPC[TestRPC](impl) with CallLogging[TestRPC] {
      override protected val metadata: ServerRpcMetadata[TestRPC] = TestRPC.metadata
      override protected def logAll: Boolean = all

      override def log(rpcName: String, methodName: String, args: Seq[String]): Unit =
        loggedCalls += s"$rpcName $methodName $args"
    } -> loggedCalls
  }

  "DefaultExposesServerRPC" should tests(createDefaultRpc)
  "CustomExposesServerRPC" should tests(createCustomRpc)

  "LoggingExposesServerRPC" should {
    import io.udash.rpc.InnerRPC

    "not log calls of regular RPC methods" in {
      val calls = Seq.newBuilder[String]
      val (rpc, loggedCalls) = createLoggingRpc(calls)

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("doStuff", List(write[Boolean](true))),
          List(),
          "callId1"
        )
      )
      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("doStuff", List(write[Boolean](false))),
          List(),
          "callId2"
        )
      )
      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("proc", List()),
          List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
        )
      )

      loggedCalls shouldBe empty
    }

    "log calls of annotated RPC methods" in {
      val calls = Seq.newBuilder[String]
      val (rpc, loggedCalls) = createLoggingRpc(calls)

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("func", List(write[Int](5))),
          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
          "callId1"
        )
      )
      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("func", List(write[Int](10))),
          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
          "callId2"
        )
      )
      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("fireSomething", List(write[Int](13))),
          List()
        )
      )

      loggedCalls should contain theSameElementsInOrderAs List(
        s"${classOf[InnerRPC].getSimpleName} func List(5)",
        s"${classOf[InnerRPC].getSimpleName} func List(10)",
        s"${classOf[TestRPC].getSimpleName} fireSomething List(13)"
      )
    }

    "log calls of all RPC methods" in {
      val calls = Seq.newBuilder[String]
      val (rpc, loggedCalls) = createLoggingRpc(calls, all = true)

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("doStuff", List(write[Boolean](true))),
          List(),
          "callId1"
        )
      )
      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("doStuff", List(write[Boolean](false))),
          List(),
          "callId2"
        )
      )
      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("proc", List()),
          List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
        )
      )

      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("func", List(write[Int](5))),
          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
          "callId2"
        )
      )
      rpc.handleRpcCall(
        RpcCall(
          RpcInvocation("func", List(write[Int](10))),
          List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
          "callId2"
        )
      )
      rpc.handleRpcFire(
        RpcFire(
          RpcInvocation("fireSomething", List(write[Int](13))),
          List()
        )
      )

      loggedCalls should contain theSameElementsInOrderAs List(
        s"${classOf[TestRPC].getSimpleName} doStuff List(true)",
        s"${classOf[TestRPC].getSimpleName} doStuff List(false)",
        s"${classOf[InnerRPC].getSimpleName} proc List()",
        s"${classOf[InnerRPC].getSimpleName} func List(5)",
        s"${classOf[InnerRPC].getSimpleName} func List(10)",
        s"${classOf[TestRPC].getSimpleName} fireSomething List(13)"
      )
    }
  }
}
