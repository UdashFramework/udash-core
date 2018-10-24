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
  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc: ServerRPCType = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: ServerRawRpc.AsRealRpc[ServerRPCType]

  protected val connector: ServerConnector

  protected def callTimeout: Duration = 30 seconds
  private val pendingCalls: Dictionary[Promise[JsonStr]] = js.Dictionary.empty
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

  def handleResponse(response: RpcResponse): Unit = {
    pendingCalls.remove(response.callId)
      .foreach { promise =>
        response match {
          case RpcResponseSuccess(r, _) =>
            promise.success(r)
          case RpcResponseException(_, exception, _) =>
            handleException(exception)
            promise.failure(exception)
          case RpcResponseFailure(cause, error, _) =>
            val exception = RpcFailure(cause, error)
            handleException(exception)
            promise.failure(exception)
        }
      }
  }

  override protected[rpc] def fireRemote(getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit =
    sendRpcRequest(RpcFire(invocation, getterChain))

  protected[rpc] def callRemote(callId: String, getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit =
    sendRpcRequest(RpcCall(invocation, getterChain, callId))

  private def sendRpcRequest(request: RpcRequest): Unit =
    connector.sendRpcRequest(request)

  protected class RawRemoteRPC(getterChain: List[RpcInvocation]) extends ServerRawRpc {
    def fire(invocation: RpcInvocation): Unit =
      fireRemote(getterChain, invocation)

    def call(invocation: RpcInvocation): Future[JsonStr] =
      Promise[JsonStr]().setup { promise =>
        val callId = newCallId()
        callRemote(callId, getterChain, invocation)
        pendingCalls.put(callId, promise)
        dom.window.setTimeout(
          () => handleResponse(RpcResponseException("Request timeout", UsesServerRPC.CallTimeout(callTimeout), callId)),
          callTimeout.toMillis
        )
      }.future

    def get(invocation: RpcInvocation): ServerRawRpc =
      new RawRemoteRPC(invocation :: getterChain)
  }
}

object UsesServerRPC {
  case class CallTimeout(callTimeout: Duration) extends RuntimeException(s"Response missing after $callTimeout.")
}
