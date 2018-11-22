package io.udash.rpc

import scala.concurrent.Future

/** Base trait for anything that exposes some RPC interface. */
trait ExposesLocalRpc[RpcApi] {
  protected def rawLocalRpc: RawRpc

  /** Handles RPCCall and returns Future with call result. */
  protected def handleRpcCall(call: RpcCall): Future[JsonStr] =
    rawLocalRpc.handleCall(call)

  /** Handles RPCFire */
  protected def handleRpcFire(fire: RpcFire): Unit =
    rawLocalRpc.handleFire(fire)
}
