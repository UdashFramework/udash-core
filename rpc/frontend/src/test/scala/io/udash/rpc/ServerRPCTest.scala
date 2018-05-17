package io.udash.rpc

import java.util.concurrent.TimeUnit

import com.github.ghik.silencer.silent
import io.udash.rpc.internals.UsesServerRPC
import io.udash.testing.AsyncUdashFrontendTest

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.{Duration, DurationLong, FiniteDuration}
import scala.util.{Failure, Random}

class ServerRPCTest extends AsyncUdashFrontendTest with Utils {
  class MockServerConnector[RPCRequest] extends ServerConnector[RPCRequest] {
    val requests = new ArrayBuffer[RPCRequest]()

    override def sendRPCRequest(request: RPCRequest): Unit =
      requests += request
  }

  def tests[LocalFramework <: UdashRPCFramework, ServerFramework <: UdashRPCFramework](
    createServerRpc: FiniteDuration => (MockServerConnector[ServerFramework#RPCRequest], ServerRPC[TestRPC])
  ): Unit = {
    val defaultTimeout = 500 millis

    "gain access to RPC methods of server" in {
      val (connectorMock, serverRPC) = createServerRpc(defaultTimeout)
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
      val (_, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)
      val f4 = rpc.doStuff(true)
      val f5 = rpc.doStuffUnit()

      import serverRPC.remoteFramework._
      serverRPC.handleResponse(RPCResponseSuccess(write("response1"), "1"))
      serverRPC.handleResponse(RPCResponseSuccess(write("response2"), "2"))
      serverRPC.handleResponse(RPCResponseSuccess(write[Int](5), "3"))
      serverRPC.handleResponse(RPCResponseSuccess(write[Unit](()), "5"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(true)
      f4.isCompleted should be(false)
      f5.isCompleted should be(true)

      f1.value.get.get should be("response1")
      f2.value.get.get should be("response2")
      f3.value.get.get should be(5)
      f5.value.get.get should be(())
    }

    "handle fail responses from server" in {
      val (_, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)

      import serverRPC.remoteFramework._
      serverRPC.handleResponse(RPCResponseFailure("cause1", "msg1", "1"))
      serverRPC.handleResponse(RPCResponseFailure("cause2", "msg2", "2"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(false)

      f1.value should be(Some(Failure(RPCFailure("cause1", "msg1"))))
      f2.value should be(Some(Failure(RPCFailure("cause2", "msg2"))))
    }

    "handle exception responses from server" in {
      case class Ex(i: Int) extends Throwable
      val exName = Ex.getClass.getName

      val (_, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").func(123)
      val f3 = rpc.doStuffInt(true)

      import serverRPC.remoteFramework._
      serverRPC.handleResponse(RPCResponseException(exName, Ex(1), "1"))
      serverRPC.handleResponse(RPCResponseException(exName, Ex(2), "2"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(false)

      f1.value should be(Some(Failure(Ex(1))))
      f2.value should be(Some(Failure(Ex(2))))
    }

    "timeout calls without response" in {
      val timeoutMillis = Random.nextInt(1000) + 500
      val (connectorMock, serverRPC) = createServerRpc(timeoutMillis millis)
      val rpc = serverRPC.remoteRpc

      val start = System.nanoTime()
      val resp = rpc.doStuff(true)
      connectorMock.requests.exists(req => req.invocation.rpcName == "doStuff") should be(true)
      for {
        _ <- retrying {
          resp.isCompleted should be(true)
          resp.value.get.failed.get.isInstanceOf[UsesServerRPC.CallTimeout] should be(true)
        }
        end = System.nanoTime()
      } yield {
        val duration = TimeUnit.NANOSECONDS.toMillis(end - start)
        val margin = 250
        duration > timeoutMillis - margin && duration < timeoutMillis + margin should be(true)
      }
    }

    "call failure interceptors" in {
      val (connectorMock, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      var firstCalled = false
      serverRPC.onCallFailure { case _ =>
        firstCalled = true
      }

      var secondCalled = false
      val registration = serverRPC.onCallFailure { case _ =>
        secondCalled = true
      }

      var thirdCalled = false
      serverRPC.onCallFailure { case _ =>
        thirdCalled = true
      }

      registration.cancel()

      val resp = rpc.doStuff(true)
      connectorMock.requests.exists(req => req.invocation.rpcName == "doStuff") should be(true)
      retrying {
        resp.isCompleted should be(true)
        resp.value.get.failed.get.isInstanceOf[UsesServerRPC.CallTimeout] should be(true)
        firstCalled should be(true)
        secondCalled should be(false)
        thirdCalled should be(true)
      }
    }
  }

  def createDefaultServerRpc(timeout: FiniteDuration): (MockServerConnector[DefaultServerUdashRPCFramework.RPCRequest], DefaultServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector[DefaultServerUdashRPCFramework.RPCRequest]
    @silent
    val serverRPC = new DefaultServerRPC[TestRPC](connectorMock, timeout)
    (connectorMock, serverRPC)
  }

  class UPickleServerRPC[ServerRPCType : ServerUPickleUdashRPCFramework.AsRealRPC](
    override protected val connector: ServerConnector[ServerUPickleUdashRPCFramework.RPCRequest],
    override protected val callTimeout: Duration
  ) extends ServerRPC[ServerRPCType] {
    override val remoteFramework = ServerUPickleUdashRPCFramework
    override val localFramework = ClientUPickleUdashRPCFramework
    override val remoteRpcAsReal: ServerUPickleUdashRPCFramework.AsRealRPC[ServerRPCType] = implicitly[ServerUPickleUdashRPCFramework.AsRealRPC[ServerRPCType]]
  }

  def createCustomServerRpc(timeout: FiniteDuration): (MockServerConnector[ServerUPickleUdashRPCFramework.RPCRequest], UPickleServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector[ServerUPickleUdashRPCFramework.RPCRequest]
    @silent
    val serverRPC = new UPickleServerRPC[TestRPC](connectorMock, timeout)
    (connectorMock, serverRPC)
  }

  class MixedServerRPC[ServerRPCType : DefaultServerUdashRPCFramework.AsRealRPC](
    override protected val connector: ServerConnector[DefaultServerUdashRPCFramework.RPCRequest],
    override protected val callTimeout: Duration
  ) extends ServerRPC[ServerRPCType] {
    override val remoteFramework = DefaultServerUdashRPCFramework
    override val localFramework = ClientUPickleUdashRPCFramework
    override val remoteRpcAsReal: DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType] = implicitly[DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType]]
  }

  def createMixedServerRpc(timeout: FiniteDuration): (MockServerConnector[DefaultServerUdashRPCFramework.RPCRequest], MixedServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector[DefaultServerUdashRPCFramework.RPCRequest]
    @silent
    val serverRPC = new MixedServerRPC[TestRPC](connectorMock, timeout)
    (connectorMock, serverRPC)
  }

  "DefaultServerRPC" should tests[DefaultClientUdashRPCFramework.type, DefaultServerUdashRPCFramework.type](createDefaultServerRpc)
  "CustomServerRPC" should tests[ClientUPickleUdashRPCFramework.type, ServerUPickleUdashRPCFramework.type](createCustomServerRpc)
  "MixedServerRPC" should tests[ClientUPickleUdashRPCFramework.type, DefaultServerUdashRPCFramework.type](createMixedServerRpc)
}
