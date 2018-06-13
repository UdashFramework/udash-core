package io.udash.rpc.serialization

import com.avsystem.commons.serialization.{GenCodec, ListOutput, ObjectOutput, Output}

import scala.scalajs.js
import scala.scalajs.js.JSON

class NativeJsonOutput(valueConsumer: js.Any => Unit) extends Output {
  override def writeNull(): Unit =
    valueConsumer(null)

  override def writeString(str: String): Unit =
    valueConsumer(str)

  override def writeDouble(double: Double): Unit =
    valueConsumer(double)

  override def writeInt(int: Int): Unit =
    valueConsumer(int)

  override def writeLong(long: Long): Unit =
    writeString(long.toString)

  override def writeBigInt(bigInt: BigInt): Unit =
    writeString(bigInt.toString)

  override def writeBigDecimal(bigDecimal: BigDecimal): Unit =
    writeString(bigDecimal.toString)

  override def writeBoolean(boolean: Boolean): Unit =
    valueConsumer(boolean)

  override def writeList(): ListOutput =
    new NativeJsonListOutput(valueConsumer)

  override def writeObject(): ObjectOutput =
    new NativeJsonObjectOutput(valueConsumer)

  override def writeBinary(binary: Array[Byte]): Unit = {
    val l = writeList()
    binary.foreach(b => l.writeElement().writeInt(b))
    l.finish()
  }

  def writeRaw(raw: js.Any): Unit = valueConsumer(raw)
}

class NativeJsonListOutput(valueConsumer: js.Any => Unit) extends ListOutput {
  private val builder = new js.Array[js.Any]()
  override def writeElement(): Output =
    new NativeJsonOutput(el => builder.append(el))
  override def finish(): Unit =
    valueConsumer(builder)
}

class NativeJsonObjectOutput(valueConsumer: js.Any => Unit) extends ObjectOutput {
  private val builder = js.Dictionary.empty[js.Any]
  override def writeField(key: String): Output =
    new NativeJsonOutput(el => builder(key) = el)
  override def finish(): Unit =
    valueConsumer(builder)
}

object NativeJsonOutput {
  def write[T: GenCodec](value: T): String = {
    var result = ""
    GenCodec.write(new NativeJsonOutput(value => result = JSON.stringify(value)), value)
    result
  }
}
