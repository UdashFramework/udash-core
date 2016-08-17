package io.udash.rpc.internals

import com.typesafe.scalalogging.LazyLogging
import org.atmosphere.cpr._

private[rpc] trait BroadcasterInit extends LazyLogging {

  private var factory: BroadcasterFactory = _
  private var metaBroadcaster: MetaBroadcaster = _

  def init(factory: BroadcasterFactory, metaBroadcaster: MetaBroadcaster) = {
    if (this.factory != null || this.metaBroadcaster != null) {
      logger.warn("BroadcastManager is reinitialized! It should not happen!")
    }

    this.factory = factory
    this.metaBroadcaster = metaBroadcaster
  }

  protected final def withBroadcaster(clientId: String)(op: Broadcaster => Unit): Unit = {
    require(factory != null, "Init manager with BroadcasterFactory first!")
    op(factory.lookup[Broadcaster](clientPath(clientId), true))
  }

  protected final def withMetaBroadcaster(op: MetaBroadcaster => Unit): Unit = {
    require(metaBroadcaster != null, "Init manager with MetaBroadcaster first!")
    op(metaBroadcaster)
  }

  protected final def clientPath(clientId: String) = s"/client/$clientId"

  protected final def pathWildcard = "*"
}

private[rpc] object BroadcastManager extends BroadcasterInit {

  def registerResource(resource: AtmosphereResource, clientId: String): Unit =
    withBroadcaster(clientId)(_.addAtmosphereResource(resource))

  def sendToClient(clientId: String, msg: String): Unit =
    withBroadcaster(clientId)(_.broadcast(msg))

  def broadcastToAllClients(msg: String): Unit =
    withMetaBroadcaster(_.broadcastTo(clientPath(pathWildcard), msg))

  def broadcast(msg: String): Unit =
    withMetaBroadcaster(_.broadcastTo(clientPath(pathWildcard), msg))

}
