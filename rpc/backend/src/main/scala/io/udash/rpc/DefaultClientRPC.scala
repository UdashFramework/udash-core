package io.udash.rpc

import io.udash.rpc.internals.{BroadcastManager, UsesClientRPC}

import scala.concurrent.ExecutionContext

/**
  * Target for server to client call. Possible values:<br/>
  * * [[io.udash.rpc.AllClients]] - all connected clients<br/>
  * * [[io.udash.rpc.ClientId]] - one concrete client connection id
  */
sealed trait ClientRPCTarget
case object AllClients extends ClientRPCTarget
case class ClientId(id: String) extends ClientRPCTarget

abstract class ClientRPC[ClientRPCType](target: ClientRPCTarget)
                                       (implicit ec: ExecutionContext) extends UsesClientRPC[ClientRPCType] {
  override protected def fireRemote(getterChain: List[remoteFramework.RawInvocation], invocation: remoteFramework.RawInvocation): Unit = {
    import remoteFramework._
    val msg: RawValue = write[RPCRequest](RPCFire(invocation, getterChain))
    target match {
      case AllClients =>
        BroadcastManager.broadcast(msg.json)
      case ClientId(clientId) =>
        BroadcastManager.sendToClient(clientId, msg.json)
    }
  }

  def get: ClientRPCType = remoteRpc
}

/** Default implementation of [[io.udash.rpc.ClientRPC]] for server to client communication. */
class DefaultClientRPC[ClientRPCType : DefaultClientUdashRPCFramework.AsRealRPC]
                      (target: ClientRPCTarget)(implicit ec: ExecutionContext) extends ClientRPC[ClientRPCType](target) {
  override val localFramework: DefaultServerUdashRPCFramework.type = DefaultServerUdashRPCFramework
  override val remoteFramework: DefaultClientUdashRPCFramework.type = DefaultClientUdashRPCFramework
  protected val remoteRpcAsReal: DefaultClientUdashRPCFramework.AsRealRPC[ClientRPCType] = implicitly[DefaultClientUdashRPCFramework.AsRealRPC[ClientRPCType]]
}
