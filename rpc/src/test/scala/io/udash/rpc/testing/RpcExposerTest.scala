package io.udash
package rpc.testing

import io.udash.rpc._
import io.udash.testing.AsyncUdashSharedTest

import scala.collection.mutable.ArrayBuffer

abstract class RpcExposerTest(name: String) extends AsyncUdashSharedTest with Utils {
  def createRpcExposer(calls: ArrayBuffer[String]): TestRpcExposer

  name should {
    "handle RPC fires" in {
      val calls = ArrayBuffer.empty[String]
      val responses = ArrayBuffer.empty[RpcMessage]
      val rpc: TestRpcExposer = createRpcExposer(calls)

      rpc.connectionWithMessages(
        Seq(
          RpcFire(RpcInvocation("handle", List()), List()),
          RpcFire(
            RpcInvocation("proc", Nil),
            List(RpcInvocation("innerRpc", List(write[String]("arg0"))))
          )
        ),
        responses
      )

      retrying {
        calls should contain("handle")
        calls should contain("innerRpc.proc")
      }
    }

    "handle RPC calls" in {
      val calls = ArrayBuffer.empty[String]
      val responses = ArrayBuffer.empty[RpcMessage]
      val rpc: TestRpcExposer = createRpcExposer(calls)

      rpc.connectionWithMessages(
        Seq(
          RpcCall(
            RpcInvocation("doStuff", List(write[Boolean](true))),
            List(),
            "callId1"
          ),
          RpcCall(
            RpcInvocation("func", List(write[Int](5))),
            List(RpcInvocation("innerRpc", List(write[String]("arg0")))),
            "callId2"
          )
        ),
        responses
      )

      retrying {
        calls should contain("doStuff")
        calls should contain("innerRpc.func")
        responses.size should be(2)
        responses should contain(RpcResponseSuccess(JsonStr("\"doStuffResult\""),"callId1"))
        responses should contain(RpcResponseSuccess(JsonStr("\"innerRpc.funcResult\""),"callId2"))
      }
    }

    "handle getter error in rpc calls" in {
      val calls = ArrayBuffer.empty[String]
      val responses = ArrayBuffer.empty[RpcMessage]
      val rpc: TestRpcExposer = createRpcExposer(calls)

      rpc.connectionWithMessages(
        Seq(
          RpcCall(
            RpcInvocation("proc", Nil),
            List(RpcInvocation("throwingGetter", Nil)),
            "callId1"
          ),
          RpcCall(
            RpcInvocation("proc", Nil),
            List(RpcInvocation("nullGetter", Nil)),
            "callId2"
          )
        ),
        responses
      )

      retrying {
        calls should contain("throwingGetter")
        calls should contain("nullGetter")
        calls shouldNot contain("proc")
        responses.size should be(2)
        responses should contain(RpcResponseFailure("com.avsystem.commons.rpc.UnknownRpc", "Unknown RPC proc for raw method call", "callId2"))
        responses.exists {
          case RpcResponseException("java.lang.NullPointerException", ex, "callId1") =>
            ex.isInstanceOf[NullPointerException]
          case _ => false
        } should be(true)
      }
    }
  }
}