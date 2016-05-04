package io.udash.rpc

import com.avsystem.commons.rpc.RPCFramework
import com.avsystem.commons.serialization._
import io.udash.macros

import scala.language.postfixOps

trait UdashRPCFramework extends RPCFramework {
  type Writer[T] = GenCodec.Auto[T]
  type Reader[T] = GenCodec.Auto[T]

  val RawValueCodec: GenCodec[RawValue]

  private implicit def rawCodec: GenCodec[RawValue] =
    RawValueCodec

  /** Converts `String` into `RawValue`. It is used to read data from network. */
  def stringToRaw(string: String): RawValue
  /** Converts `RawValue` into `String`. It is used to write data to network. */
  def rawToString(raw: RawValue): String

  /** Returns `Input` for data marshalling. */
  def inputSerialization(value: RawValue): Input
  /** Returns `Output` for data unmarshalling. */
  def outputSerialization(valueConsumer: RawValue => Unit): Output

  /** Converts value of type `T` into `RawValue`. */
  def write[T: Writer](value: T): RawValue = {
    var result: RawValue = null.asInstanceOf[RawValue]
    GenCodec.autoWrite[T](outputSerialization(result = _), value)
    result
  }

  /** Converts `RawValue` into value of type `T`. */
  def read[T: Reader](raw: RawValue): T =
    GenCodec.autoRead[T](inputSerialization(raw))

  class AsRawClientRPC[T](implicit asRawRpc: AsRawRPC[T]) {
    def asRaw(rpcImpl: T): RawRPC =
      asRawRpc.asRaw(rpcImpl)
  }

  object AsRawClientRPC {
    def apply[T](implicit asRawClientRPC: AsRawClientRPC[T]): AsRawClientRPC[T] =
      asRawClientRPC
  }

  implicit def materializeAsRawClient[T]: AsRawClientRPC[T] =
    macro macros.rpc.UdashRPCMacros.asRawClientImpl[T]

  class AsRealClientRPC[T](implicit asRealRpc: AsRealRPC[T]) {
    def asReal(rawRpc: RawRPC): T =
      asRealRpc.asReal(rawRpc)
  }

  object AsRealClientRPC {
    def apply[T](implicit asRealClientRPC: AsRealClientRPC[T]): AsRealClientRPC[T] =
      asRealClientRPC
  }

  implicit def materializeAsRealClient[T]: AsRealClientRPC[T] =
    macro macros.rpc.UdashRPCMacros.asRealClientImpl[T]

  sealed trait RPCRequest {
    def invocation: RawInvocation
    def gettersChain: List[RawInvocation]
  }

  /** [[io.udash.rpc.UdashRPCFramework.RPCRequest]] which returns some value. */
  case class RPCCall(invocation: RawInvocation, gettersChain: List[RawInvocation], callId: String) extends RPCRequest
  /** [[io.udash.rpc.UdashRPCFramework.RPCRequest]] which returns Unit. */
  case class RPCFire(invocation: RawInvocation, gettersChain: List[RawInvocation]) extends RPCRequest

  sealed trait RPCResponse {
    def callId: String
  }
  /** Message containing response for [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseSuccess(response: RawValue, callId: String) extends RPCResponse
  /** Message reporting failure of [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseFailure(cause: String, errorMsg: String, callId: String) extends RPCResponse

  case class RPCFailure(remoteCause: String, remoteMessage: String) extends Exception(s"$remoteCause: $remoteMessage")

  /* GenCodecs for internal classes of RPC framework. */
  implicit val RawInvocationCodec = new GenCodec[RawInvocation] {
    override def read(input: Input): RawInvocation = {
      val obj = input.readObject().get
      var rpcName: String = null
      var args: List[List[RawValue]] = null
      while (obj.hasNext) {
        obj.nextField() match {
          case ("rpcName", in) =>
            rpcName = in.readString().get
          case ("argLists", in) =>
            args = readArgs(in.readList().get)
        }
      }
      RawInvocation(rpcName, args)
    }

    override def write(output: Output, inv: RawInvocation): Unit = {
      val obj = output.writeObject()
      obj.writeField("rpcName").writeString(inv.rpcName)
      val argLists: ListOutput = obj.writeField("argLists").writeList()
      writeArgs(argLists, inv.argLists)
      argLists.finish()
      obj.finish()
    }

    private def readArgs(input: ListInput): List[List[RawValue]] = {
      val argLists = List.newBuilder[List[RawValue]]
      while (input.hasNext) {
        val i = input.nextElement()
        val argList = List.newBuilder[RawValue]
        val it = i.readList().get.iterator((el: Input) => argList += RawValueCodec.read(el))
        while (it.hasNext) it.next()
        argLists += argList.result()
      }
      argLists.result()
    }

    private def writeArgs(input: ListOutput, argLists: List[List[RawValue]]): Unit = {
      argLists.foreach((argList: List[RawValue]) => {
        val args = input.writeElement().writeList()
        argList.foreach(v => RawValueCodec.write(args.writeElement(), v))
        args.finish()
      })
    }
  }

