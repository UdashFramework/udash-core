package io.udash.rpc.internals

import io.udash.rpc._

/**
 * Base class for server-side components which use some RPC exposed by client-side.
 */
private[rpc] abstract class UsesClientRPC[ClientRPCType] extends UsesRemoteRPC[ClientRPCType] {
  override val localFramework: ServerUdashRPCFramework
  override val remoteFramework: ClientUdashRPCFramework

  import remoteFramework._
  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: AsRealRPC[ClientRPCType]

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def fire(rpcName: String, argLists: List[List[RawValue]]): Unit =
      fireRemote(getterChain, RawInvocation(rpcName, argLists))

    def get(rpcName: String, argLists: List[List[RawValue]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }
}
