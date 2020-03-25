package io.udash.wrappers.atmosphere

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}

object Transport {
  type Transport = String

  val POLLING: Transport = "polling"
  val LONG_POLLING: Transport = "long-polling"
  val STREAMING: Transport = "streaming"
  val JSONP: Transport = "jsonp"
  val SSE: Transport = "sse"
  val WEBSOCKET: Transport = "websocket"
}

object Method {
  type Method = String

  val OPTIONS: Method = "OPTIONS"
  val GET: Method = "GET"
  val HEAD: Method = "HEAD"
  val POST: Method = "POST"
  val PUT: Method = "PUT"
  val DELETE: Method = "DELETE"
  val TRACE: Method = "TRACE"
  val CONNECT: Method = "CONNECT"
}

object State {
  type StateValue = String

  val MESSAGE_RECEIVED: StateValue = "messageReceived"
  val ERROR: StateValue = "error"
  val OPENING: StateValue = "opening"
  val MESSAGE_PUBLISHED: StateValue = "messagePublished"
  val RE_CONNECTING: StateValue = "re-connecting"
  val CLOSED_BY_CLIENT: StateValue = "closedByClient"
  val FAIL_TO_RECONNECT: StateValue = "fail-to-reconnect"
  val UNSUBSCRIBE: StateValue = "unsubscribe"
  val CLOSED: StateValue = "closed"
  val RE_OPENING: StateValue = "re-opening"
}

@js.native
@JSImport("atmosphere.js", JSImport.Namespace, "atmosphere")
object Atmosphere extends js.Object {
  def subscribe(request: AtmosphereRequest): AtmosphereRequest = js.native

  def publish(request: AtmosphereRequest): Unit = js.native

  def unsubscribe(): Unit = js.native

  def addCallback(callback: js.Function1[AtmosphereResponse, Any]): Unit = js.native

  val util: js.Dynamic = js.native
}

@js.native
trait AtmosphereRequest extends js.Object {
  def url: String = js.native
  def webSocketUrl: String = js.native

  def connectTimeout: Int = js.native
  def reconnectInterval: Int = js.native
  def timeout: Int = js.native

  def method: Method.Method = js.native
  def fallbackMethod: Method.Method = js.native

  def headers: js.Array[js.Any] = js.native
  def contentType: String = js.native
  def data: String = js.native

  def suspend: Boolean = js.native
  def maxRequest: Int = js.native
  def maxStreamingLength: Int = js.native

  def logLevel: String = js.native

  def transport: Transport.Transport = js.native
  def fallbackTransport: Transport.Transport = js.native

  def webSocketImpl: js.Any = js.native
  def webSocketPathDelimiter: String = js.native

  def enableXDR: Boolean = js.native
  def rewriteURL: Boolean = js.native
  def attachHeadersAsQueryString: Boolean = js.native
  def dropHeaders: Boolean = js.native
  def executeCallbackBeforeReconnect: Boolean = js.native
  def withCredentials: Boolean = js.native

  def trackMessageLength: Boolean = js.native
  def messageDelimiter: String = js.native

  def shared: Boolean = js.native
  def enableProtocol: Boolean = js.native
  def readResponseHeaders: Boolean = js.native

  def maxReconnectOnClose: Int = js.native
  def pollingInterval: Int = js.native
  @JSName("heartbeat.server") def heartbeatServer: Int = js.native
  def closeAsync: Boolean = js.native
  def ackInterval: Int = js.native
  def reconnectOnServerError: Boolean = js.native
  def reconnectOnWindowLocationChange: Boolean = js.native

  def responseBody: String = js.native

  def callback: js.Function1[AtmosphereResponse, Any] = js.native
  def onOpen: js.Function1[AtmosphereResponse, Any] = js.native
  def onClose: js.Function1[AtmosphereResponse, Any] = js.native
  def onMessage: js.Function1[AtmosphereResponse, Any] = js.native
  def onError: js.Function1[AtmosphereResponse, Any] = js.native
  def onReconnect: js.Function2[AtmosphereRequest, AtmosphereResponse, Any] = js.native
  def onMessagePublished: js.Function2[AtmosphereRequest, AtmosphereResponse, Any] = js.native
  def onClientTimeout: js.Function1[AtmosphereResponse, Any] = js.native
  def onTransportFailure: js.Function2[String, AtmosphereResponse, Any] = js.native

