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

  override def handleRpcCall(call: localFramework.RPCCall): Future[localFramework.RawValue] = {
    val classMetadata = call
      .gettersChain.reverse
      .foldLeft[RPCMetadata[_]](metadata)((metadata, invocation) =>
      metadata.getterSignatures(invocation.rpcName).resultMetadata.value)

    classMetadata
      .functionSignatures
      .get(call.invocation.rpcName)
      .filter(_.annotations.collectFirst({ case a: Logged => a }).nonEmpty)
      .foreach(methodMetadata =>
        log(classMetadata.name, methodMetadata.name, call.invocation.args.map(_.json))
      )
    super.handleRpcCall(call)
  }
}
