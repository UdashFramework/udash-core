package io.udash.rpc

import io.udash.testing.UdashRpcBackendTest
import org.atmosphere.cpr.AtmosphereResource

class DefaultAtmosphereServiceConfigTest extends UdashRpcBackendTest {

  "DefaultAtmosphereServiceConfig" should {
    "create RPC for new connection" in {
      var counter = 0
      val config = new DefaultAtmosphereServiceConfig[TestRPC](_ => { counter += 1; new DefaultExposesServerRPC(null) })

      config.initRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "1", null, null))
      counter should be(1)

      config.initRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "2", null, null))
      counter should be(2)

      config.initRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "1", null, null))
      config.initRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "2", null, null))
      counter should be(2)
    }

    "resolve RPC for connection" in {
      var counter = 0
      val config = new DefaultAtmosphereServiceConfig[TestRPC](_ => { counter += 1; new DefaultExposesServerRPC(null) })

      config.resolveRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "1", null, null)) shouldNot be(null)
      counter should be(1)

      config.resolveRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "2", null, null)) shouldNot be(null)
      counter should be(2)

      config.resolveRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "1", null, null)) shouldNot be(null)
      config.resolveRpc(new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "2", null, null)) shouldNot be(null)
      counter should be(2)
    }

    "fire callbacks on new or closed conenction" in {
      var counter = 0
      val config = new DefaultAtmosphereServiceConfig[TestRPC](_ => new DefaultExposesServerRPC(null))

      val c1 = config.onNewConnection { case _ => counter += 1 }
      val c2 = config.onClosedConnection { case _ => counter -= 1 }

      val r1 = new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "1", null, null)
      val r2 = new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "2", null, null)
      val r3 = new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "3", null, null)
      val r4 = new AtmosphereResourceMock(AtmosphereResource.TRANSPORT.WEBSOCKET, "4", null, null)

      config.resolveRpc(r1)
      counter should be(1)

      config.resolveRpc(r2)
      counter should be(2)

      config.onClose(r2)
      counter should be(1)

      config.resolveRpc(r3)
      counter should be(2)

      config.onClose(r1)
      counter should be(1)

      config.onClose(r3)
      counter should be(0)

      c1.cancel()
      c2.cancel()

      config.resolveRpc(r4)
      counter should be(0)

      config.onClose(r4)
      counter should be(0)
    }
  }
}
