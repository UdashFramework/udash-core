package io.udash.rpc

import io.udash.rpc.internals.BroadcastManager
import io.udash.testing.UdashRpcBackendTest
import org.atmosphere.cpr._

class ClientRPCTest extends UdashRpcBackendTest {

  import scala.concurrent.ExecutionContext.Implicits.global

  def tests(createClientRpc: ClientRPCTarget => ClientRPC[TestClientRPC]): Unit = {
    "gain access to RPC methods of concrete client" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val broadcaster = new BroadcasterMock
        val broadcasterFactory = new BroadcasterFactoryMock(broadcaster)
        val metaBroadcaster = new DefaultMetaBroadcaster

        val rpc = createClientRpc(ClientId("id123")).remoteRpc

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)
        rpc.handle
        rpc.handleMore()

        eventually {
          broadcaster.broadcasts.size should be(2)
          broadcasterFactory.lookups should contain("/client/id123")
        }
      }
    }

    "gain access to RPC methods of all clients" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val broadcaster = new BroadcasterMock
        val broadcasterFactory = new BroadcasterFactoryMock(broadcaster)
        val metaBroadcaster = new MetaBroadcasterMock
        val rpc = createClientRpc(AllClients).remoteRpc

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)
        rpc.handle
        rpc.handleMore()

        eventually {
          metaBroadcaster.broadcasts.size should be(2)
          metaBroadcaster.broadcasts(0)._1 should be("/client/*")
          metaBroadcaster.broadcasts(1)._1 should be("/client/*")
        }
      }
    }

    "require BroadcastManager to be initialized" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val rpc = createClientRpc(AllClients).remoteRpc
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
    val r = new DefaultClientRPC[TestClientRPC](target)
    r
  }

  "DefaultClientRPC" should tests(createDefaultClientRPC)
}
