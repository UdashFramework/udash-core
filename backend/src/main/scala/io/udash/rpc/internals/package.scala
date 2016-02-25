package io.udash.rpc

import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

package object internals {
  implicit val internalRPCExecutionContext: ExecutionContext = new ExecutionContext with LazyLogging {
    val threadPool = Executors.newCachedThreadPool()

    def execute(runnable: Runnable): Unit = {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable): Unit = {
      logger.error("RPC execution fail", t)
    }
  }
}
