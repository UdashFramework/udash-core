package io.udash.rpc.internals

import io.udash.rpc._

abstract class ExposesClientRPC[ClientRPCType](protected val localRpc: ClientRPCType)
  extends ExposesLocalRPC[ClientRPCType] {
  /**
    * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
    * into calls on actual RPC implementation.
    */
  protected def localRpcAsRaw: ClientRawRpc.AsRawRpc[ClientRPCType]

  protected lazy val rawLocalRpc: ClientRawRpc = localRpcAsRaw.asRaw(localRpc)

  /** Handles RPCFires */
  def handleRpcFire(fire: RpcFire): Unit =
    rawLocalRpc.handleFire(fire)
}

class DefaultExposesClientRPC[ClientRPCType](local: ClientRPCType)(
  implicit protected val localRpcAsRaw: ClientRawRpc.AsRawRpc[ClientRPCType]
) extends ExposesClientRPC(local)
