package io.udash.rpc.serialization

import com.avsystem.commons.misc.Opt
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._

import scala.scalajs.js
import scala.scalajs.js.JSON

//todo move to scala-commons
class NativeJsonInput(value: js.Any) extends Input { self =>
  private def read[T](expected: String)(matcher: PartialFunction[Any, T]): T =
    matcher.applyOrElse(value, (_: Any) => throw new ReadFailure(s"$expected expected."))

  override def isNull: Boolean = value == null

  override def readNull(): Null =
    read("Null") {
      case null => null
    }

  override def readString(): String =
    read("String") {
      case s: String => s
    }

  override def readDouble(): Double =
    read("Double") {
      case v: Double => v
    }

  override def readInt(): Int =
    read("Int") {
      case v: Int => v.toInt
    }

  override def readLong(): Long = {
    def fail = throw new ReadFailure(s"Long expected.")
    (value: Any) match {
      case s: String => try s.toLong catch {
        case _: NumberFormatException => fail
      }
      case i: Int => i
      case d: Double if d.isWhole => d.toLong
      case _ => fail
    }
  }

  override def readBigInt(): BigInt = {
    def fail = throw new ReadFailure(s"BigInt expected.")
    (value: Any) match {
      case s: String => try BigInt(s) catch {
        case _: NumberFormatException => fail
      }
      case i: Int => BigInt(i)
      case d: Double if d.isWhole => BigInt(d.toLong)
      case _ => fail
    }
  }

  override def readBigDecimal(): BigDecimal = {
    def fail = throw new ReadFailure(s"BigDecimal expected.")
    (value: Any) match {
      case s: String => try BigDecimal(s) catch {
        case _: NumberFormatException => fail
      }
      case i: Int => BigDecimal(i)
      case d: Double => BigDecimal(d)
      case _ => fail
    }
  }

  override def readBoolean(): Boolean =
    read("Boolean") {
      case v: Boolean => v
    }

  override def readList(): ListInput =
    read("List") {
      case array: js.Array[js.Any@unchecked] => new JsonListInput(array)
    }

  override def readObject(): ObjectInput =
    read("Object") {
      case obj: js.Object => new JsonObjectInput(obj.asInstanceOf[js.Dictionary[js.Any]])
    }

  override def skip(): Unit = ()

  override def readBinary(): Array[Byte] = {
    readList().iterator(i => i.readInt().toByte).toArray
  }

  def readRaw(): Any = value
}

class JsonListInput(list: js.Array[js.Any]) extends ListInput {
  var it = 0

  override def hasNext: Boolean =
    it < list.length

  override def nextElement(): Input = {
    val in = new NativeJsonInput(list(it))
    it += 1
    in
  }
}

class JsonObjectInput(dict: js.Dictionary[js.Any]) extends ObjectInput {
  val it = dict.keysIterator

  override def hasNext: Boolean =
    it.hasNext

  override def peekField(name: String): Opt[FieldInput] =
    if (dict.contains(name)) Opt(new NativeJsonFieldInput(name, dict(name))) else Opt.Empty

  override def nextField(): FieldInput = {
    val key = it.next()
    new NativeJsonFieldInput(key, dict.apply(key))
  }
}

class NativeJsonFieldInput(val fieldName: String, value: js.Any)
  extends NativeJsonInput(value) with FieldInput

object NativeJsonInput {
  def read[T: GenCodec](value: String): T = {
    GenCodec.read[T](new NativeJsonInput(JSON.parse(value)))
  }
}
