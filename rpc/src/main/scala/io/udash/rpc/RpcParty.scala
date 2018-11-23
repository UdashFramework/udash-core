package io.udash
package rpc

import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.serialization.json.JsonStringInput
import io.udash.logging.CrossLogging
import io.udash.rpc.serialization.ExceptionCodecRegistry

import scala.util.{Failure, Success, Try}

trait RpcParty[LocalRpcApi, RemoteRpcApi] extends ExposesLocalRpc[LocalRpcApi] with UsesRemoteRpc[RemoteRpcApi] with CrossLogging {
  protected def rpcMessageHandler(implicit exceptionsRegistry: ExceptionCodecRegistry): PartialFunction[Try[RpcMessage], Unit] = {
    case Success(call: RpcCall) =>
      handleRpcCall(call).onCompleteNow {
        case Success(response) =>
          sendRpcMessage(RpcResponseSuccess(response, call.callId))
        case Failure(ex) =>
          val response = exceptionsRegistry.name(ex) match {
            case Some(exceptionName) =>
              RpcResponseException(exceptionName, ex, call.callId)
            case None =>
              val cause: String = if (ex.getCause != null) ex.getCause.getMessage else ex.getClass.getName
              RpcResponseFailure(cause, Option(ex.getMessage).getOrElse(""), call.callId)
          }
          sendRpcMessage(response)
      }
    case Success(fire: RpcFire) =>
      handleRpcFire(fire)
    case Success(callResponse: RpcResponse) =>
      handleResponse(callResponse)
    case Failure(ex) =>
      logger.warn(s"Cannot handle an rpc message.", ex)
  }

  protected def handleMessage(message: String)(implicit exceptionsRegistry: ExceptionCodecRegistry): Unit = {
    rpcMessageHandler.apply(Try(JsonStringInput.read[RpcMessage](message)))
  }
}