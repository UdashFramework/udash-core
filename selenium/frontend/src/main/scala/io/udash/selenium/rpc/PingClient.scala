package io.udash.selenium.rpc

import io.udash.selenium.rpc.demos.rpc.PingClientRPC

object PingClient extends PingClientRPC {
  private val pongListeners = scala.collection.mutable.ArrayBuffer[Int => Any]()

  override def pong(id: Int): Unit = {
    pongListeners.foreach(l => l(id))
  }

  def registerPongListener(listener: Int => Any) = pongListeners += listener
  def unregisterPongListener(listener: (Int) => Any) = pongListeners -= listener
}
