package io.udash.rpc

import java.util.concurrent.TimeUnit

import io.udash.rpc.internals.UsesServerRPC
import io.udash.testing.AsyncUdashFrontendTest

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.util.{Failure, Random}

class ServerRPCTest extends AsyncUdashFrontendTest with Utils {
  class MockServerConnector extends ServerConnector {
    val requests = new ArrayBuffer[RpcRequest]()

    override def sendRpcRequest(request: RpcRequest): Unit =
      requests += request
  }

  def tests(createServerRpc: FiniteDuration => (MockServerConnector, ServerRPC[TestRPC])): Unit = {
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

      rpc.innerRpc("bla").recInner("arg").proc()
      connectorMock.requests.exists(req => req.invocation.rpcName == "proc") should be(true)
    }

    "handle responses from server" in {
      val (_, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").recInner("arg").func(123)
      val f3 = rpc.doStuffInt(true)
      val f4 = rpc.doStuff(true)
      val f5 = rpc.doStuffUnit()

      serverRPC.handleResponse(RpcResponseSuccess(write("response1"), "1"))
      serverRPC.handleResponse(RpcResponseSuccess(write("response2"), "2"))
      serverRPC.handleResponse(RpcResponseSuccess(write[Int](5), "3"))
      serverRPC.handleResponse(RpcResponseSuccess(write[Unit](()), "5"))

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
      val f2 = rpc.innerRpc("bla").recInner("arg").func(123)
      val f3 = rpc.doStuffInt(true)

      serverRPC.handleResponse(RpcResponseFailure("cause1", "msg1", "1"))
      serverRPC.handleResponse(RpcResponseFailure("cause2", "msg2", "2"))

      f1.isCompleted should be(true)
      f2.isCompleted should be(true)
      f3.isCompleted should be(false)

      f1.value should be(Some(Failure(RpcFailure("cause1", "msg1"))))
      f2.value should be(Some(Failure(RpcFailure("cause2", "msg2"))))
    }

    "handle exception responses from server" in {
      case class Ex(i: Int) extends Throwable
      val exName = Ex.getClass.getName

      val (_, serverRPC) = createServerRpc(defaultTimeout)
      val rpc = serverRPC.remoteRpc

      val f1 = rpc.doStuff(true)
      val f2 = rpc.innerRpc("bla").recInner("arg").func(123)
      val f3 = rpc.doStuffInt(true)

      serverRPC.handleResponse(RpcResponseException(exName, Ex(1), "1"))
      serverRPC.handleResponse(RpcResponseException(exName, Ex(2), "2"))

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

  def createDefaultServerRpc(timeout: FiniteDuration): (MockServerConnector, DefaultServerRPC[TestRPC]) = {
    val connectorMock = new MockServerConnector
    val serverRPC = new DefaultServerRPC[TestRPC](connectorMock, timeout)
    (connectorMock, serverRPC)
  }

  "DefaultServerRPC" should tests(createDefaultServerRpc)
}
