package io.udash.rest

import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

object FutureUtils {
  private val scheduler = Executors.newSingleThreadScheduledExecutor()

  def delay(duration: FiniteDuration): Future[Unit] = {
    val result = Promise[Unit]()
    scheduler.schedule(() => result.success(()), duration.toMillis, TimeUnit.MILLISECONDS)
    result.future
  }
}
