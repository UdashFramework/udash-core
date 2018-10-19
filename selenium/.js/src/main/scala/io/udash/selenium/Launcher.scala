package io.udash.selenium

import com.softwaremill.sttp.Uri
import io.udash.Application
import io.udash.rest.SttpRestClient
import io.udash.routing.{UrlLogging, WindowUrlPathChangeProvider}
import io.udash.rpc.DefaultServerRPC
import io.udash.selenium.routing.{RoutingRegistryDef, RoutingState, StatesToViewFactoryDef}
import io.udash.selenium.rpc.demos.rest.MainServerREST
import io.udash.selenium.rpc.{GuideExceptions, MainClientRPC, MainServerRPC, RPCService}
import io.udash.selenium.views.demos.UrlLoggingDemoService
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
      UrlLoggingDemoService.log(url, referrer)
  }
  val serverRpc: MainServerRPC =
    DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService, exceptionsRegistry = GuideExceptions.registry)

  val restServer: MainServerREST = {
    val (scheme, defaultPort) =
      if (dom.window.location.protocol == "https:") ("https", 443) else ("http", 80)
    val port = Try(dom.window.location.port.toInt).getOrElse(defaultPort)
    SttpRestClient[MainServerREST](Uri(scheme, dom.window.location.hostname, port, List("rest_api")))
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    jQ((jThis: Element) => {
      val appRoot = jQ("#application").get(0).get
      applicationInstance.run(appRoot)
    })
  }
}
