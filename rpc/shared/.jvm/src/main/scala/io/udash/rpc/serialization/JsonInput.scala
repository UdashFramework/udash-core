package io.udash.rpc.serialization

import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.jawn._

class JsonInput(value: JValue) extends Input {
  private def _read[T](expect: String)(matcher: PartialFunction[JValue, T]): T =
    matcher.applyOrElse(value, (_: JValue) => throw new ReadFailure(s"$expect expected."))

  def inputType = value match {
    case JNull => InputType.Null
    case _: JList => InputType.List
    case _: JObject => InputType.Object
    case _ => InputType.Simple
  }

  def readNull() = _read("Null") {
    case JNull => null
  }

  def readString() = _read("String") {
    case JString(v) => v
  }

  def readInt() = _read("Int") {
    case JInt(v) => v
    case JDouble(v) if v == v.toInt => v.toInt
  }

  def readLong() = _read("Long") {
    case JString(v) => v.toLong
    case JInt(v) => v
    case JDouble(v) if v == v.toLong => v.toLong
  }

  def readDouble() = _read("Double") {
    case JDouble(v) => v
    case JInt(v) => v.toDouble
  }

  def readBoolean() = _read("Boolean") {
    case JBoolean(v) => v
  }

  def readBinary() = _read("List of bytes") {
    case JList(vals) if vals.forall(_.isInstanceOf[JInt]) => vals.map(_.asInstanceOf[JInt].value.toByte).toArray
  }

  def readList() =  _read("List") {
    case JList(vals) =>
      new ListInput {
        private val it = vals.iterator.map(new JsonInput(_))
        def hasNext = it.hasNext
        def nextElement() = it.next()
      }
  }

  def readObject() = _read("Object") {
    case JObject(entries) =>
      new ObjectInput {
        private val it = entries.iterator.map { case (k, v) => new JsonFieldInput(k, v) }
        def hasNext = it.hasNext
        def nextField() = it.next()
      }
  }

  def skip() = ()
}

class JsonFieldInput(val fieldName: String, value: JValue) extends JsonInput(value) with FieldInput
