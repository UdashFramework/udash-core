package io.udash.rpc

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

/**
 * Base trait for anything that uses remote RPC interface.
 */
trait UsesRemoteRPC[T] {
  val framework: UdashRPCFramework

  import framework._

  protected def createPendingCallsRegistry: mutable.Map[String, Promise[RawValue]] =
    new mutable.HashMap

  /**
   * Sends the raw RPC invocation of method returning `Unit` through network.
   */
  protected def fireRemote(getterChain: List[RawInvocation], invocation: RawInvocation): Unit

  /**
   * Sends the raw RPC invocation of method returning `Future` through network.
   */
  protected def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit

  protected def returnRemoteResult(callId: String, value: RawValue): Unit =
    pendingCalls.remove(callId).foreach(_.success(value))

  protected def reportRemoteFailure(callId: String, cause: String, message: String): Unit =
    pendingCalls.remove(callId).foreach(_.failure(RPCFailure(cause, message)))

  private var cid: Int = 0

  private def newCallId() = {
    cid += 1
    cid.toString
  }

  private val pendingCalls = createPendingCallsRegistry

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def fire(rpcName: String, argLists: List[List[RawValue]]): Unit =
      fireRemote(getterChain, RawInvocation(rpcName, argLists))

    def call(rpcName: String, argLists: List[List[RawValue]]): Future[RawValue] = {
      val callId = newCallId()
      val promise = Promise[RawValue]()
      pendingCalls.put(callId, promise)
      callRemote(callId, getterChain, RawInvocation(rpcName, argLists))
      promise.future
    }

    def get(rpcName: String, argLists: List[List[RawValue]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }

}
