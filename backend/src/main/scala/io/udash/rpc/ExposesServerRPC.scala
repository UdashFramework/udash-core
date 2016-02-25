package io.udash.rpc

import io.udash.rpc.internals._
import upickle.Js

import scala.concurrent.{ExecutionContext, Future}

final class ExposesServerRPC[ServerRPCType <: RPC](local: ServerRPCType)(implicit protected val localRpcAsRaw: AsRawRPC[ServerRPCType])
  extends ExposesLocalRPC[ServerRPCType] {

  override protected def localRpc: ServerRPCType = local

  implicit val executionContext: ExecutionContext = internalRPCExecutionContext

  /** Handles RPCCall and returns Future with call result. */
  def handleRpcCall(call: RPCCall): Future[Js.Value] = {
    val receiver = localRpcAsRaw.asRaw(localRpc).resolveGetterChain(call.gettersChain)
    receiver.call(call.invocation.rpcName, call.invocation.argLists)
  }

  /** Handles RPCFire */
  def handleRpcFire(fire: RPCFire): Unit = {
    val receiver = localRpcAsRaw.asRaw(localRpc).resolveGetterChain(fire.gettersChain)
    receiver.fire(fire.invocation.rpcName, fire.invocation.argLists)
  }
}
