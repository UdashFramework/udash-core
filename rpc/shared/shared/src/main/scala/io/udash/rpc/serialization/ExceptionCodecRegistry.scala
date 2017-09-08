package io.udash.rpc.serialization

import com.avsystem.commons.serialization.{GenCodec, Input, InputType, Output}

import scala.collection.mutable
import scala.reflect.ClassTag

trait ExceptionCodecRegistry {
  /** Registers GenCodec for provided class name. It can override previous value. */
  def register[T <: Throwable : ClassTag](codec: GenCodec[T]): Unit
  /** Returns GenCodec for provided class name. */
  def get[T <: Throwable](name: String): GenCodec[T]
  /** Returns true, if contains GenCodec for provided class name. */
  def contains(name: String): Boolean
  /** Returns name used for class T. */
  def name[T <: Throwable](ex: T): String
}

abstract class ClassNameBasedECR extends ExceptionCodecRegistry {
  import scala.reflect._

  protected final val codecs: mutable.Map[String, GenCodec[_]] = mutable.Map.empty

  protected final val exceptionWriter: (Output, Throwable) => Unit =
    (output: Output, ex: Throwable) => if (ex.getMessage == null) output.writeNull() else output.writeString(ex.getMessage)

  protected final val exceptionReader: (Input) => String =
    (input: Input) => if (input.inputType == InputType.Null) input.readNull() else input.readString()

  register(GenCodec.create((input) => new NullPointerException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new ClassCastException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new IndexOutOfBoundsException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new ArrayIndexOutOfBoundsException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new StringIndexOutOfBoundsException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new UnsupportedOperationException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new IllegalArgumentException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new IllegalStateException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new NoSuchElementException(exceptionReader(input)), exceptionWriter))
  register(GenCodec.create((input) => new NumberFormatException(exceptionReader(input)), exceptionWriter))

  override def register[T <: Throwable : ClassTag](codec: GenCodec[T]): Unit =
    codecs(classTag[T].runtimeClass.getName) = codec

  override def get[T <: Throwable](name: String): GenCodec[T] =
    codecs(name).asInstanceOf[GenCodec[T]]

  override def contains(name: String): Boolean =
    codecs.contains(name)
}