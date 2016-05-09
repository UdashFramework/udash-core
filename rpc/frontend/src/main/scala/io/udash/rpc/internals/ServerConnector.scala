package io.udash.rpc.internals

import io.udash.StrictLogging
import io.udash.rpc.{DefaultUdashRPCFramework, UdashRPCFramework}
import io.udash.wrappers.atmosphere._

import scala.collection.mutable
import scala.scalajs.js.timers

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

  private val atmRequest = AtmosphereRequest(
    url = serverUrl,
    contentType = "application/json",
    logLevel = "info",
    transport = Transport.WEBSOCKET,
    fallbackTransport = Transport.SSE,
    maxReconnectOnClose = 36000,
    onOpen = (res: AtmosphereResponse) => ready(true),
    onReopen = (res: AtmosphereResponse) => ready(true),
    onError = (res: AtmosphereResponse) => ready(false),
    onReconnect = (req: AtmosphereRequest, res: AtmosphereResponse) => ready(false),
    onClose = (res: AtmosphereResponse) => {
      if (res.request != null) {
        if (res.request.method == Transport.WEBSOCKET || (res.request.method == Transport.SSE && res.request.method == Method.GET)) {
          ready(false)
        }
      }
    },
    onClientTimeout = (res: AtmosphereResponse) => ready(false),
    onMessage = (res: AtmosphereResponse) => handleMessage(res.responseBody),
    onMessagePublished = (req: AtmosphereRequest, res: AtmosphereResponse) => handleMessage(req.responseBody)
  )

  private var socket = Atmosphere.subscribe(atmRequest)

  timers.setInterval(5000)({
    if (!isReady) {
      Atmosphere.unsubscribe()
      socket = Atmosphere.subscribe(atmRequest)
    }
  })

  override def sendRPCRequest(request: RPCRequest): Unit = {
    val msg = requestToString(request)
    if (isReady) socket.push(msg)
    else waitingRequests += request
  }

  private def handleMessage(msg: String) = {
    val rawMsg = stringToRaw(msg)
    try {
      val response = read[RPCResponse](rawMsg)
      handleResponse(response)
    } catch {
      case _: Exception =>
        try {
          read[rpcFramework.RPCRequest](rawMsg) match {
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
