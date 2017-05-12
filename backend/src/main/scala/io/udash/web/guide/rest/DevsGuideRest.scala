package io.udash.web.guide.rest

import akka.actor.{Actor, ActorRefFactory, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import io.udash.web.guide.demos.rest.RestExampleClass

import scala.concurrent.{ExecutionContext, Future}

object DevsGuideRest extends HttpSerializationUtils {
  import akka.http.scaladsl.server.Directives._

  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  private val route = CorsDirectives.cors() {
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

  private var bindingFuture: Future[Http.ServerBinding] = _

  def start(port: Int): Unit = {
    require(bindingFuture == null)
    bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port)
  }

  def stop(): Unit = {
    require(bindingFuture != null)
    bindingFuture.flatMap(_.unbind()).onComplete(_ => bindingFuture = null)
  }
}
