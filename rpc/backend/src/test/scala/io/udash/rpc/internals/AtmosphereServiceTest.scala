package io.udash.rpc.internals

import java.io.{CharArrayWriter, PrintWriter}

import io.udash.rpc._
import io.udash.testing.UdashRpcBackendTest
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT
import org.atmosphere.cpr._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class AtmosphereServiceTest extends UdashRpcBackendTest {
  
  def createBroadcasters(): (BroadcasterMock, BroadcasterFactoryMock, MetaBroadcasterMock) = {
    val broadcaster = new BroadcasterMock
    val broadcasterFactory = new BroadcasterFactoryMock(broadcaster)
    val metaBroadcaster = new MetaBroadcasterMock
    (broadcaster, broadcasterFactory, metaBroadcaster)
  }

  def createTestRPC(): (mutable.Builder[String, Seq[String]], DefaultExposesServerRPC[TestRPC]) = {
    val calls: mutable.Builder[String, Seq[String]] = Seq.newBuilder[String]
    val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
      calls += method
    })
    val rpc: DefaultExposesServerRPC[TestRPC] = new DefaultExposesServerRPC[TestRPC](impl)
    (calls, rpc)
  }

  def createConfigs(filters: Seq[(AtmosphereResource) => Try[Any]], resolveRpcResult: ExposesServerRPC[TestRPC],
                    broadcasterFactory: BroadcasterFactoryMock, metaBroadcaster: MetaBroadcasterMock): (AtmosphereService[TestRPC], AtmosphereConfigMock)  = {
    val config = new AtmosphereServiceConfigMock[TestRPC](filters, resolveRpcResult)
    val atm = new AtmosphereService[TestRPC](config)
    val atmConfig = new AtmosphereConfigMock(broadcasterFactory, metaBroadcaster)
    (atm, atmConfig)
  }

  def createConfigsWithoutRpc(): (AtmosphereServiceConfigMock[TestRPC], AtmosphereService[TestRPC])  = {
    val config = new AtmosphereServiceConfigMock[TestRPC]()
    val atm = new AtmosphereService[TestRPC](config)
    (config, atm)
  }

  val requestBody = DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
    DefaultServerUdashRPCFramework.RPCCall(
      DefaultServerUdashRPCFramework.RawInvocation("doStuffWithFail", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
      List(),
      "callId1"
    )
  )

  "AtmosphereService" should {
    "init BroadcastManager" in {
      BroadcastManager.synchronized {
        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (atm, atmConfig) = createConfigs(Seq.empty, null, broadcasterFactory, metaBroadcaster)

        BroadcastManager.init(null, null)
        intercept[IllegalArgumentException] {
          BroadcastManager.broadcastToAllClients("test")
        }

        atm.init(atmConfig)
        BroadcastManager.broadcastToAllClients("test")

        metaBroadcaster.broadcasts.size should be(1)
        metaBroadcaster.broadcasts(0)._2 should be("test")
      }
    }

    "filter incoming fires" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(
          Seq((_) => Success(""), (_) => Failure(new RuntimeException), (_) => Success("")),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCFire(
            DefaultServerUdashRPCFramework.RawInvocation("doStuffInteger", List(List(DefaultServerUdashRPCFramework.write[Int](5)))), List()
          )
        ))

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          resource.suspended should be(true)
        }

        calls.result().size should be(0)
      }
    }

    "filter incoming calls" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(
          Seq((_) => Success(""), (_) => Failure(new RuntimeException), (_) => Success("")),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCCall(
            DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
            List(),
            "callId1"
          )
        ))

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RPCResponseFailure")) shouldNot be(empty)
          resource.suspended should be(true)
        }

        calls.result().size should be(0)
      }
    }

    "handle incoming websocket fire" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(
          Seq((_) => Success(""), (_) => Success("")),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCFire(
            DefaultServerUdashRPCFramework.RawInvocation("handle", List()),
            List()
          )
        ))

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("handle")
        }

        broadcasterFactory.lookups should contain("/client/uuid123")
        broadcaster.addedResources.size should be(1)
        broadcaster.addedResources should contain(resource)
        broadcaster.broadcasts.filter(_.contains("RPCResponseFailure")) should be(empty)
        resource.suspended should be(true)
      }
    }

    "handle incoming websocket call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCCall(
            DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
            List(),
            "callId1"
          )
        ))

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("doStuff")
        }

        broadcasterFactory.lookups should contain("/client/uuid123")
        broadcaster.addedResources.size should be(1)
        broadcaster.addedResources should contain(resource)
        broadcaster.broadcasts.filter(_.contains("RPCResponseSuccess")) shouldNot be(empty)
        broadcaster.broadcasts.filter(_.contains("RPCResponseFailure")) should be(empty)
        resource.suspended should be(true)
      }
    }

    "handle incoming websocket failing call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(requestBody)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)
        eventually {
          calls.result() should contain("doStuffWithFail")
        }

        broadcasterFactory.lookups should contain("/client/uuid123")
        broadcaster.addedResources.size should be(1)
        broadcaster.addedResources should contain(resource)
        broadcaster.broadcasts.filter(_.contains("RPCResponseSuccess")) should be(empty)
        broadcaster.broadcasts.filter(_.contains("RPCResponseFailure")) shouldNot be(empty)
        resource.suspended should be(true)
      }
    }

    "handle incoming polling fire" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCFire(
            DefaultServerUdashRPCFramework.RawInvocation("handle", List()),
            List()
          )
        ))

        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("handle")
        }

        broadcasterFactory.lookups shouldNot be(empty)
        broadcaster.addedResources.size should be(0)
        resource.getBroadcaster shouldNot be(null)
        resource.suspended should be(false)
        resource.resumed should be(true)
      }
    }

    "handle incoming polling call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
          DefaultServerUdashRPCFramework.RPCCall(
            DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
            List(),
            "callId1"
          )
        ))

        val response = new AtmosphereResponseMock(null)
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request, response)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("doStuff")
          response.write should be(true)
          response.error should be(false)
        }

        broadcasterFactory.lookups shouldNot be(empty)
        broadcaster.addedResources.size should be(0)
        resource.getBroadcaster shouldNot be(null)
        resource.suspended should be(false)
        resource.resumed should be(true)
      }
    }

    "handle incoming polling failing call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(requestBody)

        val response = new AtmosphereResponseMock(null)
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request, response)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("doStuffWithFail")
          response.write should be(true)
          response.error should be(false)
        }

        broadcasterFactory.lookups shouldNot be(empty)
        broadcaster.addedResources.size should be(0)
        resource.getBroadcaster shouldNot be(null)
        resource.suspended should be(false)
        resource.resumed should be(true)
      }
    }

    "handle incoming polling broken request" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Seq.empty, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(requestBody.substring(5))

        val response = new AtmosphereResponseMock(null)
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request, response)

        atm.init(atmConfig)
        atm.onRequest(resource)
        eventually {
          calls.result() shouldNot contain("doStuffWithFail")
          response.write should be(false)
          response.error should be(true)
        }

        broadcasterFactory.lookups shouldNot be(empty)
        broadcaster.addedResources.size should be(0)
        resource.getBroadcaster shouldNot be(null)
      }
    }

    "suspend and register SSE request" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (atm, atmConfig) = createConfigs(Seq.empty, null, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        val resource = new AtmosphereResourceMock(TRANSPORT.SSE, "sseUuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          broadcasterFactory.lookups should contain("/client/sseUuid123")
          broadcaster.addedResources.size should be(1)
          resource.suspended should be(true)
          resource.resumed should be(false)
        }
      }
    }

    "call onClose when connection gets closed" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (config, atm) = createConfigsWithoutRpc()

        val request = AtmosphereRequestImpl.newInstance()
        val response = new AtmosphereResponseMock(null)

        val resource = new AtmosphereResourceMock(TRANSPORT.SSE, "sseUuid123", request, response)
        val event = new AtmosphereResourceEventImpl(resource)
        event.setCancelled(true)

        atm.onStateChange(event)

        eventually {
          config.closed should be(true)
        }
      }
    }

    "send broadcasts over websocket" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (config, atm) = createConfigsWithoutRpc()

        val request = AtmosphereRequestImpl.newInstance()
        val out: CharArrayWriter = new java.io.CharArrayWriter()
        val response = new AtmosphereResponseMock(new PrintWriter(out))
        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request, response)

        val event = new AtmosphereResourceEventImpl(resource)
        event.setMessage(
          DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
            DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
          )
        )

        atm.onStateChange(event)

        eventually {
          config.closed should be(false)
          out.toString.contains("\"callId\":\"call1\"") should be(true)
        }
      }
    }

    "send broadcasts over SSE" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (config, atm) = createConfigsWithoutRpc()

        val request = AtmosphereRequestImpl.newInstance()
        val out: CharArrayWriter = new java.io.CharArrayWriter()
        val response = new AtmosphereResponseMock(new PrintWriter(out))
        val resource = new AtmosphereResourceMock(TRANSPORT.SSE, "sseUuid123", request, response)

        val event = new AtmosphereResourceEventImpl(resource)
        event.setMessage(
          DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
            DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
          )
        )

        atm.onStateChange(event)

        eventually {
          config.closed should be(false)
          out.toString.contains("\"callId\":\"call1\"") should be(true)
        }
      }
    }

    "send response over polling connection" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (config, atm) = createConfigsWithoutRpc()

        val request = AtmosphereRequestImpl.newInstance()
        val out: CharArrayWriter = new java.io.CharArrayWriter()
        val response = new AtmosphereResponseMock(new PrintWriter(out))
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123123123", request, response)

        val event = new AtmosphereResourceEventImpl(resource)
        event.setMessage(
          DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
            DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
          )
        )

        atm.onStateChange(event)

        eventually {
          config.closed should be(false)
          resource.resumed should be(true)
          out.toString.contains("\"callId\":\"call1\"") should be(true)
        }
      }
    }
  }

}
