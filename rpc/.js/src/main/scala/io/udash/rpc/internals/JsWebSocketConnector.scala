package io.udash
package rpc.internals

import io.udash.rpc.WebSocketConnector
import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}

class JsWebSocketConnector(
  url: String,
  onOpen: Event => Unit,
  onClose: CloseEvent => Unit,
  onError: Event => Unit,
  onMessage: MessageEvent => Unit,
) extends WebSocketConnector {
  private val socket: WebSocket = new WebSocket(url)

  socket.onopen = onOpen
  socket.onclose = onClose
  socket.onerror = onError
  socket.onmessage = onMessage

  override def isOpen: Boolean = socket.readyState == WebSocket.OPEN
  override def isClosed: Boolean = socket.readyState == WebSocket.CLOSED || socket.readyState == WebSocket.CLOSING
  override def close(): Unit = socket.close()
  override def send(text: String): Unit = socket.send(text)
}
