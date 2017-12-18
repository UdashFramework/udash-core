package io.udash.rpc.serialization

import com.avsystem.commons.serialization.{ListOutput, ObjectOutput, Output}

private[serialization] trait AbstractJsonOutput extends Output {
  def consume(element: String): Unit

  override def writeNull(): Unit =
    consume("null")

  override def writeString(str: String): Unit = {
    val sb = new StringBuilder
    sb.append('\"')
    EscapeUtils.escape(sb, str)
    sb.append('\"')
    consume(sb.result())
  }

  override def writeDouble(double: Double): Unit =
    consume(double.toString)

  override def writeInt(int: Int): Unit =
    consume(int.toString)

  override def writeLong(long: Long): Unit =
    writeString(long.toString)

  override def writeBoolean(boolean: Boolean): Unit =
    consume(boolean.toString)

  override def writeList(): ListOutput =
    new JsonListOutput(new StringBuilder()) {
      override def finish(): Unit =
        consume(sb.append(']').result())
    }

  override def writeObject(): ObjectOutput =
    new JsonObjectOutput(new StringBuilder()) {
      override def finish(): Unit =
        consume(sb.append('}').result())
    }

  override def writeBinary(binary: Array[Byte]): Unit = {
    val l = writeList()
    binary.foreach(b => l.writeElement().writeInt(b))
    l.finish()
  }
}

class JsonOutput(valueConsumer: String => Unit) extends AbstractJsonOutput {
  override def consume(element: String): Unit =
    valueConsumer(element)
}

private[serialization] class JsonOutputBuilder(sb: StringBuilder) extends AbstractJsonOutput {
  override def consume(element: String): Unit =
    sb.append(element)

  override def writeString(str: String): Unit = {
    sb.append('\"')
    EscapeUtils.escape(sb, str)
    sb.append('\"')
  }

  override def writeList(): ListOutput =
    new JsonListOutput(sb)

  override def writeObject(): ObjectOutput =
    new JsonObjectOutput(sb)
}

private[rpc] class JsonListOutput(protected val sb: StringBuilder) extends ListOutput {
  var first = true
  sb.append('[')

  override def writeElement(): Output = {
    if (!first) sb.append(',')
    first = false
    new JsonOutputBuilder(sb)
  }

  override def finish(): Unit =
    sb.append(']')
}

private[rpc] class JsonObjectOutput(protected val sb: StringBuilder) extends ObjectOutput {
  var first = true
  sb.append('{')

  override def writeField(key: String): Output = {
    if (!first) sb.append(',')
    first = false
    sb.append('\"')
    EscapeUtils.escape(sb, key)
    sb.append("\":")
    new JsonOutputBuilder(sb)
  }

  override def finish(): Unit =
    sb.append('}')
}