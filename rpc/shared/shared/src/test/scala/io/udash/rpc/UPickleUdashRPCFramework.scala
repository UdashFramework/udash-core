package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._
import upickle.Js

import scala.collection.mutable.ListBuffer

trait UPickleUdashRPCFramework extends UdashRPCFramework {
  override type RawValue = Js.Value

  def inputSerialization(value: Js.Value): Input =
    new JsObjectInput(value)

  def outputSerialization(valueConsumer: Js.Value => Unit): Output =
    new JsObjectOutput(valueConsumer)

  def stringToRaw(string: String): RawValue =
    try {
      upickle.json.read(string)
    }
    catch {
      case ex: upickle.Invalid => throw new ReadFailure("Parse error!", ex)
    }

  def rawToString(raw: Js.Value): String =
    upickle.json.write(raw)

  /**
    * Created by grzesiul on 2016-02-03.
    */
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
      case Js.Num(num) if num == num.toLong => num.toLong
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

    def skip() = ()
  }

  class JsObjectFieldInput(val fieldName: String, value: Js.Value) extends JsObjectInput(value) with FieldInput

  /**
    * Created by grzesiul on 2016-02-02.
    */
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

      def finish() = setResultThenConsume(Js.Obj(builder.result(): _*))
    }
  }

  val RawValueCodec: GenCodec[RawValue] = new GenCodec[Js.Value] {
    override def read(input: Input): Js.Value = {
      val obj = input.readObject()
      val fields = obj.iterator(i => i).toMap
      val tpe = fields("type").readString()
      val item = fields("item")
      tpe match {
        case "Bool" =>
          if (item.readBoolean()) Js.True
          else Js.False
        case "Num" =>
          Js.Num(item.readDouble())
        case "String" =>
          Js.Str(item.readString())
        case "Obj" =>
          val subfields = item.readList()
          val it = subfields.iterator(i => i).map(el => {
            val i = el.readObject()
            val objFields = i.iterator(i => i).toMap
            (objFields("key").readString(), read(objFields("item")))
          })
          Js.Obj(it.toSeq: _*)
        case "Arr" =>
          val els = item.readList()
          val it = els.iterator(i => i).map(el => read(el))
          Js.Arr(it.toSeq: _*)
        case "Null" =>
          item.readNull()
          Js.Null
      }
    }

    override def write(output: Output, value: Js.Value): Unit = {
      val obj = output.writeObject()
      val tpe = obj.writeField("type")
      val item = obj.writeField("item")
      value match {
        case Js.True =>
          tpe.writeString("Bool")
          item.writeBoolean(true)
        case Js.False =>
          tpe.writeString("Bool")
          item.writeBoolean(false)
        case Js.Num(v) =>
          tpe.writeString("Num")
          item.writeDouble(v)
        case Js.Str(v) =>
          tpe.writeString("String")
          item.writeString(v)
        case v: Js.Obj =>
          tpe.writeString("Obj")
          val fields = item.writeList()
          v.value.foreach {
            case (key, subfield) =>
              val i = fields.writeElement().writeObject()
              i.writeField("key").writeString(key)
              write(i.writeField("item"), subfield)
              i.finish()
          }
          fields.finish()
        case v: Js.Arr =>
          tpe.writeString("Arr")
          val fields = item.writeList()
          v.value.foreach(subfield => {
            write(fields.writeElement(), subfield)
          })
          fields.finish()
        case Js.Null | null =>
          tpe.writeString("Null")
          item.writeNull()
      }
      obj.finish()
    }
  }
}

object ServerUPickleUdashRPCFramework extends UPickleUdashRPCFramework with ServerUdashRPCFramework
object ClientUPickleUdashRPCFramework extends UPickleUdashRPCFramework with ClientUdashRPCFramework
