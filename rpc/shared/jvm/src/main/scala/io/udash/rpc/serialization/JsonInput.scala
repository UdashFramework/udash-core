package io.udash.rpc.serialization

import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.jawn._

class JsonInput(value: JValue) extends Input {
  private def _read[T](expect: String)(matcher: PartialFunction[JValue, ValueRead[T]]): ValueRead[T] = {
    if (matcher.isDefinedAt(value)) matcher(value)
    else ReadFailed(s"$expect expected.")
  }

  def readNull() = _read("Null") {
    case JNull => ReadSuccessful(null)
  }

  def readString() = _read("String") {
    case JString(v) => ReadSuccessful(v)
  }

  def readInt() = _read("Int") {
    case JInt(v) => ReadSuccessful(v)
  }

  def readLong() = _read("Long") {
    case JString(v) => ReadSuccessful(v.toLong)
  }

  def readDouble() = _read("Double") {
    case JDouble(v) => ReadSuccessful(v)
  }

  def readBoolean() = _read("Boolean") {
    case JBoolean(v) => ReadSuccessful(v)
  }

  def readBinary() = _read("List of bytes") {
    case JList(vals) if vals.forall(_.isInstanceOf[JInt]) => ReadSuccessful(vals.map(_.asInstanceOf[JInt].value.toByte).toArray)
  }

  def readList() =  _read("List") {
    case JList(vals) =>
      ReadSuccessful(new ListInput {
        private val it = vals.iterator.map(new JsonInput(_))
        def hasNext = it.hasNext
        def nextElement() = it.next()
      })
  }

  def readObject() = _read("Object") {
    case JObject(entries) =>
      ReadSuccessful(new ObjectInput {
        private val it = entries.iterator.map { case (k, v) => (k, new JsonInput(v)) }
        def hasNext = it.hasNext
        def nextField() = it.next()
      })
  }

  def skip() = ()
}