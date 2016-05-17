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

  override def handleRpcCall(call: framework.RPCCall): Future[framework.RawValue] = {
    val classMetadata = call
      .gettersChain.reverse
      .foldLeft[RPCMetadata[_]](metadata)((metadata, invocation) => metadata.getterResultMetadata(invocation.rpcName))

    classMetadata
      .methodsByRpcName
      .get(call.invocation.rpcName)
      .filter(_.signature.annotations.exists(_.isInstanceOf[Logged]))
      .foreach(methodMetadata =>
        log(classMetadata.name, methodMetadata.signature.methodName, call.invocation.argLists.flatMap(_.map(framework.rawToString)))
      )
    super.handleRpcCall(call)
  }

}