package io.udash.rpc.serialization

import com.avsystem.commons.serialization.{GenCodec, Input, Output}

import scala.collection.mutable
import scala.reflect.ClassTag

trait ExceptionCodecRegistry {
  /** Registers GenCodec for provided class name. It can override previous value. */
  def register[T <: Throwable : ClassTag](codec: GenCodec[T], name: String): Unit
  /** Returns GenCodec for provided class name. */
  def get[T <: Throwable](name: String): GenCodec[T]
  /** Returns name used for class T. Returns `None` if there is no matched codec for the provided instance. */
  def name[T <: Throwable](ex: T): Option[String]
}

class DefaultExceptionCodecRegistry extends ExceptionCodecRegistry {

  import scala.reflect._

  protected final val codecsByClass: mutable.Map[Class[_], (String, GenCodec[_])] = mutable.Map.empty
  protected final val codecsByName: mutable.Map[String, GenCodec[_]] = mutable.Map.empty

  override def register[T <: Throwable : ClassTag](codec: GenCodec[T], name: String): Unit = {
    codecsByClass(classTag[T].runtimeClass) = (name, codec)
    codecsByName(name) = codec
  }

  override def get[T <: Throwable](name: String): GenCodec[T] = {
    codecsByName(name).asInstanceOf[GenCodec[T]]
  }

  override def name[T <: Throwable](ex: T): Option[String] = {
    codecsByClass.get(ex.getClass).map(_._1).orElse(
      codecsByClass.collectFirst { // todo some kind of priorities?
        case (cls, (name, _)) if cls.isInstance(ex) => name
      }
    )
  }

  protected final val exceptionWriter: (Output, Throwable) => Unit =
    (output: Output, ex: Throwable) =>
      if (ex.getMessage == null) output.writeNull() else output.writeSimple().writeString(ex.getMessage)

  protected final val exceptionReader: Input => String =
    (input: Input) => if (input.readNull()) null else input.readSimple().readString()

  register(GenCodec.create(input => new NullPointerException(exceptionReader(input)), exceptionWriter), "NPE")
  register(GenCodec.create(input => new ClassCastException(exceptionReader(input)), exceptionWriter), "CCE")
  register(GenCodec.create(input => new IndexOutOfBoundsException(exceptionReader(input)), exceptionWriter), "IOOBE")
  register(GenCodec.create(input => new ArrayIndexOutOfBoundsException(exceptionReader(input)), exceptionWriter), "AIOOBE")
  register(GenCodec.create(input => new StringIndexOutOfBoundsException(exceptionReader(input)), exceptionWriter), "SIOOBE")
  register(GenCodec.create(input => new UnsupportedOperationException(exceptionReader(input)), exceptionWriter), "UOE")
  register(GenCodec.create(input => new IllegalArgumentException(exceptionReader(input)), exceptionWriter), "IAE")
  register(GenCodec.create(input => new IllegalStateException(exceptionReader(input)), exceptionWriter), "ISE")
  register(GenCodec.create(input => new NoSuchElementException(exceptionReader(input)), exceptionWriter), "NSEE")
  register(GenCodec.create(input => new NumberFormatException(exceptionReader(input)), exceptionWriter), "NFE")
}