package io.udash.rpc.internals

import io.udash.rpc._
import upickle.Js

import scala.collection.mutable
import scala.concurrent.Promise
import scala.scalajs.js

/**
 * Base trait for client-side components which use some RPC exposed by server-side.
 */
trait UsesServerRPC[ServerRPCType <: RPC] extends UsesRemoteRPC[ServerRPCType] {
  protected val connector: ServerConnector

  private[rpc] def returnServerResult(callId: String, value: Js.Value) =
    returnRemoteResult(callId, value)

  private[rpc] def reportServerFailure(callId: String, cause: String, message: String) =
    reportRemoteFailure(callId, cause, message)

  // overridden to use JS object instead of mutable.HashMap
  override protected[rpc] final def createPendingCallsRegistry: mutable.Map[String, Promise[Js.Value]] =
    new js.Object().asInstanceOf[js.Dictionary[Promise[Js.Value]]]

  protected[rpc] def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    sendRPCRequest(RPCFire(invocation, getterChain))

  protected[rpc] def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit = {
    sendRPCRequest(RPCCall(invocation, getterChain, callId))
  }

  private def sendRPCRequest(request: RPCRequest) = {
    connector.sendRPCRequest(request)
  }

  def handleResponse(response: RPCResponse) = {
    response match {
      case RPCResponseSuccess(r, callId) =>
        returnServerResult(callId, r)
      case RPCResponseFailure(cause, error, callId) =>
        reportServerFailure(callId, cause, error)
    }
  }
}
