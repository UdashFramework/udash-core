package io.udash.rpc.internals

import io.udash.rpc.{ClientRPC, RawInvocation, UsesRemoteRPC}

/**
 * Base trait for server-side components which use some RPC exposed by client-side.
 */
private[rpc] trait UsesClientRPC[ClientRPCType <: ClientRPC] extends UsesRemoteRPC[ClientRPCType] {
  protected def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    throw new IllegalStateException("You can not broadcast remote call!")
}
