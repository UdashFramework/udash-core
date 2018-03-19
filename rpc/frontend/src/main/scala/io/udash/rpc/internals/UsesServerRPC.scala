package io.udash.rpc.internals

import com.avsystem.commons.SharedExtensions._
import io.udash.rpc._
import io.udash.utils.{CallbacksHandler, Registration}
import org.scalajs.dom

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.Dictionary

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
  lazy val remoteRpc: ServerRPCType = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: AsRealRPC[ServerRPCType]

  protected val connector: ServerConnector[RPCRequest]

  protected val callTimeout: Duration = 30 seconds
  private val pendingCalls: Dictionary[(RPCRequest, Promise[RawValue])] = js.Dictionary.empty
  private val exceptionCallbacks = new CallbacksHandler[Throwable]

  private var cid: Int = 0
  private def newCallId(): String = {
    cid += 1
    cid.toString
  }

  /**
    * Registers callback which will be called whenever RPC request returns failure.
    *
    * The callbacks are executed in order of registration. Registration operations don't preserve callbacks order.
    * Each callback is executed once, exceptions thrown in callbacks are swallowed.
    */
  def onCallFailure(callback: exceptionCallbacks.CallbackType): Registration =
    exceptionCallbacks.register(callback)

  private def handleException(ex: Throwable): Unit =
    exceptionCallbacks.fire(ex)

  def handleResponse(response: RPCResponse): Unit = {
    pendingCalls.remove(response.callId)
      .foreach { case (_, promise) =>
        response match {
          case RPCResponseSuccess(r, _) =>
            promise.success(r)
          case RPCResponseException(_, exception, _) =>
            handleException(exception)
            promise.failure(exception)
          case RPCResponseFailure(cause, error, _) =>
            val exception = RPCFailure(cause, error)
            handleException(exception)
            promise.failure(exception)
        }
      }
    }

  override protected[rpc] def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit =
    sendRPCRequest(RPCFire(invocation, getterChain))

  protected[rpc] def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): RPCCall =
    RPCCall(invocation, getterChain, callId).setup(sendRPCRequest)

  private def sendRPCRequest(request: RPCRequest) =
    connector.sendRPCRequest(request)

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def fire(rpcName: String, argLists: List[List[RawValue]]): Unit =
      fireRemote(getterChain, RawInvocation(rpcName, argLists))

    def call(rpcName: String, argLists: List[List[RawValue]]): Future[RawValue] = {
      val callId = newCallId()
      Promise[RawValue]().setup { promise =>
        val request = callRemote(callId, getterChain, RawInvocation(rpcName, argLists))
        pendingCalls.put(callId, (request, promise))
        dom.window.setTimeout(
          () => handleResponse(RPCResponseException("Request timeout", UsesServerRPC.CallTimeout(callTimeout), callId)),
          callTimeout.toMillis
        )
      }.future
    }

    def get(rpcName: String, argLists: List[List[RawValue]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }
}

object UsesServerRPC {
  case class CallTimeout(callTimeout: Duration) extends RuntimeException(s"Response missing after $callTimeout.")
}
