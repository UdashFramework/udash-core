package io.udash.rpc

import com.avsystem.commons.SharedExtensions._
import io.udash.logging.CrossLogging
import io.udash.rpc.utils.{CallTimeout, TimeoutConfig}
import io.udash.utils.{CallbacksHandler, CrossCollections, Registration}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

/** Base trait for anything that uses remote RPC interface. */
trait UsesRemoteRpc[RpcApi] extends CrossLogging {
  /** Callbacks fired on failed RPC call. */
  final private val exceptionCallbacks = new CallbacksHandler[Throwable]

  /** This map contains promises of the call responses. */
  final private val pendingCalls: mutable.Map[String, Promise[JsonStr]] = CrossCollections.createDictionary

  /** Messages queued due to the connection problems. These are expected to be resend. */
  final private val queuedMessages: mutable.Buffer[(RpcMessage, Int)] = CrossCollections.createArray

  /**
    * Sends RPCRequest to the other node. If the transmission failed,
    * the implementation should `enqueueMessage` to be resend.
    */
  protected def sendRpcMessage(request: RpcMessage, retryNumber: Int = 0): Unit

  /** Returns new, unique call id. The id is used to match request/response pairs. */
  protected def newCallId(): String

  /** Configuration of messages send/recv timeouts. */
  protected def timeouts: TimeoutConfig

  /** This method should schedule execution of `callback`. */
  protected def timeoutCallback(callback: () => Unit, timeout: FiniteDuration): Unit

  protected def enqueueMessage(message: RpcMessage, retryNumber: Int): Unit = {
    if (retryNumber < timeouts.sendRetriesLimit) {
      queuedMessages.synchronized {
        queuedMessages.append((message, retryNumber + 1))
      }
      scheduleQueuedMessages()
    } else {
      logger.warn(s"Message $message dropped after $retryNumber retries.")
    }
  }

  protected def retryQueuedMessages(): Unit = {
    queuedMessages.synchronized {
      val cpy = CrossCollections.copyArray(queuedMessages)
      queuedMessages.clear()
      cpy
    }.foreach { case (msg, retryNumber) => sendRpcMessage(msg, retryNumber) }
  }

  private var isQueueScheduled: Boolean = false
  protected def scheduleQueuedMessages(): Unit = {
    isQueueScheduled.synchronized {
      if (!isQueueScheduled) {
        timeoutCallback(
          () => {
            isQueueScheduled.synchronized {
              isQueueScheduled = false
              retryQueuedMessages()
            }
          },
          timeouts.sendRetryTimeout
        )
        isQueueScheduled = true
      }
    }
  }

  /** Sends the raw RPC fire invocation of method returning `Unit` through network. */
  protected def fireRemote(getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit =
    sendRpcMessage(RpcFire(invocation, getterChain))

  /** Sends the raw RPC call invocation of method returning `Future[JsonStr]` through network. */
  protected def callRemote(callId: String, getterChain: List[RpcInvocation], invocation: RpcInvocation): Unit =
    sendRpcMessage(RpcCall(invocation, getterChain, callId))

  /**
    * Registers callback which will be called whenever RPC request returns failure.
    *
    * The callbacks are executed in order of registration. Registration operations don't preserve callbacks order.
    * Each callback is executed once, exceptions thrown in callbacks are swallowed.
    */
  def onCallFailure(callback: exceptionCallbacks.CallbackType): Registration =
    exceptionCallbacks.register(callback)

  /** Handles response from the remote. */
  protected def handleResponse(response: RpcResponse): Unit = {
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

  private def handleException(ex: Throwable): Unit =
    exceptionCallbacks.fire(ex)

  protected class RawRemoteRPC(getterChain: List[RpcInvocation]) extends RawRpc {
    override def call(invocation: RpcInvocation): Future[JsonStr] =
      Promise[JsonStr]().setup { promise =>
        val callId = newCallId()
        callRemote(callId, getterChain, invocation)
        pendingCalls.put(callId, promise)
        timeoutCallback(
          () => handleResponse(RpcResponseException("Request timeout", CallTimeout(timeouts.callResponseTimeout), callId)),
          timeouts.callResponseTimeout
        )
      }.future

    override def fire(invocation: RpcInvocation): Unit =
      fireRemote(getterChain, invocation)

    override def get(invocation: RpcInvocation): RawRpc =
      new RawRemoteRPC(invocation :: getterChain)
  }
}