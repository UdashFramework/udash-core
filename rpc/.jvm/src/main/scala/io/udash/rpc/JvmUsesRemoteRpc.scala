package io.udash
package rpc

import java.util.concurrent.atomic.AtomicLong
import java.util.{Timer, TimerTask}

import scala.concurrent.duration.FiniteDuration

trait JvmUsesRemoteRpc[RemoteRpcApi] extends UsesRemoteRpc[RemoteRpcApi] {
  private val callIdGen: AtomicLong = new AtomicLong(0)
  private val timer: Timer = new Timer(true)

  override protected def newCallId(): String =
    callIdGen.getAndIncrement().toString

  override protected def timeoutCallback(callback: () => Unit, timeout: FiniteDuration): Unit = {
    timer.schedule(new TimerTask {
      override def run(): Unit =
        callback()
    }, timeout.toMillis)
  }
}
