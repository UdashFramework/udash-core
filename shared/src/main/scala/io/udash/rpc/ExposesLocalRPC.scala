package io.udash.rpc

/**
 * Base trait for anything that exposes some RPC interface.
 */
trait ExposesLocalRPC[T <: RPC] extends HasExecutionContext {
  /**
   * Implementation of local RPC interface. Common approach is to implement the local RPC directly and
   * return reference to `this` here.
   */
  protected def localRpc: T

  /**
   * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
   * into calls on actual RPC implementation.
   */
  protected def localRpcAsRaw: AsRawRPC[T]

  protected lazy val rawLocalRpc = localRpcAsRaw.asRaw(localRpc)
}
