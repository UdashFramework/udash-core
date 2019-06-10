package io.udash.web.homepage

import io.udash._
import io.udash.logging.CrossLogging
import io.udash.routing.WindowUrlPathChangeProvider
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSExport

object Context {
  implicit val executionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue
  private val routingRegistry = new RoutingRegistryDef
  private val viewFactoriesRegistry = new StatesToViewFactoryDef

  implicit val applicationInstance = new Application[RoutingState](routingRegistry, viewFactoriesRegistry, new WindowUrlPathChangeProvider)
}

object Init extends CrossLogging {
  import Context._

  @JSExport
  def main(args: Array[String]): Unit = {
    jQ((_: Element) => {
      val appRoot = jQ("#application").get(0)
      if (appRoot.isEmpty) {
        logger.error("Application root element not found! Check you index.html file!")
      } else applicationInstance.run(appRoot.get)
    })
  }
}