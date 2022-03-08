package io.udash.rpc.internals

import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.logging.CrossLogging
import io.udash.rpc._
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.utils.{CallbacksHandler, Registration}
import io.udash.wrappers.atmosphere.Transport.Transport
import io.udash.wrappers.atmosphere._
import org.scalajs.dom

import scala.scalajs.js
import scala.util.control.NonFatal

trait ServerConnector {
  /** Sends RPCRequest to server. */
  def sendRpcRequest(request: RpcRequest): Unit
}

/** [[io.udash.rpc.internals.ServerConnector]] implementation based on Atmosphere framework. */
abstract class AtmosphereServerConnector(
  private val serverUrl: String,
  val exceptionsRegistry: ExceptionCodecRegistry
) extends ServerConnector with CrossLogging {

  protected val clientRpc: ExposesClientRPC[_]

  def handleResponse(response: RpcResponse): Any
  def handleRpcFire(fire: RpcFire): Any

  private var waitingRequests = new js.Array[RpcRequest]()
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

  def uuid: String = socket.getUUID().toString

  override def sendRpcRequest(request: RpcRequest): Unit = {
    val msg = JsonStringOutput.write[RpcRequest](request)
    if (isReady == ConnectionStatus.Open) socket.push(msg)
    else waitingRequests += request
  }

  private def handleMessage(msg: String): Unit = {
    implicit val ecr: ExceptionCodecRegistry = exceptionsRegistry
    try JsonStringInput.read[RpcServerMessage](msg) match {
      case fire: RpcFire => handleRpcFire(fire)
      case response: RpcResponse => handleResponse(response)
    } catch {
      case NonFatal(e) =>
        logger.error(s"Failure reading server message: $msg", e)
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
      val queue = waitingRequests
      waitingRequests = js.Array()
      queue.foreach(sendRpcRequest)
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
      onMessagePublished = (req: AtmosphereRequest, _: AtmosphereResponse) => handleMessage(req.responseBody)
    )
  }
}

class DefaultAtmosphereServerConnector(
  override protected val clientRpc: DefaultExposesClientRPC[_],
  responseHandler: RpcResponse => Any,
  serverUrl: String,
  override val exceptionsRegistry: ExceptionCodecRegistry
) extends AtmosphereServerConnector(serverUrl, exceptionsRegistry) {

  override def handleResponse(response: RpcResponse): Any =
    responseHandler(response)

  override def handleRpcFire(fire: RpcFire): Any =
    clientRpc.handleRpcFire(fire)
}
