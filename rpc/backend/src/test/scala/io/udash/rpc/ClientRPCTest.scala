package io.udash.rpc

import com.github.ghik.silencer.silent
import io.udash.rpc.internals.{BroadcastManager}
import io.udash.testing.{RunNowExecutionContext, UdashBackendTest}
import org.atmosphere.cpr.{Broadcaster, BroadcasterFactory, MetaBroadcaster}

import scala.concurrent.ExecutionContext

class ClientRPCTest extends UdashBackendTest {
  implicit val ec = RunNowExecutionContext

  def tests(createClientRpc: (ClientRPCTarget) => ClientRPC[TestClientRPC]) = {
    "gain access to RPC methods of concrete client" in {
      BroadcastManager.synchronized {
        val broadcasterFactory = mock[BroadcasterFactory]
        val metaBroadcaster = mock[MetaBroadcaster]
        val broadcaster = mock[Broadcaster]

        (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/id123", *).twice().returns(broadcaster)
        (broadcaster.broadcast(_: Any)).expects(*).twice()

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)

        @silent
        val rpc = createClientRpc(ClientId("id123")).remoteRpc
        rpc.handle
        rpc.handleMore()
      }
    }

    "gain access to RPC methods of all clients" in {
      BroadcastManager.synchronized {
        val broadcasterFactory = mock[BroadcasterFactory]
        val metaBroadcaster = mock[MetaBroadcaster]
        @silent
        val rpc = createClientRpc(AllClients).remoteRpc

        (metaBroadcaster.broadcastTo(_: String, _: Any)).expects("/client/*", *).twice()

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)
        rpc.handle
        rpc.handleMore()
      }
    }

    "require BroadcastManager to be initialized" in {
      BroadcastManager.synchronized {
        @silent
        val rpc = createClientRpc(AllClients).remoteRpc

        BroadcastManager.init(null, null)
        intercept[IllegalArgumentException] {
          rpc.handle
        }
        intercept[IllegalArgumentException] {
          rpc.handleMore()
        }
      }
    }

    "not compile with server RPC trait" in {
      """val rpc = new DefaultClientRPC[TestRPC](AllClients).get""" shouldNot typeCheck
    }
  }

  def createDefaultClientRPC(target: ClientRPCTarget): DefaultClientRPC[TestClientRPC] = {
    @silent
    val r = new DefaultClientRPC[TestClientRPC](target)
    r
  }

  class UPickleClientRPC[ClientRPCType](target: ClientRPCTarget)
                                       (implicit ec: ExecutionContext,
                                        protected val remoteRpcAsReal: ClientUPickleUdashRPCFramework.AsRealRPC[ClientRPCType])
    extends ClientRPC[ClientRPCType](target) {
    override val localFramework = ServerUPickleUdashRPCFramework
    override val remoteFramework = ClientUPickleUdashRPCFramework
  }

  def createCustomClientRPC(target: ClientRPCTarget): UPickleClientRPC[TestClientRPC] = {
    @silent
    val r = new UPickleClientRPC[TestClientRPC](target)
    r
  }

  "DefaultClientRPC" should tests(createDefaultClientRPC)
  "CustomClientRPC" should tests(createCustomClientRPC)
}
