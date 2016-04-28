package io.udash.rpc.internals

import com.typesafe.scalalogging.LazyLogging
import org.atmosphere.cpr._

private[rpc] object BroadcastManager extends LazyLogging {
  private var factory: BroadcasterFactory = null
  private var metaBroadcaster: MetaBroadcaster = null

  def init(factory: BroadcasterFactory, metaBroadcaster: MetaBroadcaster) = {
    if (this.factory != null || this.metaBroadcaster != null) {
      logger.warn("BroadcastManager is reinitialized! It should not happen!")
    }

    this.factory = factory
    this.metaBroadcaster = metaBroadcaster
  }

  def registerResource(resource: AtmosphereResource, clientId: String): Unit = {
    require(factory != null, "Init manager with BroadcasterFactory first!")

    val clientBroadcaster = factory.lookup[Broadcaster](clientPath(clientId), true)
    clientBroadcaster.addAtmosphereResource(resource)
  }

  def sendToClient(clientId: String, msg: String): Unit = {
    require(factory != null, "Init manager with BroadcasterFactory first!")

    val clientBroadcaster = factory.lookup[Broadcaster](clientPath(clientId), true)
    clientBroadcaster.broadcast(msg)
  }

  def broadcastToAllClients(msg: String): Unit = {
    require(metaBroadcaster != null, "Init manager with MetaBroadcaster first!")

    metaBroadcaster.broadcastTo(clientPath(pathWildcard), msg)
  }

  def broadcast(msg: String): Unit = {
    require(metaBroadcaster != null, "Init manager with MetaBroadcaster first!")

    metaBroadcaster.broadcastTo(clientPath(pathWildcard), msg)
  }

  private def clientPath(clientId: String) = s"/client/$clientId"

  private def pathWildcard = "*"
}
