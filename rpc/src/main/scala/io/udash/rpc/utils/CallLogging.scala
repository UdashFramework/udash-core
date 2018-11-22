package io.udash
package rpc.utils

import io.udash.rpc._

import scala.concurrent.Future

/**
  * ExposesServerRPC mixin simplifying RPC calls logging.
  */
trait CallLogging[LocalRpcApi, RemoteRpcApi] extends ExposesLocalRpc[LocalRpcApi] {
  import CallLogging.CallLog

  protected val metadata: RpcMetadata[LocalRpcApi]

  def log(callLog: CallLog): Unit

  private def handleRpcRequest(msg: RpcRequest): Unit = {
    val classMetadata =
      msg.gettersChain.reverse.foldLeft[RpcMetadata[_]](metadata) {
        (metadata, invocation) => metadata.getters(invocation.rpcName).resultMetadata.value
      }

    classMetadata.loggedMethods
      .get(msg.invocation.rpcName)
      .foreach { methodMetadata =>
        log(CallLog(classMetadata.name, methodMetadata.name, msg.invocation.args.map(_.json)))
      }
  }

  override protected def handleRpcFire(fire: RpcFire): Unit = {
    handleRpcRequest(fire)
    super.handleRpcFire(fire)
  }

  override protected def handleRpcCall(call: RpcCall): Future[JsonStr] = {
    handleRpcRequest(call)
    super.handleRpcCall(call)
  }
}

object CallLogging {
  case class CallLog(rpcName: String, methodName: String, args: Seq[String])
}