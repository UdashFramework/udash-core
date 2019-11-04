package io.udash.rpc.internals

import java.io.{CharArrayWriter, PrintWriter}

import com.avsystem.commons._
import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc._
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}
import io.udash.testing.UdashRpcBackendTest
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT
import org.atmosphere.cpr._

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

class AtmosphereServiceTest extends UdashRpcBackendTest {

  implicit val exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry
  exceptionsRegistry.register(GenCodec.materialize[CustomRPCException])

  def createBroadcasters(): (BroadcasterMock, BroadcasterFactoryMock, MetaBroadcasterMock) = {
    val broadcaster = new BroadcasterMock
    val broadcasterFactory = new BroadcasterFactoryMock(broadcaster)
    val metaBroadcaster = new MetaBroadcasterMock
    (broadcaster, broadcasterFactory, metaBroadcaster)
  }

  def createTestRPC(): (MBuilder[String, Seq[String]], DefaultExposesServerRPC[TestRPC]) = {
    val calls: MBuilder[String, Seq[String]] = Seq.newBuilder[String]
    val impl = TestRPC.rpcImpl((method: String, _: List[Any], _: Option[Any]) => {
      calls += method
    })
    val rpc: DefaultExposesServerRPC[TestRPC] = new DefaultExposesServerRPC[TestRPC](impl)
    (calls, rpc)
  }

  def createConfigs(filters: ISeq[AtmosphereResource => Try[Unit]], resolveRpcResult: ExposesServerRPC[TestRPC],
    broadcasterFactory: BroadcasterFactoryMock, metaBroadcaster: MetaBroadcasterMock): (AtmosphereService[TestRPC], AtmosphereConfigMock) = {
    val config = new AtmosphereServiceConfigMock[TestRPC](filters, resolveRpcResult)
    val atm = new AtmosphereService[TestRPC](config, exceptionsRegistry)
    val atmConfig = new AtmosphereConfigMock(broadcasterFactory, metaBroadcaster)
    (atm, atmConfig)
  }

  def createConfigsWithoutRpc(): (AtmosphereServiceConfigMock[TestRPC], AtmosphereService[TestRPC]) = {
    val config = new AtmosphereServiceConfigMock[TestRPC]()
    val atm = new AtmosphereService[TestRPC](config, exceptionsRegistry)
    (config, atm)
  }

  val failRequestBody: JsonStr = write[RpcRequest](
    RpcCall(
      RpcInvocation("doStuffWithFail", List(write[Boolean](true))),
      List(),
      "callId1"
    )
  )

  val exRequestBody: JsonStr = write[RpcRequest](
    RpcCall(
      RpcInvocation("doStuffWithEx", Nil),
      List(),
      "callId2"
    )
  )

