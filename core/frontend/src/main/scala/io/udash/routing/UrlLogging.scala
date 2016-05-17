package io.udash.routing

import io.udash._

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.concurrent.JSExecutionContext
import scala.util.Try

/**
  * RoutingRegistry mixin simplifying logging app navigation.
  */
trait UrlLogging[S <: State] extends RoutingRegistry[S] with StrictLogging {

  implicit protected val loggingEC: ExecutionContext = JSExecutionContext.queue
  protected def log(url: String, referrer: Option[String]): Unit

  abstract override def matchUrl(url: Url, previous: S): S = {
    Future(log(url.value, Try(matchState(previous).value).toOption))
      .onFailure { case t => logger.warn("Logging url change failed: {}", t.getMessage) }
    super.matchUrl(url, previous)
  }

}
