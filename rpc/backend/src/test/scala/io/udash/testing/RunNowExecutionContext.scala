package io.udash.testing

import scala.concurrent.ExecutionContext

/**
 * Author: ghik
 * Created: 28/05/15.
 */
object RunNowExecutionContext extends ExecutionContext {
  def execute(runnable: Runnable): Unit = {
    try {
      runnable.run()
    } catch {
      case t: Throwable => reportFailure(t)
    }
  }

  def reportFailure(t: Throwable): Unit =
    t.printStackTrace()
}
