package io.udash

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

package object bootstrap extends BootstrapImplicits {
  implicit val ec: ExecutionContext = JSExecutionContext.queue
}
