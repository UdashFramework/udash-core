package io.udash.routing

import io.udash._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.concurrent.JSExecutionContext
import scala.util.Try

/**
  * RoutingRegistry mixin simplifying logging app navigation.
  */
trait UrlLogging[S <: GState[S]] extends StrictLogging { app: Application[S] =>

  implicit protected val loggingEC: ExecutionContext = JSExecutionContext.queue
  protected def log(url: String, referrer: Option[String]): Unit

  app.onStateChange((event: StateChangeEvent[S]) => {
    Future(log(matchState(event.currentState).value, Try(matchState(event.oldState).value).toOption))
      .failed.foreach(t => logger.warn("Logging url change failed: {}", t.getMessage))
  })
}