  implicit val RPCRequestCodec = new GenCodec[RPCRequest] {
    override def read(input: Input): RPCRequest = {
      val obj = input.readObject().get
      var inv: RawInvocation = null
      val getters = List.newBuilder[RawInvocation]
      var callId: String = null
      var tpe: String = null
      while (obj.hasNext) {
        obj.nextField() match {
          case ("inv", in) =>
            inv = RawInvocationCodec.read(in)
          case ("getters", in) =>
            val l = in.readList().get
            while (l.hasNext) getters += RawInvocationCodec.read(l.nextElement)
          case ("callId", in) =>
            callId = in.readString().get
          case ("type", in) =>
            tpe = in.readString().get
        }
      }

      tpe match {
        case "RPCCall" =>
          RPCCall(
            inv,
            getters.result(),
            callId
          )
        case "RPCFire" =>
          RPCFire(inv, getters.result())
      }
    }

    override def write(output: Output, value: RPCRequest): Unit = {
      val obj = output.writeObject()
      RawInvocationCodec.write(obj.writeField("inv"), value.invocation)
      val gettersOutput = obj.writeField("getters").writeList()
      value.gettersChain.foreach(el => RawInvocationCodec.write(gettersOutput.writeElement(), el))
      gettersOutput.finish()
      value match {
        case RPCCall(_, _, callId) =>
          obj.writeField("type").writeString("RPCCall")
          obj.writeField("callId").writeString(callId)
        case _: RPCFire =>
          obj.writeField("type").writeString("RPCFire")
      }
      obj.finish()
    }
  }

  implicit val RPCResponseCodec = new GenCodec[RPCResponse] {
    override def read(input: Input): RPCResponse = {
      val obj = input.readObject().get
      var response: Any = null
      var callId: String = null
      var cause: String = null
      var errorMsg: String = null
      var tpe: String = null
      while (obj.hasNext) {
        obj.nextField() match {
          case ("response", in) =>
            response = RawValueCodec.read(in)
          case ("callId", in) =>
            callId = in.readString().get
          case ("cause", in) =>
            cause = in.readString().get
          case ("errorMsg", in) =>
            errorMsg = in.readString().get
          case ("type", in) =>
            tpe = in.readString().get
        }
      }

      tpe match {
        case "RPCResponseSuccess" =>
          RPCResponseSuccess(response.asInstanceOf[RawValue], callId)
        case "RPCResponseFailure" =>
          RPCResponseFailure(cause, errorMsg, callId)
      }
    }

    override def write(output: Output, value: RPCResponse): Unit = {
      val obj = output.writeObject()
      value match {
        case RPCResponseSuccess(response, callId) =>
          obj.writeField("type").writeString("RPCResponseSuccess")
          RawValueCodec.write(obj.writeField("response"), response)
          obj.writeField("callId").writeString(callId)
        case RPCResponseFailure(cause, errorMsg, callId) =>
          obj.writeField("type").writeString("RPCResponseFailure")
          obj.writeField("cause").writeString(cause)
          obj.writeField("errorMsg").writeString(errorMsg)
          obj.writeField("callId").writeString(callId)
      }
      obj.finish()
    }
  }

  implicit val RPCFailureCodec = new GenCodec[RPCFailure] {
    override def read(input: Input): RPCFailure = {
      val obj = input.readObject().get
      var remoteCause: String = null
      var remoteMessage: String = null
      while (obj.hasNext) {
        obj.nextField() match {
          case ("remoteCause", in) =>
            remoteCause = in.readString().get
          case ("remoteMessage", in) =>
            remoteMessage = in.readString().get
        }
      }
      RPCFailure(remoteCause, remoteMessage)
    }

    override def write(output: Output, value: RPCFailure): Unit = {
      val obj = output.writeObject()
      obj.writeField("remoteCause").writeString(value.remoteCause)
      obj.writeField("remoteMessage").writeString(value.remoteMessage)
      obj.finish()
    }
  }
}