package io.udash.rpc

trait RPCShared {
  type RPCRequest         = io.udash.rpc.internals.RPCRequest
  val  RPCCall            = io.udash.rpc.internals.RPCCall
  val  RPCFire            = io.udash.rpc.internals.RPCFire

  type RPCResponse        = io.udash.rpc.internals.RPCResponse
  val  RPCResponseSuccess = io.udash.rpc.internals.RPCResponseSuccess
  val  RPCResponseFailure = io.udash.rpc.internals.RPCResponseFailure
}
