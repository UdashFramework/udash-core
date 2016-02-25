package io.udash.rpc.internals

import io.udash.utils.Logger
import io.udash.wrappers.atmosphere._
import upickle.Invalid
import upickle.default._

import scala.collection.mutable
import scala.scalajs.js.timers

trait ServerConnector {
  /** Sends RPCRequest to server. */
  def sendRPCRequest(request: RPCRequest): Unit
}

/** [[io.udash.rpc.internals.ServerConnector]] implementation based on Atmosphere framework. */
class AtmosphereServerConnector(responseHandler: (RPCResponse) => Any, private val clientRpc: ExposesClientRPC[_], private val serverUrl: String = "/atm") extends ServerConnector {

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
    val msg = write[RPCRequest](request)
    if (isReady) socket.push(msg)
    else waitingRequests += request
  }

  private def handleMessage(msg: String) = {
    try {
      val response: RPCResponse = read[RPCResponse](msg)
      responseHandler(response)
    } catch {
      case _: Invalid.Json | _: Invalid.Data =>
        try {
          read[RPCRequest](msg) match {
            case fire: RPCFire =>
              clientRpc.handleRpcFire(fire)
            case unhandled =>
              Logger.error(s"Unhandled RPCRequest: $unhandled")
          }
        } catch {
          case _: Invalid.Json | _: Invalid.Data =>
            Logger.error(s"Unhandled message: $msg")
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
