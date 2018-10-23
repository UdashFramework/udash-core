package io.udash.rpc

/**
  * Base trait for anything that uses remote RPC interface.
  */
trait UsesRemoteRPC[T] {
  /**
    * Sends the raw RPC invocation of method returning `Unit` through network.
    */
  protected def fireRemote(getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit
}
