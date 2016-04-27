package io.udash.rpc.internals

import io.udash.rpc.UsesRemoteRPC

/**
 * Base class for server-side components which use some RPC exposed by client-side.
 */
private[rpc] abstract class UsesClientRPC[ClientRPCType] extends UsesRemoteRPC[ClientRPCType] {
  import framework._
  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  val remoteRpc = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: AsRealClientRPC[ClientRPCType]

  protected def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    throw new IllegalStateException("You can not broadcast remote call!")
}
