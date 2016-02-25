package io.udash.rpc.internals

import io.udash.rpc.RawInvocation
import upickle._

sealed trait RPCRequest {
  def invocation: RawInvocation
  def gettersChain: List[RawInvocation]
}
case class RPCCall(invocation: RawInvocation, gettersChain: List[RawInvocation], callId: String) extends RPCRequest
case class RPCFire(invocation: RawInvocation, gettersChain: List[RawInvocation]) extends RPCRequest

sealed trait RPCResponse {
  def callId: String
}
case class RPCResponseSuccess(response: Js.Value, callId: String) extends RPCResponse
case class RPCResponseFailure(cause: String, errorMsg: String, callId: String) extends RPCResponse

object RPCRequest {
  import RawInvocation._

  implicit val RPCRequestWriter = upickle.default.Writer[RPCRequest] {
    case RPCCall(invocation, getters, callId) => Js.Obj(
      "type" -> Js.Str("RPCCall"),
      "invocation" -> default.writeJs[RawInvocation](invocation),
      "getters" -> Js.Arr(getters.map(default.writeJs[RawInvocation]):_*),
      "callId" -> Js.Str(callId)
    )
    case RPCFire(invocation, getters) => Js.Obj(
      "type" -> Js.Str("RPCFire"),
      "invocation" -> default.writeJs[RawInvocation](invocation),
      "getters" -> Js.Arr(getters.map(default.writeJs[RawInvocation]):_*)
    )
  }

  implicit val RPCRequestReader = upickle.default.Reader[RPCRequest] {
    case obj: Js.Obj => try {
        val invocation = default.readJs[RawInvocation](obj("invocation"))
        val getters = obj("getters").asInstanceOf[Js.Arr].value.map(default.readJs[RawInvocation]).toList
        default.readJs[String](obj("type")) match {
          case "RPCCall" =>
            RPCCall(invocation, getters, default.readJs[String](obj("callId")))
          case "RPCFire" => RPCFire(invocation, getters)
        }
      } catch {
        case ex: Exception => throw new Invalid.Data(obj, ex.getMessage)
      }
  }
}

object RPCResponse {
  implicit val RPCResponseWriter = upickle.default.Writer[RPCResponse] {
    case RPCResponseSuccess(response, callId) => Js.Obj(
      "type" -> Js.Str("RPCResponseSuccess"),
      "response" -> response,
      "callId" -> Js.Str(callId)
    )
    case RPCResponseFailure(cause, errorMsg, callId) => Js.Obj(
      "type" -> Js.Str("RPCResponseFailure"),
      "cause" -> Js.Str(cause),
      "errorMsg" -> Js.Str(errorMsg),
      "callId" -> Js.Str(callId)
    )
  }

  implicit val RPCResponseReader = upickle.default.Reader[RPCResponse] {
    case obj: Js.Obj => try {
        val callId = default.readJs[String](obj("callId"))
        default.readJs[String](obj("type")) match {
          case "RPCResponseSuccess" =>
            val response = obj("response")
            RPCResponseSuccess(response, callId)
          case "RPCResponseFailure" =>
            val cause = default.readJs[String](obj("cause"))
            val errorMsg = default.readJs[String](obj("errorMsg"))
            RPCResponseFailure(cause, errorMsg, callId)
        }
      } catch {
        case ex: Exception => throw new Invalid.Data(obj, ex.getMessage)
      }
  }
}