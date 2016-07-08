package io.udash.rpc.internals

import java.io.PrintWriter
import java.util.concurrent.TimeUnit

import io.udash.rpc._
import io.udash.testing.UdashBackendTest
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT
import org.atmosphere.cpr._

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

class MockablePrintWriter extends PrintWriter(new java.io.CharArrayWriter())

class MockableAtmosphereConfig extends AtmosphereConfig(null)

trait MockableAtmosphereResource extends AtmosphereResource {
  override def initialize(config: AtmosphereConfig,
                          broadcaster: Broadcaster,
                          req: AtmosphereRequest,
                          response: AtmosphereResponse,
                          asyncSupport: AsyncSupport[_ <: AtmosphereResource],
                          atmosphereHandler: AtmosphereHandler) : AtmosphereResource
}

class AtmosphereResponseMock(writer: PrintWriter) extends AtmosphereResponseImpl(null, null, false) {
  var error = false
  var write = false

  override def sendError(sc: Int): Unit = { error = true }
  override def write(data: String): AtmosphereResponse = {
    write = true
    null
  }

  override def getWriter: PrintWriter = writer
}

class AtmosphereServiceTest extends UdashBackendTest {

  "AtmosphereService" should {
    "init BroadcastManager" in {
      BroadcastManager.synchronized {
        val broadcasterFactory = mock[BroadcasterFactory]
        val metaBroadcaster = mock[MetaBroadcaster]
        val broadcaster = mock[Broadcaster]
        val config = mock[AtmosphereServiceConfig[TestRPC]]
        val atm = new AtmosphereService[TestRPC](config)

        val atmConfig = mock[MockableAtmosphereConfig]
        (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
        (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

        (metaBroadcaster.broadcastTo(_: String, _: Any)).expects(*, "test").once()

        atm.init(atmConfig)
        BroadcastManager.broadcastToAllClients("test")
      }
    }

    "filter incoming fires" in {
      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCFire(
          DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))), List()
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.WEBSOCKET)
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().atLeastOnce().returns("uuid123")
      (resource.getRequest _).expects().atLeastOnce().returns(request)

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/uuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()
      (config.resolveRpc _).expects(resource).once().returns(rpc)
      (config.filters _).expects().returns(Seq((_) => Success(""), (_) => Failure(new RuntimeException), (_) => Success("")))

      atm.init(atmConfig)
      atm.onRequest(resource)
      TimeUnit.SECONDS.sleep(1)
      calls.result().size should be(0)
    }

    "filter incoming calls" in {
      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.WEBSOCKET)
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().atLeastOnce().returns("uuid123")
      (resource.getRequest _).expects().atLeastOnce().returns(request)

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/uuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()
      (broadcaster.broadcast(_: Any)).expects(where((msg: Any) => msg.toString.contains("RPCResponseFailure"))).once()
      (config.resolveRpc _).expects(resource).once().returns(rpc)
      (config.filters _).expects().returns(Seq((_) => Success(""), (_) => Failure(new RuntimeException), (_) => Success("")))

