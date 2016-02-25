package io.udash.rpc

import upickle.Js

import scala.concurrent.Future

/**
 * Author: ghik
 * Created: 27/05/15.
 */
trait RawRPC {
  protected def fail(rpcTpe: String, methodName: String, args: List[List[Js.Value]]) = {
    val argsRepr = args.mkString("[", ", ", "]")
    throw new Exception(s"Don't know how to invoke method named $methodName with arguments $argsRepr on $rpcTpe")
  }

  def fire(rpcName: String, argLists: List[List[Js.Value]]): Unit

  def call(rpcName: String, argLists: List[List[Js.Value]]): Future[Js.Value]

  def get(rpcName: String, argLists: List[List[Js.Value]]): RawRPC

  def resolveGetterChain(getterChain: List[RawInvocation]): RawRPC =
    getterChain.foldRight(this) {
      case (RawInvocation(rpcName, argLists), rawRpc) => rawRpc.get(rpcName, argLists)
    }
}

trait AsRawRPC[T <: RPC] {
  def asRaw(rpcImpl: T): RawRPC
}

object AsRawRPC {
  /**
   * Materializes a factory of implementations of [[RawRPC]] which translate invocations of its `call` and `fire` methods
   * to invocations of actual methods on `rpcImpl`. Method arguments and results are serialized and deserialized
   * from/to JSON using `uPickle` library.
   *
   * Only calls to non-generic, abstract methods returning `Unit` or `Future` are supported.
   */
  implicit def materialize[T <: RPC]: AsRawRPC[T] = macro io.udash.macros.RPCMacros.asRawImpl[T]

  def apply[T <: RPC](implicit asRawRPC: AsRawRPC[T]): AsRawRPC[T] = asRawRPC
}

trait AsRealRPC[T <: RPC] {
  def asReal(rawRpc: RawRPC): T
}

object AsRealRPC {
  /**
   * Materializes a factory of implementations of `T` which are proxies that implement all abstract methods of `T`
   * by forwarding them to `rawRpc`. Method arguments and results are serialized and deserialized
   * from/to JSON using `uPickle` library.
   *
   * All abstract methods of `T` must be non-generic and return `Unit` or `Future`.
   */
  implicit def materialize[T <: RPC]: AsRealRPC[T] = macro io.udash.macros.RPCMacros.asRealImpl[T]

  def apply[T <: RPC](implicit asRealRPC: AsRealRPC[T]): AsRealRPC[T] = asRealRPC
}
