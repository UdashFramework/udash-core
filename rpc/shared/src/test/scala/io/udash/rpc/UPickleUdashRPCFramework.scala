package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.JsonStr
import ujson.Js

import scala.collection.mutable.ListBuffer

trait UPickleUdashRPCFramework extends UdashRPCFramework {

  override def read[T: GenCodec](value: JsonStr): T =
    GenCodec.read[T](new JsObjectInput(upickle.json.read(value.json)))

  override def write[T: GenCodec](value: T): JsonStr = {
    var result: String = null
    GenCodec.write(new JsObjectOutput(v => result = upickle.json.write(v)), value)
    JsonStr(result)
  }

  val rawValueCodec: GenCodec[JsonStr] = GenCodec.createNonNull(
    {
      case cji: JsObjectInput => JsonStr(upickle.json.write(cji.readRaw()))
      case in => JsonStr(in.readString())
    },
    {
      case (cjo: JsObjectOutput, json) => cjo.writeRaw(upickle.json.read(json.json))
      case (out, v) => out.writeString(v.json)
    }
  )

  class JsObjectInput(value: Js.Value) extends Input {
    def inputType = value match {
      case Js.Null => InputType.Null
      case _: Js.Arr => InputType.List
      case _: Js.Obj => InputType.Object
      case _ => InputType.Simple
    }

    def readNull() = value match {
      case Js.Null => null
      case _ => throw new ReadFailure("Not Js.Null")
    }

    def readString() = value match {
      case Js.Str(str) => str
      case _ => throw new ReadFailure("Not Js.Str")
    }

    def readLong() = value match {
      case Js.Str(num) => num.toLong
      case Js.Num(num) if num.isWhole => num.toLong
      case _ => throw new ReadFailure("Not Js.Num (Long)")
    }

    def readDouble() = value match {
      case Js.Num(num) => num
      case _ => throw new ReadFailure("Not Js.Num (Double)")
    }

    def readBoolean() = value match {
      case Js.True => true
      case Js.False => false
      case _ => throw new ReadFailure("Not Js.Bool")
    }

    def readInt() = value match {
      case Js.Num(num) if num == num.toInt => num.toInt
      case _ => throw new ReadFailure("Not Js.Num (Int) ")
    }

    def readBigInt(): BigInt = value match {
      case Js.Str(num) => BigInt(num)
      case Js.Num(num) if num.isWhole => BigInt(num.toLong)
      case _ => throw new ReadFailure("Not Js.Num (BigInt)")
    }

    def readBigDecimal(): BigDecimal = value match {
      case Js.Str(num) => BigDecimal(num)
      case Js.Num(num) => BigDecimal(num)
      case _ => throw new ReadFailure("Not Js.Num (BigInt)")
    }

    def readObject() = value match {
      case asObj: Js.Obj =>

        new ObjectInput {
          private val it = asObj.value.iterator.map {
            case (k, v) => new JsObjectFieldInput(k, v)
          }

          def nextField() = it.next()

          def hasNext = it.hasNext
        }
      case _ =>
        throw new ReadFailure("Not Js.Obj")
    }

    def readBinary() = value match {
      case jsArr: Js.Arr if jsArr.value.forall(_.isInstanceOf[Js.Num]) =>
        Array(jsArr.value.map(_.asInstanceOf[Js.Num].value.toByte): _*)
      case _ => throw new ReadFailure("Not Js.Arr of Js.Num")
    }

    def readList() = value match {
      case jsArr: Js.Arr =>
        new ListInput {
          private val it = jsArr.value.iterator.map(new JsObjectInput(_))

          def nextElement() = it.next()

          def hasNext = it.hasNext
        }
      case _ =>
        throw new ReadFailure("Not Js.Arr")
    }

    def readRaw(): Js.Value = value

    def skip() = ()
  }

  class JsObjectFieldInput(val fieldName: String, value: Js.Value) extends JsObjectInput(value) with FieldInput

  class JsObjectOutput(val consumer: Js.Value => Unit) extends Output {
    var result: Js.Value = _
    private val setResultThenConsume: Js.Value => Unit = consumer.compose(value => {
      result = value
      value
    })

    def writeNull() = setResultThenConsume(Js.Null)

    def writeBoolean(boolean: Boolean) = setResultThenConsume(if (boolean) Js.True else Js.False)

    def writeString(str: String) = setResultThenConsume(Js.Str(str))

    def writeInt(int: Int) = setResultThenConsume(Js.Num(int))

    def writeLong(long: Long) = setResultThenConsume(Js.Str(long.toString))

    def writeDouble(double: Double) = setResultThenConsume(Js.Num(double))

    def writeBigInt(bigInt: BigInt): Unit = setResultThenConsume(Js.Str(bigInt.toString))

    def writeBigDecimal(bigDecimal: BigDecimal): Unit = setResultThenConsume(Js.Str(bigDecimal.toString))

    def writeBinary(binary: Array[Byte]) =
      setResultThenConsume(Js.Arr(binary.map(b => Js.Num(b.toDouble)): _*))

    def writeList() = new ListOutput {
      private val buffer = new ListBuffer[Js.Value]

      def writeElement() = new JsObjectOutput(buffer += _)

      def finish() = setResultThenConsume(Js.Arr(buffer.result(): _*))
    }

    def writeObject() = new ObjectOutput {
      private val builder = new ListBuffer[(String, Js.Value)]

      def writeField(key: String) = new JsObjectOutput(v => builder += ((key, v)))

      def finish() = setResultThenConsume(Js.Obj.from(builder.result()))
    }

    def writeRaw(js: Js.Value) = consumer(js)
  }
}

object ServerUPickleUdashRPCFramework extends UPickleUdashRPCFramework with ServerUdashRPCFramework
object ClientUPickleUdashRPCFramework extends UPickleUdashRPCFramework with ClientUdashRPCFramework
