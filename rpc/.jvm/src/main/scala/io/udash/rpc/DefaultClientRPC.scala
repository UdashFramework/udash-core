package io.udash.rpc

import com.avsystem.commons.rpc.AsReal
import com.avsystem.commons.serialization.json.JsonStringOutput
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

  override protected def fireRemote(getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit = {
    val json = JsonStringOutput.write[RpcRequest](RpcFire(invocation, getterChain))
    target match {
      case AllClients =>
        BroadcastManager.broadcast(json)
      case ClientId(clientId) =>
        BroadcastManager.sendToClient(clientId, json)
    }
  }

  def get: ClientRPCType = remoteRpc
}

/** Default implementation of [[io.udash.rpc.ClientRPC]] for server to client communication. */
class DefaultClientRPC[ClientRPCType: ClientRawRpc.AsRealRpc](target: ClientRPCTarget)(implicit ec: ExecutionContext)
  extends ClientRPC[ClientRPCType](target) {

  protected val remoteRpcAsReal: ClientRawRpc.AsRealRpc[ClientRPCType] =
    AsReal[ClientRawRpc, ClientRPCType]
}