      atm.init(atmConfig)
      atm.onRequest(resource)
      TimeUnit.SECONDS.sleep(1)
      calls.result().size should be(0)
    }

    "handle incoming websocket fire" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCFire(
          DefaultServerUdashRPCFramework.RawInvocation("handle", List()),
          List()
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.WEBSOCKET)
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().atLeastOnce().returns("uuid123")
      (resource.getRequest _).expects().atLeastOnce().returns(request)

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/uuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("handle")
      }
    }

    "handle incoming websocket call" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.WEBSOCKET)
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().atLeastOnce().returns("uuid123")
      (resource.getRequest _).expects().atLeastOnce().returns(request)

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/uuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()
      (broadcaster.broadcast(_: Any)).expects(where((msg: Any) => msg.toString.contains("RPCResponseSuccess"))).once()
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("doStuff")
      }
      TimeUnit.SECONDS.sleep(1)
    }

    "handle incoming websocket failing call" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuffWithFail", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.WEBSOCKET)
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().atLeastOnce().returns("uuid123")
      (resource.getRequest _).expects().atLeastOnce().returns(request)

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/uuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()
      (broadcaster.broadcast(_: Any)).expects(where((msg: Any) => msg.toString.contains("RPCResponseFailure"))).once()
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("doStuffWithFail")
      }

      Thread.sleep(10) //Wait for broadcaster.broadcast(_: Any)) call on broadcaster mock
    }

    "handle incoming polling fire" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCFire(
          DefaultServerUdashRPCFramework.RawInvocation("handle", List()),
          List()
        )
      ))

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.POLLING)
      (resource.uuid _).expects().atLeastOnce().returns("123456-654321") // create custom brodcaster
      (resource.setBroadcaster _).expects(broadcaster).once() // create custom brodcaster
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().never()
      (resource.getRequest _).expects().atLeastOnce().returns(request)
      (resource.getResponse _).expects().never()
      (resource.resume _).expects().once()

      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects(*, *).once().returns(broadcaster) // create custom brodcaster
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("handle")
      }
    }

    "handle incoming polling call" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuff", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ))

      val response = new AtmosphereResponseMock(null)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.POLLING)
      (resource.uuid _).expects().atLeastOnce().returns("123456-654321") // create custom brodcaster
      (resource.setBroadcaster _).expects(broadcaster).once() // create custom brodcaster
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().never()
      (resource.getRequest _).expects().atLeastOnce().returns(request)
      (resource.getResponse _).expects().atLeastOnce().returns(response)
      (resource.resume _).expects().once()

      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects(*, *).once().returns(broadcaster) // create custom brodcaster
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("doStuff")
        response.write should be(true)
        response.error should be(false)
      }
    }

    "handle incoming polling failing call" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuffWithFail", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ))

      val response = new AtmosphereResponseMock(null)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.POLLING)
      (resource.uuid _).expects().atLeastOnce().returns("123456-654321") // create custom brodcaster
      (resource.setBroadcaster _).expects(broadcaster).once() // create custom brodcaster
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().never()
      (resource.getRequest _).expects().atLeastOnce().returns(request)
      (resource.getResponse _).expects().atLeastOnce().returns(response)
      (resource.resume _).expects().once()

      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects(*, *).once().returns(broadcaster) // create custom brodcaster
      (config.filters _).expects().returns(Seq())
      (config.resolveRpc _).expects(resource).returns(rpc)

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() should contain("doStuffWithFail")
        response.write should be(true)
        response.error should be(false)
      }
    }

    "handle incoming polling broken request" in {
      val calls = Seq.newBuilder[String]
      val impl = TestRPC.rpcImpl((method: String, args: List[List[Any]], result: Option[Any]) => {
        calls += method
      })
      val rpc = new DefaultExposesServerRPC[TestRPC](impl)

      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()
      request.body(DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCRequest](
        DefaultServerUdashRPCFramework.RPCCall(
          DefaultServerUdashRPCFramework.RawInvocation("doStuffWithFail", List(List(DefaultServerUdashRPCFramework.write[Boolean](true)))),
          List(),
          "callId1"
        )
      ).substring(5))

      val response = new AtmosphereResponseMock(null)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.POLLING)
      (resource.uuid _).expects().atLeastOnce().returns("123456-654321") // create custom brodcaster
      (resource.setBroadcaster _).expects(broadcaster).once() // create custom brodcaster
      (resource.suspend _).expects().once()
      (resource.uuid _).expects().never()
      (resource.getRequest _).expects().atLeastOnce().returns(request)
      (resource.getResponse _).expects().atLeastOnce().returns(response)
      (resource.resume _).expects().never()
      (config.resolveRpc _).expects(resource).once().returns(rpc)

      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects(*, *).once().returns(broadcaster) // create custom brodcaster

      atm.init(atmConfig)
      atm.onRequest(resource)
      eventually {
        calls.result() shouldNot contain("doStuffWithFail")
        response.write should be(false)
        response.error should be(true)
      }
    }

    "suspend and register SSE request" in {
      val broadcasterFactory = mock[BroadcasterFactory]
      val metaBroadcaster = mock[MetaBroadcaster]
      val broadcaster = mock[Broadcaster]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val atmConfig = mock[MockableAtmosphereConfig]
      (atmConfig.getBroadcasterFactory _).expects().once().returns(broadcasterFactory)
      (atmConfig.metaBroadcaster _).expects().once().returns(metaBroadcaster)

      val request = AtmosphereRequestImpl.newInstance()

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().atLeastOnce().returns(TRANSPORT.SSE)
      (resource.suspend(_: Long)).expects(*).once()
      (resource.uuid _).expects().once().returns("sseUuid123")

      (config.initRpc _).expects(resource).once()
      (broadcasterFactory.lookup[Broadcaster](_: Any, _: Boolean)).expects("/client/sseUuid123", *).atLeastOnce().returns(broadcaster)
      (broadcaster.addAtmosphereResource _).expects(resource).once()
      (broadcaster.setBroadcasterLifeCyclePolicy _).expects(*).never()

      atm.init(atmConfig)
      atm.onRequest(resource)
      TimeUnit.SECONDS.sleep(1)
    }

    "call onClose when connection gets closed" in {
      val event = mock[AtmosphereResourceEvent]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val request = AtmosphereRequestImpl.newInstance()
      val response = new AtmosphereResponseMock(null)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().never().returns(TRANSPORT.SSE)
      (resource.getResponse _).expects().once().returns(response)

      (event.getResource _).expects().atLeastOnce().returns(resource)
      (event.isCancelled _).expects().once().returns(true)
      (config.onClose _).expects(resource).once()

      atm.onStateChange(event)
      TimeUnit.SECONDS.sleep(1)
    }

    "send broadcasts over websocket" in {
      val event = mock[AtmosphereResourceEvent]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val writer = mock[MockablePrintWriter]
      (writer.write(_: String)).expects(*).once()
      (writer.flush _).expects().once()

      val request = AtmosphereRequestImpl.newInstance()
      val response = new AtmosphereResponseMock(writer)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().once().returns(TRANSPORT.WEBSOCKET)
      (resource.getResponse _).expects().once().returns(response)

      (event.getMessage _).expects().atLeastOnce().returns(
        DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
          DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
        )
      )
      (event.getResource _).expects().atLeastOnce().returns(resource)
      (event.isCancelled _).expects().once().returns(false)
      (event.isClosedByApplication _).expects().once().returns(false)
      (event.isClosedByClient _).expects().once().returns(false)
      (config.onClose _).expects(resource).never()

      atm.onStateChange(event)
      TimeUnit.SECONDS.sleep(1)
    }

    "send broadcasts over SSE" in {
      val event = mock[AtmosphereResourceEvent]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val writer = mock[MockablePrintWriter]
      (writer.write(_: String)).expects(*).once()
      (writer.flush _).expects().once()

      val request = AtmosphereRequestImpl.newInstance()
      val response = new AtmosphereResponseMock(writer)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().once().returns(TRANSPORT.SSE)
      (resource.getResponse _).expects().once().returns(response)

      (event.getMessage _).expects().atLeastOnce().returns(
        DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
          DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
        )
      )
      (event.getResource _).expects().atLeastOnce().returns(resource)
      (event.isCancelled _).expects().once().returns(false)
      (event.isClosedByApplication _).expects().once().returns(false)
      (event.isClosedByClient _).expects().once().returns(false)
      (config.onClose _).expects(resource).never()

      atm.onStateChange(event)
      TimeUnit.SECONDS.sleep(1)
    }

    "send response over polling connection" in {
      val event = mock[AtmosphereResourceEvent]
      val config = mock[AtmosphereServiceConfig[TestRPC]]
      val atm = new AtmosphereService[TestRPC](config)

      val writer = mock[MockablePrintWriter]
      (writer.write(_: String)).expects(*).once()
      (writer.flush _).expects().never()

      val request = AtmosphereRequestImpl.newInstance()
      val response = new AtmosphereResponseMock(writer)

      val resource = mock[MockableAtmosphereResource]
      (resource.transport _).expects().once().returns(TRANSPORT.POLLING)
      (resource.getResponse _).expects().once().returns(response)
      (resource.resume _).expects().once()

      (event.getMessage _).expects().atLeastOnce().returns(
        DefaultServerUdashRPCFramework.write[DefaultServerUdashRPCFramework.RPCResponse](
          DefaultServerUdashRPCFramework.RPCResponseSuccess(DefaultServerUdashRPCFramework.write[String]("response"), "call1")
        )
      )
      (event.getResource _).expects().atLeastOnce().returns(resource)
      (event.isCancelled _).expects().once().returns(false)
      (event.isClosedByApplication _).expects().once().returns(false)
      (event.isClosedByClient _).expects().once().returns(false)
      (config.onClose _).expects(resource).never()

      atm.onStateChange(event)
      TimeUnit.SECONDS.sleep(1)
    }
  }

}
