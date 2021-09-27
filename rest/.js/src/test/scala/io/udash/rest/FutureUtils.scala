package io.udash.rest

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.timers.setTimeout

object FutureUtils {
  def delay(duration: FiniteDuration): Future[Unit] = {
    val result = Promise[Unit]()
    setTimeout(duration)(result.success(()))
    result.future
  }
}
