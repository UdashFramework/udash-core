package io.udash
package rpc

import io.udash.logging.CrossLogging
import io.udash.rpc.internals.JsWebSocketConnector
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.TimeoutConfig
import org.scalajs.dom
import org.scalajs.dom.raw.{CloseEvent, ErrorEvent, Event, MessageEvent}

import scala.concurrent.duration.FiniteDuration

class JsRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  serverUrl: String,
  exceptionsRegistry: ExceptionCodecRegistry,
  protected override val timeouts: TimeoutConfig
) extends AbstractRpcClient[LocalRpcApi, RemoteRpcApi](localApiImpl, exceptionsRegistry) with UsesRemoteRpc[RemoteRpcApi] with CrossLogging {
  private var callIdGen = -1

  override protected def newCallId(): String = {
    callIdGen += 1
    callIdGen.toString
  }

  override protected def timeoutCallback(callback: () => Unit, timeout: FiniteDuration): Unit = {
    dom.window.setTimeout(callback, timeout.toMillis)
  }

  protected def createSession(): WebSocketConnector = {
    new JsWebSocketConnector(
      withClientIdParam(serverUrl, clientId),
      onOpen, onClose, onError, onWebSocketText
    )
  }

  protected def onOpen(event: Event): Unit = ()

  protected def onClose(event: CloseEvent): Unit = {
    connectorClosed()
  }

  protected def onError(event: Event): Unit = {
    logger.warn("WebSocket connection error: " + event.asInstanceOf[ErrorEvent].message)
    connectorClosed()
  }

  protected def onWebSocketText(event: MessageEvent): Unit = {
    handleMessage(event.data.toString)(exceptionsRegistry)
  }
}