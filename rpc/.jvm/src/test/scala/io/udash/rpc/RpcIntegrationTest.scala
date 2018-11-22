package io.udash
package rpc

import io.udash.rpc.serialization.DefaultExceptionCodecRegistry
import io.udash.rpc.utils.{ClientId, TimeoutConfig}
import io.udash.testing.{AsyncUdashRpcBackendTest, TestRpcServer}
import javax.websocket.ContainerProvider
import org.eclipse.jetty.server.{Server, ServerConnector}
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class RpcIntegrationTest extends AsyncUdashRpcBackendTest with BeforeAndAfterAll {
  import RpcIntegrationTest._

  val rpcServer = new TestRpcServer[ServerRpc, ClientRpc](
    (_, clientId) => new ServerRpcImpl(clientId),
    new DefaultExceptionCodecRegistry, TimeoutConfig.Default,
    1 second
  )

  val port = 22654
  val contextPrefix = "rpcTest"
  val endpointPrefix = "websocket"
  val server = new Server
  val connector = new ServerConnector(server)
  connector.setPort(port)
  server.addConnector(connector)

  val clientContainer = ContainerProvider.getWebSocketContainer

  def startServer(): Unit = {
    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath(s"/$contextPrefix")
    server.setHandler(context)

    try {
      val wscontainer = WebSocketServerContainerInitializer.configureContext(context)
      wscontainer.addEndpoint(rpcServer.endpointConfig(s"/$endpointPrefix"))

      server.start()
    } catch {
      case t: Throwable =>
        t.printStackTrace(System.err)
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    startServer()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    server.join()
    super.afterAll()
  }

  "Rpc endpoints" should {

    "queue messages on connection problems (server)" in {
      val rpcClient = new DefaultRpcClient[ClientRpc, ServerRpc](
        new ClientRpcImpl(3),
        s"ws://127.0.0.1:$port/$contextPrefix/$endpointPrefix",
        new DefaultExceptionCodecRegistry,
        clientContainer, TimeoutConfig.Default
      )

      val clientIdFuture = rpcClient.call().loadClientId()
      val clientId = eventually { clientIdFuture.value.get.get }

      rpcClient.close()

      val result = rpcServer.call(clientId).call("asd")

      rpcClient.open()

      eventually {
        result.value should be(Some(Success("asdasdasd")))
      }

      rpcClient.close()

      eventually {
        rpcServer.clientsCount should be(0)
      }
    }

    "queue messages on connection problems (client)" in {
      server.stop()
      server.join()

      val rpcClient = new DefaultRpcClient[ClientRpc, ServerRpc](
        new ClientRpcImpl(3),
        s"ws://127.0.0.1:$port/$contextPrefix/$endpointPrefix",
        new DefaultExceptionCodecRegistry,
        clientContainer, TimeoutConfig.Default
      )

      val result = rpcClient.call().call("abc")
      val multipliedResult = rpcClient.call().nestedWithMultiplier(2).call("abc")

      startServer()

      eventually {
        result.value should be(Some(Success("abc")))
        multipliedResult.value should be(Some(Success("abcabc")))
      }

      rpcClient.close()

      eventually {
        rpcServer.clientsCount should be(0)
      }
    }

    "call server from client" in {
      val rpcClient = DefaultRpcClient[ClientRpc, ServerRpc](
        new ClientRpcImpl(),
        s"ws://127.0.0.1:$port/$contextPrefix/$endpointPrefix",
        new DefaultExceptionCodecRegistry
      )

      val result = rpcClient.call().call("abc")
      val multipliedResult = rpcClient.call().nestedWithMultiplier(2).call("abc")

      eventually {
        result.value should be(Some(Success("abc")))
        multipliedResult.value should be(Some(Success("abcabc")))
      }

      rpcClient.close()

      eventually {
        rpcServer.clientsCount should be(0)
      }
    }

    "call client from server" in {
      val rpcClient = DefaultRpcClient[ClientRpc, ServerRpc](
        new ClientRpcImpl(3),
        s"ws://127.0.0.1:$port/$contextPrefix/$endpointPrefix",
        new DefaultExceptionCodecRegistry
      )
      val rpcClient2 = DefaultRpcClient[ClientRpc, ServerRpc](
        new ClientRpcImpl(2),
        s"ws://127.0.0.1:$port/$contextPrefix/$endpointPrefix",
        new DefaultExceptionCodecRegistry
      )

      val clientIdFuture = rpcClient.call().loadClientId()
      val clientId = eventually { clientIdFuture.value.get.get }

      val clientIdFuture2 = rpcClient2.call().loadClientId()
      val clientId2 = eventually { clientIdFuture2.value.get.get }

      val result = rpcServer.call(clientId).call("asd")
      val result2 = rpcServer.call(clientId2).call("asd")
      val result3 = rpcServer.call(Seq(clientId, clientId2), _.call("qwe"))

      eventually {
        result.value should be(Some(Success("asdasdasd")))
        result2.value should be(Some(Success("asdasd")))
        result3(clientId).get.value should be(Some(Success("qweqweqwe")))
        result3(clientId2).get.value should be(Some(Success("qweqwe")))
      }

      rpcClient.close()
      rpcClient2.close()

      eventually {
        rpcServer.clientsCount should be(0)
      }
    }
  }
}

object RpcIntegrationTest {
  trait ClientRpc {
    def fire(i: Int, s: String): Unit
    def call(s: String): Future[String]
    def nestedWithMultiplier(multiplier: Int): ClientRpc
  }
  object ClientRpc extends DefaultRpcCompanion[ClientRpc]

  trait ServerRpc extends ClientRpc {
    def loadClientId(): Future[ClientId]
  }
  object ServerRpc extends DefaultRpcCompanion[ServerRpc]

  class ClientRpcImpl(multiplier: Int = 1)(implicit ec: ExecutionContext)
    extends ClientRpc {

    override def fire(i: Int, s: String): Unit = ()
    override def call(s: String): Future[String] = Future(s * multiplier)
    override def nestedWithMultiplier(multiplier: Int): ClientRpc = new ClientRpcImpl(multiplier)
  }

  class ServerRpcImpl(clientId: ClientId)(implicit ec: ExecutionContext)
    extends ClientRpcImpl with ServerRpc {

    override def loadClientId(): Future[ClientId] = Future(clientId)
  }
}