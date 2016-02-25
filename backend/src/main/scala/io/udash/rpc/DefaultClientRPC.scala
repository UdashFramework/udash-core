package io.udash.rpc

import io.udash.rpc.internals.{BroadcastManager, UsesClientRPC}
import upickle.default._

import scala.concurrent.ExecutionContext

/**
  * Target for server to client call. Possible values:<br/>
  * * [[io.udash.rpc.AllClients]] - all connected clients<br/>
  * * [[io.udash.rpc.ClientId]] - one concrete client connection id
  */
sealed trait ClientRPCTarget
case object AllClients extends ClientRPCTarget
case class ClientId(id: String) extends ClientRPCTarget

/** Default implementation of UsesClientRPC for server to client communication. */
class DefaultClientRPC[ClientRPCType <: ClientRPC](target: ClientRPCTarget, override protected val remoteRpcAsReal: AsRealRPC[ClientRPCType])
                                                  (implicit ec: ExecutionContext) extends UsesClientRPC[ClientRPCType] {

  override protected def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit = {
    val msg: String = write[RPCRequest](RPCFire(invocation, getterChain))
    target match {
      case AllClients =>
        BroadcastManager.broadcast(msg)
      case ClientId(clientId) =>
        BroadcastManager.sendToClient(clientId, msg)
    }
  }

  def get: ClientRPCType = remoteRpc

  override protected implicit def executionContext: ExecutionContext = ec
}
