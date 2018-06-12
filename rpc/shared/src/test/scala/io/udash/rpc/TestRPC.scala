package io.udash.rpc

import com.avsystem.commons.serialization.HasGenCodec
import com.github.ghik.silencer.silent
import io.udash.rpc.utils.Logged

import scala.concurrent.Future

case class Record(i: Int, fuu: String)
object Record extends HasGenCodec[Record]

case class CustomRPCException(i: Int) extends Throwable
object CustomRPCException extends HasGenCodec[CustomRPCException]

trait RPCMethods {
  @silent
  def handle: Unit

  def handleMore(): Unit

  @RPCName("doStuffBase")
  def doStuff(lol: Int, fuu: String)(cos: Option[Boolean]): Unit

  @RPCName("doStuffInteger")
  def doStuff(num: Int): Unit

  def takeCC(r: Record): Unit

  def srslyDude(): Unit
}

/** Inner Server side RPC interface */
@RPC
trait InnerRPC {
  def proc(): Unit

  @Logged
  def func(arg: Int): Future[String]
}

/** Inner Client RPC interface */
@RPC
trait InnerClientRPC {
  def proc(): Unit
}

/** Main Server side RPC interface */
@RPC
trait TestRPC extends RPCMethods {
  def doStuff(yes: Boolean): Future[String]

  def doStuffWithFail(no: Boolean): Future[String]

  def doStuffWithEx(): Future[String]

  def doStuffInt(yes: Boolean): Future[Int]

  def doStuffUnit(): Future[Unit]

  @Logged
  def fireSomething(arg: Int): Unit

  def innerRpc(name: String): InnerRPC

  def throwingGetter(): InnerRPC

  def nullGetter(): InnerRPC
}

/** Main Client RPC interface */
@RPC
trait TestClientRPC extends RPCMethods {
  def innerRpc(name: String): InnerClientRPC
}

/** Basic RPC methods implementation with callbacks support */
trait RPCMethodsImpl extends RPCMethods {
  def onInvocationInternal: (String, List[List[Any]], Option[Any]) => Any

  protected def onFire(methodName: String, args: List[List[Any]]): Unit =
    onInvocationInternal(methodName, args, None)

  protected def onCall[T](methodName: String, args: List[List[Any]], result: T): Future[T] = {
    onInvocationInternal(methodName, args, Some(result))
    Future.successful(result)
  }

  protected def onFailingCall[T](methodName: String, args: List[List[Any]], result: Throwable): Future[T] = {
    onInvocationInternal(methodName, args, Some(result))
    Future.failed(result)
  }

  protected def onGet[T](methodName: String, args: List[List[Any]], result: T): T = {
    onInvocationInternal(methodName, args, None)
    result
  }

  override def handleMore(): Unit =
    onFire("handleMore", List(Nil))

  override def doStuff(lol: Int, fuu: String)(cos: Option[Boolean]): Unit =
    onFire("doStuff", List(List(lol, fuu), List(cos)))

  override def doStuff(num: Int): Unit =
    onFire("doStuffInt", List(List(num)))

  def doStuff(yes: Boolean): Future[String] =
    onCall("doStuff", List(List(yes)), "doStuffResult")

  def doStuffWithFail(no: Boolean): Future[String] =
    onFailingCall("doStuffWithFail", List(List(no)), new Exception)

  @silent
  override def handle: Unit =
    onFire("handle", Nil)

  override def takeCC(r: Record): Unit =
    onFire("recordCC", List(List(r)))

  override def srslyDude(): Unit =
    onFire("srslyDude", List(Nil))
}

object TestRPC {
  /** Returns implementation of server side RPC interface */
  def rpcImpl(onInvocation: (String, List[List[Any]], Option[Any]) => Any) = new TestRPC with RPCMethodsImpl {
    override def doStuff(yes: Boolean): Future[String] =
      onCall("doStuff", List(List(yes)), "doStuffResult")

    override def doStuffWithFail(no: Boolean): Future[String] =
      onFailingCall("doStuffWithFail", List(List(no)), new Exception)

    override def doStuffWithEx(): Future[String] =
      onFailingCall("doStuffWithEx", List(List()), CustomRPCException(5))

    override def doStuffInt(yes: Boolean): Future[Int] =
      onCall("doStuffInt", List(List(yes)), 5)

    def doStuffUnit(): Future[Unit] =
      onCall("doStuffUnit", List(Nil), ())

    override def fireSomething(arg: Int): Unit =
      onFire("fireSomething", List(List(arg)))

    override def onInvocationInternal: (String, List[List[Any]], Option[Any]) => Any = onInvocation

    override def innerRpc(name: String): InnerRPC = {
      onInvocationInternal("innerRpc", List(List(name)), None)
      new InnerRPC {
        def func(arg: Int): Future[String] =
          onCall("innerRpc.func", List(List(arg)), "innerRpc.funcResult")

        def proc(): Unit =
          onFire("innerRpc.proc", List(Nil))
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

object TestClientRPC {
  /** Returns implementation of client side RPC interface */
  def rpcImpl(onInvocation: (String, List[List[Any]], Option[Any]) => Any) = new TestClientRPC with RPCMethodsImpl {
    override def onInvocationInternal: (String, List[List[Any]], Option[Any]) => Any = onInvocation

    override def innerRpc(name: String): InnerClientRPC = {
      onInvocationInternal("innerRpc", List(List(name)), None)
      new InnerClientRPC {
        def proc(): Unit =
          onFire("innerRpc.proc", List(Nil))
      }
    }
  }
}
