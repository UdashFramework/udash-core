package io.udash.rpc

import upickle._

case class RawInvocation(rpcName: String, argLists: List[List[Js.Value]])

object RawInvocation {
  implicit val RawInvocationWriter = upickle.default.Writer[RawInvocation]{
    case inv =>
      Js.Obj(
        ("rpcName", Js.Str(inv.rpcName)),
        ("argLists", argsToJsArr(inv.argLists))
      )
  }

  implicit val RawInvocationReader = upickle.default.Reader[RawInvocation]{
    case obj: Js.Obj => try {
        val name: String = default.readJs[String](obj("rpcName"))
        val args: List[List[Js.Value]] = jsArrToArgs(obj("argLists"))
        RawInvocation(name, args)
      } catch {
        case ex: Exception => throw new Invalid.Data(obj, ex.getMessage)
      }
  }

  def argsToJsArr(argLists: List[List[Js.Value]]): Js.Value = {
    Js.Arr(argLists map { args => Js.Arr(args:_*) }:_*)
  }

  def jsArrToArgs(value: Js.Value): List[List[Js.Value]] = {
    value match {
      case array: Js.Arr =>
        (array.value map {
          case nestedArray: Js.Arr => nestedArray.value.toList
          case _ => List()
        }).toList
      case _ => List()
    }
  }
}