package io.udash.web

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object Implicits {
  implicit val backendExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))
}
