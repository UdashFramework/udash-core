package io.udash.rpc

import io.udash.rpc.internals.UsesServerRPC
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

abstract class ServerRPC[ServerRPCType] extends UsesServerRPC[ServerRPCType] {
  implicit def executionContext: ExecutionContext =
    JSExecutionContext.queue
}

/** Default implementation of [[io.udash.rpc.ServerRPC]]. */
class DefaultServerRPC[ServerRPCType : DefaultServerUdashRPCFramework.AsRealRPC]
                      (override protected val connector: ServerConnector[DefaultServerUdashRPCFramework.RPCRequest])
  extends ServerRPC[ServerRPCType] {
  override val remoteFramework = DefaultServerUdashRPCFramework
  override val localFramework = DefaultClientUdashRPCFramework
  override val remoteRpcAsReal: DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType] = implicitly[DefaultServerUdashRPCFramework.AsRealRPC[ServerRPCType]]
}

object DefaultServerRPC {
  /** Creates [[io.udash.rpc.DefaultServerRPC]] for provided RPC interfaces. */
  def apply[ClientRPCType : DefaultClientUdashRPCFramework.AsRawRPC,
            ServerRPCType : DefaultServerUdashRPCFramework.AsRealRPC]
           (localRpc: ClientRPCType, serverUrl: String = "/atm/",
            exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry,
            rpcFailureInterceptors: Seq[PartialFunction[Throwable, Any]] = Seq.empty): ServerRPCType = {
    val clientRPC = new DefaultExposesClientRPC[ClientRPCType](localRpc)
    lazy val serverConnector = new DefaultAtmosphereServerConnector(clientRPC, (resp) => serverRPC.handleResponse(resp), serverUrl, exceptionsRegistry)
    lazy val serverRPC: DefaultServerRPC[ServerRPCType] = new DefaultServerRPC[ServerRPCType](serverConnector)
    rpcFailureInterceptors.foreach(serverRPC.registerCallFailureCallback)
    serverRPC.remoteRpc
  }
}
