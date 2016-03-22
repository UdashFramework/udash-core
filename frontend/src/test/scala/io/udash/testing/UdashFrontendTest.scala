package io.udash.testing

import scala.concurrent.ExecutionContextExecutor

trait UdashFrontendTest extends UdashSharedTest {
  import scalatags.JsDom.all.div
  def emptyComponent() = div().render

  implicit val testExecutionContext = new ExecutionContextExecutor {
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
}