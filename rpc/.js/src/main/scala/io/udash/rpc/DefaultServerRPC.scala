package io.udash.rpc

import com.avsystem.commons.rpc.AsReal
import io.udash.rpc.internals.UsesServerRPC
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}

import scala.concurrent.duration.{Duration, DurationDouble}

abstract class ServerRPC[ServerRPCType] extends UsesServerRPC[ServerRPCType]

/** Default implementation of [[io.udash.rpc.ServerRPC]]. */
class DefaultServerRPC[ServerRPCType: ServerRawRpc.AsRealRpc](
  override protected val connector: ServerConnector,
  override protected val callTimeout: Duration = 30 seconds
) extends ServerRPC[ServerRPCType] {
  override val remoteRpcAsReal: ServerRawRpc.AsRealRpc[ServerRPCType] =
    AsReal[ServerRawRpc, ServerRPCType]
}

object DefaultServerRPC {
  /** Creates [[io.udash.rpc.DefaultServerRPC]] for provided RPC interfaces. */
  def apply[ClientRPCType: ClientRawRpc.AsRawRpc, ServerRPCType: ServerRawRpc.AsRealRpc](
    localRpc: ClientRPCType, serverUrl: String = "/atm",
    exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry,
    rpcFailureInterceptors: Seq[PartialFunction[Throwable, Any]] = Seq.empty,
    callTimeout: Duration = 30 seconds
  ): ServerRPCType = {
    val clientRPC = new DefaultExposesClientRPC[ClientRPCType](localRpc)
    lazy val serverConnector = new DefaultAtmosphereServerConnector(
      clientRPC, resp => serverRPC.handleResponse(resp), serverUrl, exceptionsRegistry)
    lazy val serverRPC: DefaultServerRPC[ServerRPCType] =
      new DefaultServerRPC[ServerRPCType](serverConnector, callTimeout)
    rpcFailureInterceptors.foreach(serverRPC.onCallFailure)
    serverRPC.remoteRpc
  }
}
