package io.udash.rpc.internals

import io.udash.rpc._

import scala.collection.mutable
import scala.concurrent.Promise
import scala.scalajs.js

/**
 * Base trait for client-side components which use some RPC exposed by server-side.
 */
private[rpc] trait UsesServerRPC[ServerRPCType] extends UsesRemoteRPC[ServerRPCType] {
  import framework._

  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: AsRealRPC[ServerRPCType]

  protected val connector: ServerConnector[RPCRequest]

  private[rpc] def returnServerResult(callId: String, value: RawValue) =
    returnRemoteResult(callId, value)

  private[rpc] def reportServerFailure(callId: String, cause: String, message: String) =
    reportRemoteFailure(callId, cause, message)

  // overridden to use JS object instead of mutable.HashMap
  override protected[rpc] final def createPendingCallsRegistry: mutable.Map[String, Promise[RawValue]] =
    new js.Object().asInstanceOf[js.Dictionary[Promise[RawValue]]]

  protected[rpc] def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    sendRPCRequest(RPCFire(invocation, getterChain))

  protected[rpc] def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit = {
    sendRPCRequest(RPCCall(invocation, getterChain, callId))
  }

  private def sendRPCRequest(request: RPCRequest) =
    connector.sendRPCRequest(request)

  def handleResponse(response: RPCResponse) = {
    response match {
      case RPCResponseSuccess(r, callId) =>
        returnServerResult(callId, r)
      case RPCResponseFailure(cause, error, callId) =>
        reportServerFailure(callId, cause, error)
    }
  }
}
