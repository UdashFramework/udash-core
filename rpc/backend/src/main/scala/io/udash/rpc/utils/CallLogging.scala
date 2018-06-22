package io.udash.rpc.utils

import io.udash.rpc.ExposesServerRPC

import scala.concurrent.Future

/**
  * ExposesServerRPC mixin simplifying RPC calls logging.
  */
trait CallLogging[ServerRPCType] extends ExposesServerRPC[ServerRPCType] {

  import localFramework.RPCMetadata

  protected val metadata: RPCMetadata[ServerRPCType]

  def log(rpcName: String, methodName: String, args: Seq[String]): Unit

  private def handleRpcRequest(msg: localFramework.RPCRequest): Unit = {
    val classMetadata = msg
      .gettersChain.reverse
      .foldLeft[RPCMetadata[_]](metadata)((metadata, invocation) =>
      metadata.getterSignatures(invocation.rpcName).resultMetadata.value)

    classMetadata
      .functionSignatures
      .get(msg.invocation.rpcName)
      .filter(_.annotations.collectFirst({ case a: Logged => a }).nonEmpty)
      .foreach(methodMetadata =>
        log(classMetadata.name, methodMetadata.name, msg.invocation.args.map(_.json))
      )
  }

  override def handleRpcFire(fire: localFramework.RPCFire): Unit = {
    handleRpcRequest(fire)
    super.handleRpcFire(fire)
  }

  override def handleRpcCall(call: localFramework.RPCCall): Future[localFramework.RawValue] = {
    handleRpcRequest(call)
    super.handleRpcCall(call)
  }
}
