package io.udash.rpc

import com.github.ghik.silencer.silent
import io.udash.rpc.internals.BroadcastManager
import io.udash.testing.UdashBackendTest
import org.atmosphere.cpr.{Broadcaster, BroadcasterFactory, MetaBroadcaster}

class DefaultClientRPCTest extends UdashBackendTest {
  implicit val ec = RunNowExecutionContext

  "ClientRPC" should {
    "gain access to RPC methods of concrete client" in {
      BroadcastManager.synchronized {
        val broadcasterFactory = mock[BroadcasterFactory]
        val metaBroadcaster = mock[MetaBroadcaster]
        val broadcaster = mock[Broadcaster]
        @silent
        val rpc = new DefaultClientRPC[TestClientRPC](ClientId("id123"), AsRealRPC[TestClientRPC]).get

        (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/id123", true).twice().returns(broadcaster)
        (broadcaster.broadcast(_: Any)).expects(*).twice()

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)
        rpc.handle
        rpc.handleMore()
      }
    }

    "gain access to RPC methods of all clients" in {
      BroadcastManager.synchronized {
        val broadcasterFactory = mock[BroadcasterFactory]
        val metaBroadcaster = mock[MetaBroadcaster]
        @silent
        val rpc = new DefaultClientRPC[TestClientRPC](AllClients, AsRealRPC[TestClientRPC]).get

        (metaBroadcaster.broadcastTo(_: String, _: Any)).expects("/client/*", *).twice()

        BroadcastManager.init(broadcasterFactory, metaBroadcaster)
        rpc.handle
        rpc.handleMore()
      }
    }

    "require BroadcastManager to be initialized" in {
      BroadcastManager.synchronized {
        @silent
        val rpc = new DefaultClientRPC[TestClientRPC](AllClients, AsRealRPC[TestClientRPC]).get

        BroadcastManager.init(null, null)
        intercept[IllegalArgumentException] {
          rpc.handle
        }
        intercept[IllegalArgumentException] {
          rpc.handleMore()
        }
      }
    }
  }
}
