package io.udash.rpc.internals

import io.udash.logging.CrossLogging
import io.udash.rpc._
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.utils.{CallbacksHandler, Registration}
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
abstract class AtmosphereServerConnector[RPCRequest](
  private val serverUrl: String,
  val exceptionsRegistry: ExceptionCodecRegistry
) extends ServerConnector[RPCRequest] with CrossLogging {

  protected val clientRpc: ExposesClientRPC[_]

  val remoteFramework: ServerUdashRPCFramework
  val localFramework: ClientUdashRPCFramework

  def requestToString(request: RPCRequest): String

  def handleResponse(response: remoteFramework.RPCResponse): Any
  def handleRpcFire(fire: localFramework.RPCFire): Any

  private val waitingRequests = new mutable.ArrayBuffer[RPCRequest]()
  private var isReady: ConnectionStatus = ConnectionStatus.Closed
  private val websocketSupport: Boolean = scala.scalajs.js.Dynamic.global.WebSocket != null

  private var onReconnectTimeoutHandler = 0

  protected val connectionStatusCallbacks = new CallbacksHandler[ConnectionStatus]

  private val socket: AtmosphereRequest = {
    val reconnectInterval = 1000
    val atmRequest: AtmosphereRequest = {
      if (websocketSupport)
        createRequestObject(
          Transport.WEBSOCKET, reconnectInterval,
          onOpen = (_: AtmosphereResponse) => ready(ConnectionStatus.Open),
          onReopen = (_: AtmosphereResponse) => ready(ConnectionStatus.Open),
          onReconnect = (_: AtmosphereRequest, _: AtmosphereResponse) => {
            if (onReconnectTimeoutHandler != 0) dom.window.clearTimeout(onReconnectTimeoutHandler)
            ready(ConnectionStatus.Closed)
            onReconnectTimeoutHandler = dom.window.setTimeout(() => {
              ready(ConnectionStatus.Open)
              onReconnectTimeoutHandler = 0
            }, reconnectInterval * 2)
          },
          onError = (_: AtmosphereResponse) => ready(ConnectionStatus.Closed),
          onClose = (_: AtmosphereResponse) => ready(ConnectionStatus.Closed),
          onClientTimeout = (_: AtmosphereResponse) => ready(ConnectionStatus.Closed)
        )
      else {
        isReady = ConnectionStatus.Open
        createRequestObject(Transport.SSE, reconnectInterval)
      }
    }
    Atmosphere.subscribe(atmRequest)
  }

  override def sendRPCRequest(request: RPCRequest): Unit = {
    val msg = requestToString(request)
    if (isReady == ConnectionStatus.Open) socket.push(msg)
    else waitingRequests += request
  }

  private def handleMessage(msg: String) = {
    import localFramework.RPCRequestCodec
    import remoteFramework.RPCResponse
    implicit val ecr: ExceptionCodecRegistry = exceptionsRegistry
    try handleResponse(remoteFramework.read[RPCResponse](msg)) catch {
      case _: Exception =>
        try {
          localFramework.read[localFramework.RPCRequest](msg) match {
            case fire: localFramework.RPCFire =>
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

  /** Current status of connection to server. */
  def connectionStatus: ConnectionStatus =
    isReady

  /** Registers callback which will be called when connection status changed. */
  def onConnectionStatusChange(callback: connectionStatusCallbacks.CallbackType): Registration =
    connectionStatusCallbacks.register(callback)

  private def ready(isReady: ConnectionStatus): Unit = {
    this.isReady = isReady
    if (isReady == ConnectionStatus.Open) {
      val queue = new mutable.ArrayBuffer[RPCRequest]()
      waitingRequests.copyToBuffer(queue)
      waitingRequests.clear()

      queue foreach { req => sendRPCRequest(req) }
    }

    connectionStatusCallbacks.fire(isReady)
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
  responseHandler: DefaultServerUdashRPCFramework.RPCResponse => Any,
  serverUrl: String,
  override val exceptionsRegistry: ExceptionCodecRegistry)
  extends AtmosphereServerConnector[DefaultServerUdashRPCFramework.RPCRequest](serverUrl, exceptionsRegistry) {

  override val remoteFramework: DefaultServerUdashRPCFramework.type = DefaultServerUdashRPCFramework
  override val localFramework: DefaultClientUdashRPCFramework.type = DefaultClientUdashRPCFramework

  override def requestToString(request: remoteFramework.RPCRequest): String =
    remoteFramework.write(request)

  override def handleResponse(response: remoteFramework.RPCResponse): Any =
    responseHandler(response)

  override def handleRpcFire(fire: localFramework.RPCFire): Any =
    clientRpc.handleRpcFire(fire)

}
