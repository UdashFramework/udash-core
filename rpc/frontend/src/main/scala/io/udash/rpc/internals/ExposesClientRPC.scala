package io.udash.rpc.internals

import io.udash.rpc._

abstract class ExposesClientRPC[ClientRPCType](protected val localRpc: ClientRPCType) extends ExposesLocalRPC[ClientRPCType] {
  override val localFramework: ClientUdashRPCFramework

  /**
    * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
    * into calls on actual RPC implementation.
    */
  protected def localRpcAsRaw: localFramework.AsRawRPC[ClientRPCType]

  protected lazy val rawLocalRpc = localRpcAsRaw.asRaw(localRpc)

  /** Handles RPCFires */
  def handleRpcFire(fire: localFramework.RPCFire): Unit = {
    val receiver = localRpcAsRaw.asRaw(localRpc).resolveGetterChain(fire.gettersChain)
    receiver.fire(fire.invocation.rpcName)(fire.invocation.args)
  }
}

class DefaultExposesClientRPC[ClientRPCType](local: ClientRPCType)
                                            (implicit protected val localRpcAsRaw: DefaultClientUdashRPCFramework.AsRawRPC[ClientRPCType])
  extends ExposesClientRPC(local) {

  override val localFramework = DefaultClientUdashRPCFramework
}