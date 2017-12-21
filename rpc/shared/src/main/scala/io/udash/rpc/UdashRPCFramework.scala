package io.udash.rpc

import com.avsystem.commons.rpc.{GetterRPCFramework, ProcedureRPCFramework}
import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.ExceptionCodecRegistry

import scala.language.postfixOps

/** Base for all RPC frameworks in Udash. */
trait UdashRPCFramework extends GetterRPCFramework with ProcedureRPCFramework with GenCodecSerializationFramework {
  type RawRPC <: GetterRawRPC with ProcedureRawRPC

  class ParamTypeMetadata[+T]
  implicit object ParamTypeMetadata extends ParamTypeMetadata[Nothing]

  class ResultTypeMetadata[+T]
  implicit object ResultTypeMetadata extends ResultTypeMetadata[Nothing]

  val RawValueCodec: GenCodec[RawValue]

  private implicit def rawCodec: GenCodec[RawValue] =
    RawValueCodec

  /** Converts `String` into `RawValue`. It is used to read data from network. */
  def stringToRaw(string: String): RawValue

  /** Converts `RawValue` into `String`. It is used to write data to network. */
  def rawToString(raw: RawValue): String

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
  /** Message reporting exception from [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseException(name: String, exception: Throwable, callId: String) extends RPCResponse

  case class RPCFailure(remoteCause: String, remoteMessage: String) extends Exception(s"$remoteCause: $remoteMessage")

  /* GenCodecs for internal classes of RPC framework. */
  implicit val RawInvocationCodec: GenCodec[RawInvocation] = new GenCodec[RawInvocation] {
    override def read(input: Input): RawInvocation = {
      val obj = input.readObject()
      val rpcName: String = obj.nextField().assertField("rpcName").readString()
      val args: List[List[RawValue]] = readArgs(obj.nextField().assertField("argLists").readList())
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
        val it = i.readList().iterator((el: Input) => argList += RawValueCodec.read(el))
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

  implicit val RPCRequestCodec: GenCodec[RPCRequest] = new GenCodec[RPCRequest] {
    override def read(input: Input): RPCRequest = {
      val obj = input.readObject()
      val inv = RawInvocationCodec.read(obj.nextField().assertField("inv"))
      val getters = obj.nextField().assertField("getters").readList().iterator(RawInvocationCodec.read).toList
      val tpe = obj.nextField().assertField("type").readString()

      tpe match {
        case "RPCCall" =>
          val callId = obj.nextField().assertField("callId").readString()
          RPCCall(inv, getters, callId)
        case "RPCFire" =>
          RPCFire(inv, getters)
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

  def RPCResponseCodec(exceptionsRegistry: ExceptionCodecRegistry): GenCodec[RPCResponse] =
    new GenCodec[RPCResponse] {
      override def read(input: Input): RPCResponse = {
        val obj = input.readObject()
        val tpe = obj.nextField().assertField("type").readString()

        tpe match {
          case "RPCResponseSuccess" =>
            val response = RawValueCodec.read(obj.nextField().assertField("response"))
            val callId = obj.nextField().assertField("callId").readString()
            RPCResponseSuccess(response.asInstanceOf[RawValue], callId)
          case "RPCResponseFailure" =>
            val cause = obj.nextField().assertField("cause").readString()
            val errorMsg = obj.nextField().assertField("errorMsg").readString()
            val callId = obj.nextField().assertField("callId").readString()
            RPCResponseFailure(cause, errorMsg, callId)
          case "RPCResponseException" =>
            val wrapper = obj.nextField().assertField("exception").readObject()
            val exceptionName = wrapper.nextField().assertField("type").readString()
            val exception = exceptionsRegistry.get[Throwable](exceptionName).read(wrapper.nextField().assertField("data"))
//            val stack = wrapper.nextField().assertField("stacktrace")
//            if (stack.inputType == InputType.List) {
//              val stackData = stack.readList().iterator { input =>
//                val element = input.readObject()
//                val fileName = element.nextField().assertField("fileName").readString()
//                val className = element.nextField().assertField("className").readString()
//                val methodName = element.nextField().assertField("methodName").readString()
//                val line = element.nextField().assertField("line").readInt()
//                new StackTraceElement(className, methodName, fileName, line)
//              }
//              exception.setStackTrace(stackData.toArray)
//            } else stack.readNull()
            val callId = obj.nextField().assertField("callId").readString()
            RPCResponseException(exceptionName, exception, callId)
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
          case RPCResponseException(name, exception, callId) =>
            obj.writeField("type").writeString("RPCResponseException")
            val exceptionField = obj.writeField("exception").writeObject()
            exceptionField.writeField("type").writeString(name)
            exceptionsRegistry.get(name).write(exceptionField.writeField("data"), exception)
//            val stack = exceptionField.writeField("stacktrace")
//            if (exception.getStackTrace != null) {
//              val stackList = stack.writeList()
//              exception.getStackTrace.foreach { element =>
//                val field = stackList.writeElement().writeObject()
//                field.writeField("fileName").writeString(element.getFileName)
//                field.writeField("className").writeString(element.getClassName)
//                field.writeField("methodName").writeString(element.getMethodName)
//                field.writeField("line").writeInt(element.getLineNumber)
//                field.finish()
//              }
//              stackList.finish()
//            } else stack.writeNull()
            exceptionField.finish()
            obj.writeField("callId").writeString(callId)
        }
        obj.finish()
      }
    }

  implicit val RPCFailureCodec: GenCodec[RPCFailure] = new GenCodec[RPCFailure] {
    override def read(input: Input): RPCFailure = {
      val obj = input.readObject()
      val remoteCause: String = obj.nextField().assertField("remoteCause").readString()
      val remoteMessage: String = obj.nextField().assertField("remoteMessage").readString()
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