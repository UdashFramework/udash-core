package io.udash
package rpc

trait WebSocketConnector {
  def isOpen: Boolean
  def isClosed: Boolean
  def close(): Unit
  def send(text: String): Unit
}
