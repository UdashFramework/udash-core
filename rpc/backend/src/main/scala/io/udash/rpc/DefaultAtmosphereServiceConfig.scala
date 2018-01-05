package io.udash.rpc

import com.typesafe.scalalogging.LazyLogging
import io.udash.utils.{CallbacksHandler, Registration}
import org.atmosphere.cpr._

import scala.util.Try

/**
  * <p>Default [[io.udash.rpc.AtmosphereServiceConfig]] implementation.</p>
  *
  * <p>Creates RPC endpoint per HTTP connection. Endpoint can be aware of [[io.udash.rpc.ClientId]]. </p>
  */
class DefaultAtmosphereServiceConfig[ServerRPCType](localRpc: (ClientId) => ExposesServerRPC[ServerRPCType])
  extends AtmosphereServiceConfig[ServerRPCType] with LazyLogging {

  protected val RPCName = "RPC"

  private val _connections = new DefaultAtmosphereResourceSessionFactory
  protected def connections: AtmosphereResourceSessionFactory = _connections

  protected val newConnectionCallbacks = new CallbacksHandler[ClientId]
  protected val closedConnectionCallbacks = new CallbacksHandler[ClientId]

  override def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType] = connections.synchronized {
    if (connections.getSession(resource).getAttribute(RPCName) == null) initRpc(resource)
    connections.getSession(resource).getAttribute(RPCName).asInstanceOf[ExposesServerRPC[ServerRPCType]]
  }

  override def initRpc(resource: AtmosphereResource): Unit = connections.synchronized {
    val session = connections.getSession(resource)

    if (session.getAttribute(RPCName) == null) {
      val clientId = ClientId(resource.uuid())
      session.setAttribute(RPCName, localRpc(clientId))
      newConnectionCallbacks.fire(clientId)
    }
  }

  override def filters: Seq[(AtmosphereResource) => Try[Any]] = List()

  override def onClose(resource: AtmosphereResource): Unit = {
    val clientId = ClientId(resource.uuid())
    closedConnectionCallbacks.fire(clientId)
  }

  /** Registers callback which will be called on every new connection. */
  def onNewConnection(callback: newConnectionCallbacks.CallbackType): Registration =
    newConnectionCallbacks.register(callback)

  /** Registers callback which will be called on every closed connection. */
  def onClosedConnection(callback: closedConnectionCallbacks.CallbackType): Registration =
    closedConnectionCallbacks.register(callback)
}