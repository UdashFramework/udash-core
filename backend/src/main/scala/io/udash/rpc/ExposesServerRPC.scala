package io.udash.rpc

import scala.concurrent.Future

abstract class ExposesServerRPC[ServerRPCType](local: ServerRPCType) extends ExposesLocalRPC[ServerRPCType] {
  import framework._
  /**
    * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
    * into calls on actual RPC implementation.
    */
  protected def localRpcAsRaw: AsRawRPC[ServerRPCType]

  protected lazy val rawLocalRpc = localRpcAsRaw.asRaw(localRpc)

  override protected def localRpc: ServerRPCType = local

  /** Handles RPCCall and returns Future with call result. */
  def handleRpcCall(call: RPCCall): Future[RawValue] = {
    val receiver = rawLocalRpc.resolveGetterChain(call.gettersChain)
    receiver.call(call.invocation.rpcName, call.invocation.argLists)
  }

  /** Handles RPCFire */
  def handleRpcFire(fire: RPCFire): Unit = {
    val receiver = rawLocalRpc.resolveGetterChain(fire.gettersChain)
    receiver.fire(fire.invocation.rpcName, fire.invocation.argLists)
  }
}

final class DefaultExposesServerRPC[ServerRPCType]
  (local: ServerRPCType)(implicit protected val localRpcAsRaw: DefaultUdashRPCFramework.AsRawRPC[ServerRPCType])
  extends ExposesServerRPC(local) {

  override val framework = DefaultUdashRPCFramework
}