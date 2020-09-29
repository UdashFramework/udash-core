package io.udash.routing

import io.udash._
import io.udash.logging.CrossLogging

import scala.util.Try

/**
  * RoutingRegistry mixin simplifying logging app navigation.
  */
trait UrlLogging[S >: Null <: GState[S]] extends CrossLogging { app: Application[S] =>
  protected def log(url: String, referrer: Option[String]): Unit

  app.onStateChange(event =>
    Try(log(matchState(event.currentState), Try(matchState(event.oldState)).toOption))
      .failed
      .foreach(t => logger.warn("Logging url change failed: {}", t.getMessage)))
}
