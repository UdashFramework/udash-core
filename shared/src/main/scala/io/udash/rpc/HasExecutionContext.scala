package io.udash.rpc

import scala.concurrent.ExecutionContext

/**
 * Author: ghik
 * Created: 18/06/15.
 */
trait HasExecutionContext {
  /**
   * Execution context to be used by RPC-related Future callbacks
   */
  protected implicit def executionContext: ExecutionContext
}
