package io.udash.rpc

import scala.concurrent.Future

abstract class ExposesServerRPC[ServerRPCType](local: ServerRPCType) extends ExposesLocalRPC[ServerRPCType] {
  /**
    * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
    * into calls on actual RPC implementation.
    */
  protected def localRpcAsRaw: ServerRawRpc.AsRawRpc[ServerRPCType]

  protected lazy val rawLocalRpc: ServerRawRpc =
    localRpcAsRaw.asRaw(localRpc)

  override protected def localRpc: ServerRPCType = local

  /** Handles RPCCall and returns Future with call result. */
  def handleRpcCall(call: RpcCall): Future[JsonStr] =
    rawLocalRpc.handleCall(call)

  /** Handles RPCFire */
  def handleRpcFire(fire: RpcFire): Unit =
    rawLocalRpc.handleFire(fire)
}

class DefaultExposesServerRPC[ServerRPCType](local: ServerRPCType)(
  implicit protected val localRpcAsRaw: ServerRawRpc.AsRawRpc[ServerRPCType]
) extends ExposesServerRPC(local)
