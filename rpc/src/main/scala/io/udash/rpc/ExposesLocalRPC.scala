package io.udash.rpc

/**
  * Base trait for anything that exposes some RPC interface.
  */
trait ExposesLocalRPC[T] {
  /**
    * Implementation of local RPC interface. Common approach is to implement the local RPC directly and
    * return reference to `this` here.
    */
  protected def localRpc: T
}
