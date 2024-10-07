package io.udash.rpc

import com.avsystem.commons.rpc.rpcName
import com.avsystem.commons.serialization.{GenCodec, HasGenCodec}
import io.udash.rpc.utils.Logged

import scala.annotation.nowarn
import scala.concurrent.Future

case class Record(i: Int, fuu: String)
object Record extends HasGenCodec[Record]

case class CustomRPCException(i: Int) extends Throwable
object CustomRPCException extends HasGenCodec[CustomRPCException]

trait RPCMethods {
  @nowarn
  def handle: Unit

  def handleMore(): Unit

  @rpcName("doStuffBase")
  def doStuff(lol: Int, fuu: String)(cos: Option[Boolean]): Unit

  @rpcName("doStuffInteger")
  def doStuff(num: Int): Unit

  def takeCC(r: Record): Unit

  def srslyDude(): Unit
}

/** Inner Server side RPC interface */
trait InnerRPC {
  def proc(): Unit

  @Logged
  def func(arg: Int): Future[String]

  def recInner(arg: String): InnerRPC
}
object InnerRPC extends DefaultServerRpcCompanion[InnerRPC]

/** Inner Client RPC interface */
trait InnerClientRPC {
  def proc(): Unit
}
object InnerClientRPC extends DefaultClientRpcCompanion[InnerClientRPC]

case class External(x: Int)

object ExternalTypeCodec {
  implicit val codec: GenCodec[External] = GenCodec.transformed[External, Int](_.x, External.apply)
}

/** Main Server side RPC interface */
trait TestRPC extends RPCMethods {
  def doStuff(yes: Boolean): Future[String]

  def doStuffWithFail(no: Boolean): Future[String]

  def doStuffWithEx(): Future[String]

  def doStuffInt(yes: Boolean): Future[Int]

  def doStuffUnit(): Future[Unit]

  def doStuffExternal(external: External): Future[External]

  @Logged
  def fireSomething(arg: Int): Unit

  def innerRpc(name: String): InnerRPC

  def throwingGetter(): InnerRPC

  def nullGetter(): InnerRPC
}

/** Main Client RPC interface */
trait TestClientRPC extends RPCMethods {
  def innerRpc(name: String): InnerClientRPC
}

/** Basic RPC methods implementation with callbacks support */
trait RPCMethodsImpl extends RPCMethods {
  def onInvocationInternal: (String, List[Any], Option[Any]) => Any

  protected def onFire(methodName: String, args: List[Any]): Unit =
    onInvocationInternal(methodName, args, None)

  protected def onCall[T](methodName: String, args: List[Any], result: T): Future[T] = {
    onInvocationInternal(methodName, args, Some(result))
    Future.successful(result)
  }

  protected def onFailingCall[T](methodName: String, args: List[Any], result: Throwable): Future[T] = {
    onInvocationInternal(methodName, args, Some(result))
    Future.failed(result)
  }

  protected def onGet[T](methodName: String, args: List[Any], result: T): T = {
    onInvocationInternal(methodName, args, None)
    result
  }

  override def handleMore(): Unit =
    onFire("handleMore", List(Nil))

  override def doStuff(lol: Int, fuu: String)(cos: Option[Boolean]): Unit =
    onFire("doStuff", List(List[Any](lol, fuu), List(cos)))

  override def doStuff(num: Int): Unit =
    onFire("doStuffInt", List(List(num)))

  def doStuff(yes: Boolean): Future[String] =
    onCall("doStuff", List(List(yes)), "doStuffResult")

  def doStuffWithFail(no: Boolean): Future[String] =
    onFailingCall("doStuffWithFail", List(List(no)), new Exception)

  @nowarn
  override def handle: Unit =
    onFire("handle", Nil)

  override def takeCC(r: Record): Unit =
    onFire("recordCC", List(List(r)))

  override def srslyDude(): Unit =
    onFire("srslyDude", List(Nil))
}

@nowarn
object TestRPC extends DefaultServerRpcCompanionWithDeps[ExternalTypeCodec.type, TestRPC] {
  /** Returns implementation of server side RPC interface */
  def rpcImpl(onInvocation: (String, List[Any], Option[Any]) => Any): TestRPC =
    new TestRPC with RPCMethodsImpl {
      override def doStuff(yes: Boolean): Future[String] =
        onCall("doStuff", List(List(yes)), "doStuffResult")

      override def doStuffWithFail(no: Boolean): Future[String] =
        onFailingCall("doStuffWithFail", List(List(no)), new Exception)

      override def doStuffWithEx(): Future[String] =
        onFailingCall("doStuffWithEx", List(List()), CustomRPCException(5))

      override def doStuffInt(yes: Boolean): Future[Int] =
        onCall("doStuffInt", List(List(yes)), 5)

      override def doStuffUnit(): Future[Unit] =
        onCall("doStuffUnit", List(Nil), ())

      override def doStuffExternal(external: External): Future[External] =
        onCall("doStuffExternal", List(Nil), external)

      override def fireSomething(arg: Int): Unit =
        onFire("fireSomething", List(List(arg)))

      override def onInvocationInternal: (String, List[Any], Option[Any]) => Any = onInvocation

      override def innerRpc(name: String): InnerRPC = {
        onInvocationInternal("innerRpc", List(List(name)), None)
        new InnerRPC {
          def func(arg: Int): Future[String] =
            onCall("innerRpc.func", List(List(arg)), "innerRpc.funcResult")

          def proc(): Unit =
            onFire("innerRpc.proc", List(Nil))

          def recInner(arg: String): InnerRPC =
            this
        }
      }

      override def throwingGetter(): InnerRPC = {
        onInvocationInternal("throwingGetter", List(List()), None)
        throw new NullPointerException
      }

      override def nullGetter(): InnerRPC = {
        onInvocationInternal("nullGetter", List(List()), None)
        null
      }
    }
}

@nowarn
object TestClientRPC extends DefaultClientRpcCompanion[TestClientRPC] {
  /** Returns implementation of client side RPC interface */
  def rpcImpl(onInvocation: (String, List[Any], Option[Any]) => Any): TestClientRPC =
    new TestClientRPC with RPCMethodsImpl {
      override def onInvocationInternal: (String, List[Any], Option[Any]) => Any = onInvocation

      override def innerRpc(name: String): InnerClientRPC = {
        onInvocationInternal("innerRpc", List(List(name)), None)
        () => onFire("innerRpc.proc", List(Nil))
      }
    }
}
