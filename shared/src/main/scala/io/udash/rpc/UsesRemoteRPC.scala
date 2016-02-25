package io.udash.rpc

import upickle.Js

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

/**
 * Base trait for anything that uses remote RPC interface.
 */
trait UsesRemoteRPC[T <: RPC] extends HasExecutionContext {
  /**
   * Proxy for remote RPC implementation. Use this to perform RPC calls.
   */
  val remoteRpc = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
   * This allows for generation of proxy which translates RPC calls into raw calls that
   * can be sent through the network.
   */
  protected def remoteRpcAsReal: AsRealRPC[T]

  protected def createPendingCallsRegistry: mutable.Map[String, Promise[Js.Value]] =
    new mutable.HashMap

  /**
   * Sends the raw RPC invocation of method returning `Unit` through network.
   */
  protected def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit

  /**
   * Sends the raw RPC invocation of method returning `Future` through network.
   */
  protected def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit

  protected def returnRemoteResult(callId: String, value: Js.Value): Unit =
    pendingCalls.remove(callId).foreach(_.success(value))

  protected def reportRemoteFailure(callId: String, cause: String, message: String): Unit =
    pendingCalls.remove(callId).foreach(_.failure(RPCFailure(cause, message)))

  private var cid: Int = 0

  private def newCallId() = {
    cid += 1
    cid.toString
  }

  private val pendingCalls = createPendingCallsRegistry

  private class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def fire(rpcName: String, argLists: List[List[Js.Value]]): Unit =
      fireRemote(getterChain, RawInvocation(rpcName, argLists))

    def call(rpcName: String, argLists: List[List[Js.Value]]): Future[Js.Value] = {
      val callId = newCallId()
      val promise = Promise[Js.Value]()
      pendingCalls.put(callId, promise)
      callRemote(callId, getterChain, RawInvocation(rpcName, argLists))
      promise.future
    }

    def get(rpcName: String, argLists: List[List[Js.Value]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }

}
