package io.udash.web.guide.rest

import akka.actor.{Actor, ActorRefFactory, Props}
import io.udash.web.guide.demos.rest.RestExampleClass
import spray.routing.HttpServiceBase

import scala.concurrent.ExecutionContext

trait DevsGuideRest extends HttpServiceBase with SpraySerializationUtils {

  val route = {
    pathPrefix("simple") {
      path("string") {
        get {
          complete("OK")
        }
      } ~
      path("int") {
        get {
          complete(123)
        }
      } ~
      path("class") {
        get {
          complete(RestExampleClass(42, "Udash", (321.123, "REST Support")))
        }
      }
    } ~
    pathPrefix("echo") {
      path(Segment) { urlPart =>
        get {
          Thread.sleep(500)
          complete(s"URL:$urlPart")
        }
      } ~
      headerValueByName("X-test") { headerValue =>
        get {
          Thread.sleep(500)
          complete(s"Header:$headerValue")
        }
      } ~
      parameter("param") { parameterValue =>
        get {
          Thread.sleep(500)
          complete(s"Query:$parameterValue")
        }
      } ~
      entity(as[String]) { bodyValue =>
        post {
          Thread.sleep(500)
          complete(s"Body:$bodyValue")
        }
      }
    }
  }
}

object DevsGuideRestActor {
  def props(prefix: String = "") = Props(classOf[DevsGuideRestActor], prefix)
}

class DevsGuideRestActor(prefix: String) extends Actor with DevsGuideRest {
  implicit lazy val executionContext: ExecutionContext = context.dispatcher
  def actorRefFactory: ActorRefFactory = context
  def receive = runRoute(if (prefix.nonEmpty) pathPrefix(prefix) {route} else route)
}