  "AtmosphereService" should {
    "init BroadcastManager" in {
      BroadcastManager.synchronized {
        val (_, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (atm, atmConfig) = createConfigs(Nil, null, broadcasterFactory, metaBroadcaster)

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
          Vector(_ => Success(()), _ => Failure(new RuntimeException), _ => Success(())),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcFire(
            RpcInvocation("doStuffInteger", List(write[Int](5))), List()
          )
        ).json)

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
          Vector(_ => Success(()), _ => Failure(new RuntimeException), _ => Success(())),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcCall(
            RpcInvocation("doStuff", List(write[Boolean](true))),
            List(),
            "callId1"
          )
        ).json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RpcResponseFailure")) shouldNot be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseException")) should be(empty)
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
          Vector(_ => Success(()), _ => Success(())),
          rpc, broadcasterFactory, metaBroadcaster
        )

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcFire(
            RpcInvocation("handle", List()),
            List()
          )
        ).json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("handle")

          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RpcResponseFailure")) should be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseException")) should be(empty)
          resource.suspended should be(true)
        }
      }
    }

    "handle incoming websocket call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcCall(
            RpcInvocation("doStuff", List(write[Boolean](true))),
            List(),
            "callId1"
          )
        ).json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("doStuff")

          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RpcResponseSuccess")) shouldNot be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseFailure")) should be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseException")) should be(empty)
          resource.suspended should be(true)
        }
      }
    }

    "handle incoming websocket failing call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(failRequestBody.json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)
        eventually {
          calls.result() should contain("doStuffWithFail")

          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RpcResponseSuccess")) should be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseFailure")) shouldNot be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseException")) should be(empty)
          resource.suspended should be(true)
        }
      }
    }

    "log websocket failing call" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val loggerMock = new {
          private val errors = new MArrayBuffer[(String, Throwable)]
          def error(msg: String, throwable: Throwable) = errors += msg -> throwable
          def currentErrors = errors.toList
        }
        val expectedMsg = "Request handling failure"


        val (_, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (_, rpc) = createTestRPC()
        val config = new AtmosphereServiceConfigMock[TestRPC](Nil, rpc)
        val atm = new AtmosphereService[TestRPC](config, exceptionsRegistry,
          onRequestHandlingFailure = (ex, _) => loggerMock.error(expectedMsg, ex))
        val atmConfig = new AtmosphereConfigMock(broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(failRequestBody.json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)
        eventually {
          resource.suspended should be(true)
          loggerMock.currentErrors.size shouldBe 1
          loggerMock.currentErrors.head._1 shouldBe expectedMsg
        }
      }
    }

    "handle incoming websocket call throwing registered exception" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(exRequestBody.json)

        val resource = new AtmosphereResourceMock(TRANSPORT.WEBSOCKET, "uuid123", request)

        atm.init(atmConfig)
        atm.onRequest(resource)
        eventually {
          calls.result() should contain("doStuffWithEx")

          broadcasterFactory.lookups should contain("/client/uuid123")
          broadcaster.addedResources.size should be(1)
          broadcaster.addedResources should contain(resource)
          broadcaster.broadcasts.filter(_.contains("RpcResponseSuccess")) should be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseFailure")) should be(empty)
          broadcaster.broadcasts.filter(_.contains("RpcResponseException")) shouldNot be(empty)
          resource.suspended should be(true)
        }
      }
    }

    "handle incoming polling fire" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcFire(
            RpcInvocation("handle", List()),
            List()
          )
        ).json)

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
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(write[RpcRequest](
          RpcCall(
            RpcInvocation("doStuff", List(write[Boolean](true))),
            List(),
            "callId1"
          )
        ).json)

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

    "handle incoming polling call throwing registered exception" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(exRequestBody.json)

        val response = new AtmosphereResponseMock(null)
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request, response)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          calls.result() should contain("doStuffWithEx")
          response.write should be(true)
          response.writeData.contains("RpcResponseException") should be(true)
          response.error should be(false)
        }

        broadcasterFactory.lookups shouldNot be(empty)
        broadcaster.addedResources.size should be(0)
        resource.getBroadcaster shouldNot be(null)
        resource.suspended should be(false)
        resource.resumed should be(true)
      }
    }

    "log polling call throwing registered exception" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val loggerMock = new {
          private val errors = new MArrayBuffer[(String, Throwable)]
          def error(msg: String, throwable: Throwable) = errors += msg -> throwable
          def currentErrors = errors.toList
        }
        val expectedMsg = "Request handling failure"

        val (_, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (_, rpc) = createTestRPC()
        val config = new AtmosphereServiceConfigMock[TestRPC](Nil, rpc)
        val atm = new AtmosphereService[TestRPC](config, exceptionsRegistry,
          onRequestHandlingFailure = (ex, _) => loggerMock.error(expectedMsg, ex))
        val atmConfig = new AtmosphereConfigMock(broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(exRequestBody.json)

        val response = new AtmosphereResponseMock(null)
        val resource = new AtmosphereResourceMock(TRANSPORT.POLLING, "123456-654321", request, response)

        atm.init(atmConfig)
        atm.onRequest(resource)

        eventually {
          resource.suspended should be(false)
          loggerMock.currentErrors.size shouldBe 1
          loggerMock.currentErrors.head._1 shouldBe expectedMsg
        }
      }
    }

    "handle incoming polling broken request" in {
      BroadcastManager.synchronized {
        BroadcastManager.init(null, null)

        val (broadcaster, broadcasterFactory, metaBroadcaster) = createBroadcasters()
        val (calls, rpc) = createTestRPC()
        val (atm, atmConfig) = createConfigs(Nil, rpc, broadcasterFactory, metaBroadcaster)

        val request = AtmosphereRequestImpl.newInstance()
        request.body(failRequestBody.json.substring(5))

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
        val (atm, atmConfig) = createConfigs(Nil, null, broadcasterFactory, metaBroadcaster)

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
          write[RpcServerMessage](
            RpcResponseSuccess(write[String]("response"), "call1")
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
          write[RpcServerMessage](
            RpcResponseSuccess(write[String]("response"), "call1")
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
          write[RpcServerMessage](
            RpcResponseSuccess(write[String]("response"), "call1")
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