  def push(request: AtmosphereRequest): Unit = js.native
  def push(request: String): Unit = js.native
}

@js.native
trait AtmosphereResponse extends js.Object {
  def status: Int = js.native
  def reason: String = js.native
  def responseBody: String = js.native
  def headers: js.Array[js.Any] = js.native
  def state: State.StateValue = js.native
  def transport: String = js.native
  def error: String = js.native
  def request: AtmosphereRequest = js.native
}

object AtmosphereRequest {
  def apply(url: String, connectTimeout: Int = -1, reconnectInterval: Int = 1000, timeout: Int = 300000,
    method: Method.Method = Method.GET, fallbackMethod: Method.Method = Method.GET, headers: js.Array[js.Any] = js.Array(),
    contentType: String = "", data: String = "", suspend: Boolean = true, maxRequest: Int = -1, maxStreamingLength: Int = 10000000,
    logLevel: String = "info", transport: Transport.Transport = Transport.LONG_POLLING,
    fallbackTransport: Transport.Transport = Transport.LONG_POLLING, webSocketImpl: js.Any = null,
    webSocketPathDelimiter: String = "@@", enableXDR: Boolean = false, rewriteURL: Boolean = false,
    attachHeadersAsQueryString: Boolean = true, dropHeaders: Boolean = true, executeCallbackBeforeReconnect: Boolean = false,
    withCredentials: Boolean = false, trackMessageLength: Boolean = false, messageDelimiter: String = "|",
    shared: Boolean = false, enableProtocol: Boolean = true, readResponseHeaders: Boolean = false,
    maxReconnectOnClose: Int = 5, pollingInterval: Int = 0, heartbeatServer: Int = 0, closeAsync: Boolean = false,
    ackInterval: Int = 0, reconnectOnServerError: Boolean = true, reconnectOnWindowLocationChange: Boolean = false,
    callback: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onOpen: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onReopen: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onClose: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onMessage: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onError: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onReconnect: js.Function2[AtmosphereRequest, AtmosphereResponse, Any] = (_: AtmosphereRequest, _: AtmosphereResponse) => {},
    onMessagePublished: js.Function2[AtmosphereRequest, AtmosphereResponse, Any] = (_: AtmosphereRequest, _: AtmosphereResponse) => {},
    onClientTimeout: js.Function1[AtmosphereResponse, Any] = (_: AtmosphereResponse) => {},
    onTransportFailure: js.Function2[String, AtmosphereRequest, Any] = (_: String, _: AtmosphereRequest) => {})
  : AtmosphereRequest = {
    js.Dynamic.literal(
      url = url,
      connectTimeout = connectTimeout,
      reconnectInterval = reconnectInterval,
      timeout = timeout,
      method = method.toString,
      fallbackMethod = fallbackMethod.toString,
      headers = headers,
      contentType = contentType,
      data = data,
      suspend = suspend,
      maxRequest = maxRequest,
      maxStreamingLength = maxStreamingLength,
      logLevel = logLevel,
      transport = transport.toString,
      fallbackTransport = fallbackTransport.toString,
      webSocketImpl = webSocketImpl,
      webSocketPathDelimiter = webSocketPathDelimiter,
      enableXDR = enableXDR,
      rewriteURL = rewriteURL,
      attachHeadersAsQueryString = attachHeadersAsQueryString,
      dropHeaders = dropHeaders,
      executeCallbackBeforeReconnect = executeCallbackBeforeReconnect,
      withCredentials = withCredentials,
      trackMessageLength = trackMessageLength,
      messageDelimiter = messageDelimiter,
      shared = shared,
      enableProtocol = enableProtocol,
      readResponseHeaders = readResponseHeaders,
      maxReconnectOnClose = maxReconnectOnClose,
      pollingInterval = pollingInterval,
      heartbeatServer = heartbeatServer,
      closeAsync = closeAsync,
      ackInterval = ackInterval,
      reconnectOnServerError = reconnectOnServerError,
      reconnectOnWindowLocationChange = reconnectOnWindowLocationChange,
      callback = callback,
      onOpen = onOpen,
      onClose = onClose,
      onMessage = onMessage,
      onError = onError,
      onReconnect = onReconnect,
      onMessagePublished = onMessagePublished,
      onClientTimeout = onClientTimeout,
      onTransportFailure = onTransportFailure
    ).asInstanceOf[AtmosphereRequest]
  }
}
