package io.udash.rpc

import io.udash.rpc.internals.UsesServerRPC

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

/** Default implementation of [[io.udash.rpc.internals.UsesServerRPC]]. */
class DefaultServerRPC[ServerRPCType <: RPC](val remoteRpcAsReal: AsRealRPC[ServerRPCType], val connector: ServerConnector) extends UsesServerRPC[ServerRPCType] {
  override implicit def executionContext: ExecutionContext = JSExecutionContext.queue
}

object DefaultServerRPC {
  /** Creates [[io.udash.rpc.DefaultServerRPC]] for provided RPC interfaces. */
  def apply[ClientRPCType <: ClientRPC, ServerRPCType <: RPC]
  (localRpc: ClientRPCType)(implicit localRpcAsRaw: AsRawRPC[ClientRPCType], serverRpcAsReal: AsRealRPC[ServerRPCType]): ServerRPCType = {

    val clientRPC = new ExposesClientRPC[ClientRPCType](localRpc)
    lazy val serverConnector = new AtmosphereServerConnector((resp) => serverRPC.handleResponse(resp), clientRPC)
    lazy val serverRPC: DefaultServerRPC[ServerRPCType] = new DefaultServerRPC(serverRpcAsReal, serverConnector)
    serverRPC.remoteRpc
  }
}
