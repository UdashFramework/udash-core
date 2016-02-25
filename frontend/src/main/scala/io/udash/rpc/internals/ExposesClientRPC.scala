package io.udash.rpc.internals

import io.udash.rpc._

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

class ExposesClientRPC[ClientRPCType <: ClientRPC](protected val localRpc: ClientRPCType)(implicit protected val localRpcAsRaw: AsRawRPC[ClientRPCType]) extends ExposesLocalRPC[ClientRPCType] {

  /** Handles RPCFires */
  def handleRpcFire(fire: RPCFire): Unit = {
    val receiver = localRpcAsRaw.asRaw(localRpc).resolveGetterChain(fire.gettersChain)
    receiver.fire(fire.invocation.rpcName, fire.invocation.argLists)
  }

  override protected implicit def executionContext: ExecutionContext = JSExecutionContext.queue
}
