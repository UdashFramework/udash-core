package io.udash.rpc

/**
 * Base trait for anything that uses remote RPC interface.
 */
trait UsesRemoteRPC[T] {
  val localFramework: UdashRPCFramework
  val remoteFramework: UdashRPCFramework

  import remoteFramework._

  /**
   * Sends the raw RPC invocation of method returning `Unit` through network.
   */
  protected def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit
}
