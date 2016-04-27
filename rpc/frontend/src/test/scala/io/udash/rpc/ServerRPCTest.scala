package io.udash.rpc

import com.github.ghik.silencer.silent
import io.udash.testing.UdashFrontendTest

import scala.collection.mutable.ArrayBuffer
import scala.util.Failure

class ServerRPCTest extends UdashFrontendTest with Utils {
  class MockServerConnector[RPCRequest] extends ServerConnector[RPCRequest] {
    val requests = new ArrayBuffer[RPCRequest]()

    override def sendRPCRequest(request: RPCRequest): Unit =
      requests += request
  }

  def tests[F <: UdashRPCFramework](createServerRpc: () => (MockServerConnector[F#RPCRequest], ServerRPC[TestRPC])) = {
    "gain access to RPC methods of server" in {
      val (connectorMock, serverRPC) = createServerRpc()
      val rpc = serverRPC.remoteRpc

      rpc.handle
      connectorMock.requests.exists(req => req.invocation.rpcName == "handle") should be(true)

      rpc.handleMore()
      connectorMock.requests.exists(req => req.invocation.rpcName == "handleMore") should be(true)

      rpc.doStuff(true)
      connectorMock.requests.exists(req => req.invocation.rpcName == "doStuff") should be(true)

      rpc.innerRpc("bla").proc()
      connectorMock.requests.exists(req => req.invocation.rpcName == "proc") should be(true)
    }

    "handle responses from server" in {
      val (connectorMock, serverRPC) = createServerRpc()
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)
      val f4 = rpc.doStuff(true)

      import serverRPC.framework._
      serverRPC.handleResponse(RPCResponseSuccess(write("response1"), "1"))
      serverRPC.handleResponse(RPCResponseSuccess(write("response2"), "2"))
      serverRPC.handleResponse(RPCResponseSuccess(write[Int](5), "3"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(true)
      f4.isCompleted should be(false)

      f1.value.get.get should be("response1")
      f2.value.get.get should be("response2")
      f3.value.get.get should be(5)
    }

    "handle fail responses from server" in {
      val (connectorMock, serverRPC) = createServerRpc()
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)

      import serverRPC.framework._
      serverRPC.handleResponse(RPCResponseFailure("cause1", "msg1", "1"))
      serverRPC.handleResponse(RPCResponseFailure("cause2", "msg2", "2"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(false)

      f1.value should be(Some(Failure(RPCFailure("cause1", "msg1"))))
      f2.value should be(Some(Failure(RPCFailure("cause2", "msg2"))))
    }
  }

  def createDefaultServerRpc(): (MockServerConnector[DefaultUdashRPCFramework.RPCRequest], DefaultServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector[DefaultUdashRPCFramework.RPCRequest]
    @silent
    val serverRPC = new DefaultServerRPC[TestRPC](connectorMock)
    (connectorMock, serverRPC)
  }

  class UPickleServerRPC[ServerRPCType](override protected val connector: ServerConnector[UPickleUdashRPCFramework.RPCRequest])
                                       (implicit override val remoteRpcAsReal: UPickleUdashRPCFramework.AsRealRPC[ServerRPCType])
    extends ServerRPC[ServerRPCType] {
    override val framework = UPickleUdashRPCFramework
  }

  def createCustomServerRpc(): (MockServerConnector[UPickleUdashRPCFramework.RPCRequest], UPickleServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector[UPickleUdashRPCFramework.RPCRequest]
    @silent
    val serverRPC = new UPickleServerRPC[TestRPC](connectorMock)
    (connectorMock, serverRPC)
  }

  "DefaultServerRPC" should tests[DefaultUdashRPCFramework.type](createDefaultServerRpc)
  "CustomServerRPC" should tests[UPickleUdashRPCFramework.type](createCustomServerRpc)
}
