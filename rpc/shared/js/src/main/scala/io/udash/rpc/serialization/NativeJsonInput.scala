package io.udash.rpc.serialization

import com.avsystem.commons.serialization._
import scalajs.js

class NativeJsonInput(value: Any) extends Input { self =>
  private def read[T](expected: String)(matcher: PartialFunction[Any, ValueRead[T]]): ValueRead[T] =
    if (matcher.isDefinedAt(value)) matcher(value)
    else ReadFailed(s"$expected expected.")

  override def readNull(): ValueRead[Null] =
    read("Null") {
      case null => ReadSuccessful(null)
    }

  override def readString(): ValueRead[String] =
    read("String") {
      case s: String => ReadSuccessful(s)
    }

  override def readDouble(): ValueRead[Double] =
    read("Double") {
      case v: Double => ReadSuccessful(v)
    }

  override def readInt(): ValueRead[Int] =
    read("Int") {
      case v: Int => ReadSuccessful(v.toInt)
    }

  override def readLong(): ValueRead[Long] = {
    def parseLong(): Option[Long] = {
      value match {
        case s: String =>
          try {
            Some(s.toLong)
          } catch {
            case ex: NumberFormatException =>
              None
          }
        case _ => None
      }
    }

    parseLong() match {
      case Some(l) => ReadSuccessful(l)
      case None => ReadFailed(s"Long expected.")
    }
  }

  override def readBoolean(): ValueRead[Boolean] =
    read("Boolean") {
      case v: Boolean => ReadSuccessful(v)
    }

  override def readList(): ValueRead[ListInput] =
    read("List") {
      case array: js.Array[_] => ReadSuccessful(new JsonListInput(array))
    }

  override def readObject(): ValueRead[ObjectInput] =
    read("Object") {
      case obj: js.Object => ReadSuccessful(new JsonObjectInput(obj.asInstanceOf[js.Dictionary[_]]))
    }

  override def skip(): Unit = ()

  override def readBinary(): ValueRead[Array[Byte]] = {
    readList().map(l => l.iterator(i => i.readInt().get.toByte).toArray)
  }

  class JsonListInput(list: js.Array[_]) extends ListInput {
    var it = 0

    override def hasNext: Boolean =
      it < list.length

    override def nextElement(): Input = {
      val in = new NativeJsonInput(list(it))
      it += 1
      in
    }
  }

  class JsonObjectInput(dict: js.Dictionary[_]) extends ObjectInput {
    val it = dict.keysIterator

    override def hasNext: Boolean =
      it.hasNext

    override def nextField(): (String, Input) = {
      val key = it.next()
      (key, new NativeJsonInput(dict.apply(key)))
    }
  }
}