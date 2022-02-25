package io.udash.web.homepage

import io.udash._
import io.udash.logging.CrossLogging
import io.udash.routing.WindowUrlPathChangeProvider

import scala.scalajs.js.annotation.JSExport

object Context {
  private val routingRegistry = new RoutingRegistryDef
  private val viewFactoriesRegistry = new StatesToViewFactoryDef

  implicit val applicationInstance: Application[RoutingState] =
    new Application[RoutingState](routingRegistry, viewFactoriesRegistry, new WindowUrlPathChangeProvider)
}

object Init extends CrossLogging {
  import Context._

  @JSExport
  def main(args: Array[String]): Unit =
    applicationInstance.run("#application")
}