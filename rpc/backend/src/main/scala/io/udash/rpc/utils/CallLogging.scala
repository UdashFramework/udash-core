package io.udash.rpc.utils

import com.avsystem.commons.rpc.RPCMetadata
import io.udash.rpc.ExposesServerRPC

import scala.concurrent.Future

/**
  * ExposesServerRPC mixin simplifying RPC calls logging.
  */
trait CallLogging[ServerRPCType] extends ExposesServerRPC[ServerRPCType] {

  protected val metadata: RPCMetadata[ServerRPCType]

  def log(rpcName: String, methodName: String, args: Seq[String]): Unit

  override def handleRpcCall(call: localFramework.RPCCall): Future[localFramework.RawValue] = {
    val classMetadata = call
      .gettersChain.reverse
      .foldLeft[RPCMetadata[_]](metadata)((metadata, invocation) => metadata.getterResults(invocation.rpcName))

    classMetadata
      .signatures
      .get(call.invocation.rpcName)
      .filter(_.annotations.exists(_.isInstanceOf[Logged]))
      .foreach(methodMetadata =>
        log(classMetadata.name, methodMetadata.methodName, call.invocation.argLists.flatMap(_.map(localFramework.rawToString)))
      )
    super.handleRpcCall(call)
  }

}