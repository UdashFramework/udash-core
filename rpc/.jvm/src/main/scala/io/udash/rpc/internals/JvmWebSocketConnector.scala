package io.udash
package rpc.internals

import io.udash.rpc.WebSocketConnector
import javax.websocket.Session

class JvmWebSocketConnector(
  socket: Session
) extends WebSocketConnector {
  override def isOpen: Boolean = socket.isOpen
  override def isClosed: Boolean = !socket.isOpen
  override def close(): Unit = socket.close()
  override def send(text: String): Unit = socket.getBasicRemote.sendText(text)
}
