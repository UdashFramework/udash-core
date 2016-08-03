package io.udash.rpc.internals

import io.udash.rpc._
import org.scalajs.dom

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.scalajs.js

/**
 * Base trait for client-side components which use some RPC exposed by server-side.
 */
private[rpc] trait UsesServerRPC[ServerRPCType] extends UsesRemoteRPC[ServerRPCType] {
  override val localFramework: ClientUdashRPCFramework
  override val remoteFramework: ServerUdashRPCFramework

  import remoteFramework._
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

  protected val callTimeout: Duration = 30 seconds
  private val pendingCalls = new js.Object().asInstanceOf[js.Dictionary[Promise[RawValue]]]
  private var cid: Int = 0

  private def newCallId() = {
    cid += 1
    cid.toString
  }

  def handleResponse(response: RPCResponse) = {
    response match {
      case RPCResponseSuccess(r, callId) =>
        pendingCalls.remove(callId).foreach(_.success(r))
      case RPCResponseFailure(cause, error, callId) =>
        pendingCalls.remove(callId).foreach(_.failure(RPCFailure(cause, error)))
    }
  }

  override protected[rpc] def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    sendRPCRequest(RPCFire(invocation, getterChain))

  protected[rpc] def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    sendRPCRequest(RPCCall(invocation, getterChain, callId))

  private def sendRPCRequest(request: RPCRequest) =
    connector.sendRPCRequest(request)

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def fire(rpcName: String, argLists: List[List[RawValue]]): Unit =
      fireRemote(getterChain, RawInvocation(rpcName, argLists))

    def call(rpcName: String, argLists: List[List[RawValue]]): Future[RawValue] = {
      val callId = newCallId()
      val promise = Promise[RawValue]()
      pendingCalls.put(callId, promise)
      dom.window.setTimeout(() => {
        pendingCalls.remove(callId)
          .foreach(_.failure(RPCFailure("Request timeout", s"Response missing after $callTimeout.")))
      }, callTimeout.toMillis)
      callRemote(callId, getterChain, RawInvocation(rpcName, argLists))
      promise.future
    }

    def get(rpcName: String, argLists: List[List[RawValue]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }
}
