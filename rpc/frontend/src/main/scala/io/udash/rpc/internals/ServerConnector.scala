package io.udash.rpc.internals

import com.avsystem.commons.serialization.Input
import io.udash.utils.StrictLogging
import io.udash.rpc.{DefaultUdashRPCFramework, UdashRPCFramework}
import io.udash.wrappers.atmosphere.Transport.Transport
import io.udash.wrappers.atmosphere._
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js

trait ServerConnector[RPCRequest] {
  /** Sends RPCRequest to server. */
  def sendRPCRequest(request: RPCRequest): Unit
}

/** [[io.udash.rpc.internals.ServerConnector]] implementation based on Atmosphere framework. */
abstract class AtmosphereServerConnector[RPCRequest](private val serverUrl: String)
  extends ServerConnector[RPCRequest] with StrictLogging {
  protected val clientRpc: ExposesClientRPC[_]

  val rpcFramework: UdashRPCFramework

  import rpcFramework.{RPCFire, RPCResponse, read, stringToRaw}

  def requestToString(request: RPCRequest): String

  def handleResponse(response: RPCResponse): Any
  def handleRpcFire(fire: RPCFire): Any

  private val waitingRequests = new mutable.ArrayBuffer[RPCRequest]()
  private var isReady = false
  private val websocketSupport: Boolean = scala.scalajs.js.Dynamic.global.WebSocket != null

  private var onReconnectTimeoutHandler = 0

  private val socket: AtmosphereRequest = {
    val reconnectInterval = 1000
    val atmRequest: AtmosphereRequest = {
      if (websocketSupport)
        createRequestObject(
          Transport.WEBSOCKET, reconnectInterval,
          onOpen = (res: AtmosphereResponse) => ready(true),
          onReopen = (res: AtmosphereResponse) => ready(true),
          onReconnect = (req: AtmosphereRequest, res: AtmosphereResponse) => {
            if (onReconnectTimeoutHandler != 0) dom.window.clearTimeout(onReconnectTimeoutHandler)
            ready(false)
            onReconnectTimeoutHandler = dom.window.setTimeout(() => {
              ready(true)
              onReconnectTimeoutHandler = 0
            }, reconnectInterval * 2)
          },
          onError = (res: AtmosphereResponse) => ready(false),
          onClose = (res: AtmosphereResponse) => ready(false),
          onClientTimeout = (res: AtmosphereResponse) => ready(false)
        )
      else {
        isReady = true
        createRequestObject(Transport.SSE, reconnectInterval)
      }
    }
    Atmosphere.subscribe(atmRequest)
  }

  override def sendRPCRequest(request: RPCRequest): Unit = {
    val msg = requestToString(request)
    if (isReady) socket.push(msg)
    else waitingRequests += request
  }

  private def handleMessage(msg: String) = {
    import rpcFramework.{RPCResponseCodec, RPCRequestCodec}
    val rawMsg = stringToRaw(msg)
    val rawMsgInput: Input = rpcFramework.inputSerialization(rawMsg)
    try {
      val response = RPCResponseCodec.read(rawMsgInput)
      handleResponse(response)
    } catch {
      case _: Exception =>
        try {
          RPCRequestCodec.read(rawMsgInput) match {
            case fire: RPCFire =>
              handleRpcFire(fire)
            case unhandled =>
              logger.error(s"Unhandled RPCRequest: $unhandled")
          }
        } catch {
          case _: Exception =>
            logger.error(s"Unhandled message: $msg")
        }
    }
  }

  private def ready(isReady: Boolean) = {
    this.isReady = isReady
    if (isReady) {
      val queue = new mutable.ArrayBuffer[RPCRequest]()
      waitingRequests.copyToBuffer(queue)
      waitingRequests.clear()

      queue foreach { req => sendRPCRequest(req) }
    }
  }

  private def createRequestObject(transport: Transport, reconnectInterval: Int,
                                  onOpen: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
                                  onReopen: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
                                  onClose: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
                                  onError: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
                                  onReconnect: js.Function2[AtmosphereRequest, AtmosphereResponse, Any] = (_: AtmosphereRequest, _: AtmosphereResponse) => {},
                                  onClientTimeout: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
                                  onTransportFailure: js.Function2[String, AtmosphereRequest, Any] = (_: String, _: AtmosphereRequest) => {}
                                 ): AtmosphereRequest = {
    AtmosphereRequest(
      url = serverUrl,
      contentType = "application/json",
      logLevel = "info",
      transport = transport,
      fallbackTransport = transport,
      enableProtocol = true,
      reconnectInterval = reconnectInterval,
      maxReconnectOnClose = 36000,
      onOpen = onOpen,
      onReopen = onReopen,
      onReconnect = onReconnect,
      onError = onError,
      onClose = onClose,
      onClientTimeout = onClientTimeout,
      onMessage = (res: AtmosphereResponse) => handleMessage(res.responseBody),
      onMessagePublished = (req: AtmosphereRequest, res: AtmosphereResponse) => handleMessage(req.responseBody)
    )
  }
}

class DefaultAtmosphereServerConnector(override protected val clientRpc: DefaultExposesClientRPC[_],
                                       responseHandler: (DefaultUdashRPCFramework.RPCResponse) => Any,
                                       serverUrl: String) extends AtmosphereServerConnector[DefaultUdashRPCFramework.RPCRequest](serverUrl) {
  override val rpcFramework = DefaultUdashRPCFramework

  import rpcFramework._

  override def requestToString(request: RPCRequest): String =
    rawToString(write(request))

  override def handleResponse(response: RPCResponse): Any =
    responseHandler(response)

  override def handleRpcFire(fire: RPCFire): Any =
    clientRpc.handleRpcFire(fire)

}
