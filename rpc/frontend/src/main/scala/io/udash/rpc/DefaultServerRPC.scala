package io.udash.rpc

import io.udash.rpc.internals.UsesServerRPC

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

abstract class ServerRPC[ServerRPCType] extends UsesServerRPC[ServerRPCType] {
  implicit def executionContext: ExecutionContext =
    JSExecutionContext.queue
}

/** Default implementation of [[io.udash.rpc.ServerRPC]]. */
class DefaultServerRPC[ServerRPCType](override protected val connector: ServerConnector[DefaultServerUdashRPCFramework.RPCRequest])
                                     (implicit override val remoteRpcAsReal: DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType])
  extends ServerRPC[ServerRPCType] {
  override val remoteFramework = DefaultServerUdashRPCFramework
  override val localFramework = DefaultClientUdashRPCFramework
}

object DefaultServerRPC {
  /** Creates [[io.udash.rpc.DefaultServerRPC]] for provided RPC interfaces. */
  def apply[ClientRPCType, ServerRPCType](localRpc: ClientRPCType, serverUrl: String = "/atm/")
                                         (implicit localRpcAsRaw: DefaultClientUdashRPCFramework.AsRawRPC[ClientRPCType],
                                          serverRpcAsReal: DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType]): ServerRPCType = {

    val clientRPC = new DefaultExposesClientRPC[ClientRPCType](localRpc)
    lazy val serverConnector = new DefaultAtmosphereServerConnector(clientRPC, (resp) => serverRPC.handleResponse(resp), serverUrl)
    lazy val serverRPC: DefaultServerRPC[ServerRPCType] = new DefaultServerRPC[ServerRPCType](serverConnector)
    serverRPC.remoteRpc
  }
}
