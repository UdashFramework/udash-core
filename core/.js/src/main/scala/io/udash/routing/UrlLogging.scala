package io.udash.routing

import io.udash._
import io.udash.logging.CrossLogging

import scala.concurrent.Future
import scala.util.Try

/**
  * RoutingRegistry mixin simplifying logging app navigation.
  */
trait UrlLogging[S >: Null <: GState[S]] extends CrossLogging { app: Application[S] =>
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  protected def log(url: String, referrer: Option[String]): Unit

  app.onStateChange((event: StateChangeEvent[S]) => {
    Future(log(matchState(event.currentState).value, Try(matchState(event.oldState).value).toOption))
      .failed.foreach(t => logger.warn("Logging url change failed: {}", t.getMessage))
  })
}
