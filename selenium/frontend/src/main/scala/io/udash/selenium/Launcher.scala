package io.udash.selenium

import io.udash.Application
import io.udash.routing.{UrlLogging, WindowUrlPathChangeProvider}
import io.udash.rpc.DefaultServerRPC
import io.udash.selenium.routing.{RoutingRegistryDef, RoutingState, StatesToViewFactoryDef}
import io.udash.selenium.rpc.{GuideExceptions, MainClientRPC, MainServerRPC, RPCService}
import io.udash.selenium.rpc.demos.rest.MainServerREST
import io.udash.selenium.views.demos.UrlLoggingDemo
import io.udash.wrappers.jquery.jQ
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSExport
import scala.util.Try

object Launcher {
  implicit val executionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
  private lazy val routingRegistry = new RoutingRegistryDef
  private lazy val viewFactoryRegistry = new StatesToViewFactoryDef

  implicit val applicationInstance = new Application[RoutingState](
    routingRegistry, viewFactoryRegistry, WindowUrlPathChangeProvider
  ) with UrlLogging[RoutingState] {
    override protected def log(url: String, referrer: Option[String]): Unit =
      UrlLoggingDemo.log(url, referrer)
  }
  val serverRpc = DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService, exceptionsRegistry = GuideExceptions.registry)

  import io.udash.rest._
  private val (restProtocol, restPort) = if (dom.window.location.protocol == "https:") (Protocol.Https, 443) else (Protocol.Http, 80)
  val restServer = DefaultServerREST[MainServerREST](
    restProtocol, dom.window.location.hostname, Try(dom.window.location.port.toInt).getOrElse(restPort), "/rest_api/"
  )

  @JSExport
  def main(args: Array[String]): Unit = {
    jQ((jThis: Element) => {
      val appRoot = jQ("#application").get(0).get
      applicationInstance.run(appRoot)
    })
  }
}
