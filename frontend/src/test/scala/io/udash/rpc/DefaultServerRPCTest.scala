package io.udash.rpc

import com.github.ghik.silencer.silent
import io.udash.testing.UdashFrontendTest
import upickle.Js

import scala.collection.mutable.ArrayBuffer
import scala.util.Failure

class DefaultServerRPCTest extends UdashFrontendTest {
  class MockServerConnector extends ServerConnector {
    val requests = new ArrayBuffer[RPCRequest]()

    override def sendRPCRequest(request: RPCRequest): Unit =
      requests += request
  }

  "DefaultServerRPC" should {

    "gain access to RPC methods of server" in {
      val connector = new MockServerConnector
      @silent
      val serverRPC = new DefaultServerRPC[TestRPC](AsRealRPC[TestRPC], connector)
      val rpc = serverRPC.remoteRpc

      rpc.handle
      connector.requests.exists(req => req.invocation.rpcName == "handle")

      rpc.handleMore()
      connector.requests.exists(req => req.invocation.rpcName == "handleMore")

      rpc.doStuff(true)
      connector.requests.exists(req => req.invocation.rpcName == "doStuff")

      rpc.innerRpc("bla").proc()
      connector.requests.exists(req => req.invocation.rpcName == "proc")
    }

    "handle responses from server" in {
      val connector = new MockServerConnector
      @silent
      val serverRPC = new DefaultServerRPC[TestRPC](AsRealRPC[TestRPC], connector)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)
      val f4 = rpc.doStuff(true)

      serverRPC.handleResponse(RPCResponseSuccess(Js.Str("response1"), "1"))
      serverRPC.handleResponse(RPCResponseSuccess(Js.Str("response2"), "2"))
      serverRPC.handleResponse(RPCResponseSuccess(Js.Num(5), "3"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(true)
      f4.isCompleted should be(false)

      f1.value.get.get should be("response1")
      f2.value.get.get should be("response2")
      f3.value.get.get should be(5)
    }

    "handle fail responses from server" in {
      val connector = new MockServerConnector
      @silent
      val serverRPC = new DefaultServerRPC[TestRPC](AsRealRPC[TestRPC], connector)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)

      serverRPC.handleResponse(RPCResponseFailure("cause1", "msg1", "1"))
      serverRPC.handleResponse(RPCResponseFailure("cause2", "msg2", "2"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(false)

      f1.value should be(Some(Failure(RPCFailure("cause1", "msg1"))))
      f2.value should be(Some(Failure(RPCFailure("cause2", "msg2"))))
    }
  }
}
