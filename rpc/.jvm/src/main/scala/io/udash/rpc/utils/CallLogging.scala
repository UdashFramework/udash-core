package io.udash.rpc.utils

import io.udash.rpc._

import scala.concurrent.Future

/**
  * ExposesServerRPC mixin simplifying RPC calls logging.
  */
trait CallLogging[ServerRPCType] extends ExposesServerRPC[ServerRPCType] {

  protected val metadata: ServerRpcMetadata[ServerRPCType]

  def log(rpcName: String, methodName: String, args: Seq[String]): Unit

  private def handleRpcRequest(msg: RpcRequest): Unit = {
    val classMetadata =
      msg.gettersChain.reverse.foldLeft[ServerRpcMetadata[_]](metadata) {
        (metadata, invocation) => metadata.getters(invocation.rpcName).resultMetadata.value
      }

    classMetadata.loggedMethods
      .get(msg.invocation.rpcName)
      .foreach { methodMetadata =>
        log(classMetadata.name, methodMetadata.name, msg.invocation.args.map(_.json))
      }
  }

  override def handleRpcFire(fire: RpcFire): Unit = {
    handleRpcRequest(fire)
    super.handleRpcFire(fire)
  }

  override def handleRpcCall(call: RpcCall): Future[JsonStr] = {
    handleRpcRequest(call)
    super.handleRpcCall(call)
  }
}
