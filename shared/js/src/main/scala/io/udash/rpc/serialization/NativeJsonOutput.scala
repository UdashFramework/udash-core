package io.udash.rpc.serialization

import com.avsystem.commons.serialization.{ListOutput, ObjectOutput, Output}
import scalajs.js

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

  override def writeBoolean(boolean: Boolean): Unit =
    valueConsumer(boolean)

  override def writeList(): ListOutput =
    new ListOutput {
      val builder = new js.Array[js.Any]()
      override def writeElement(): Output =
        new NativeJsonOutput(el => builder.append(el))
      override def finish(): Unit =
        valueConsumer(builder)
    }

  override def writeObject(): ObjectOutput =
    new ObjectOutput {
      val builder = js.Dictionary.empty[js.Any]
      override def writeField(key: String): Output =
        new NativeJsonOutput(el => builder(key) = el)
      override def finish(): Unit =
        valueConsumer(builder)
    }

  override def writeBinary(binary: Array[Byte]): Unit = {
    val l = writeList()
    binary.foreach(b => l.writeElement().writeInt(b))
    l.finish()
  }
}