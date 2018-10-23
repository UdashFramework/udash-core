package io.udash.rpc.internals

import io.udash.rpc._

/**
  * Base class for server-side components which use some RPC exposed by client-side.
  */
private[rpc] abstract class UsesClientRPC[ClientRPCType] extends UsesRemoteRPC[ClientRPCType] {
  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc: ClientRPCType =
    remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: ClientRawRpc.AsRealRpc[ClientRPCType]

  protected class RawRemoteRPC(getterChain: List[RpcInvocation]) extends ClientRawRpc {
    def fire(invocation: RpcInvocation): Unit =
      fireRemote(getterChain, invocation)

    def get(invocation: RpcInvocation): ClientRawRpc =
      new RawRemoteRPC(invocation :: getterChain)
  }
}
