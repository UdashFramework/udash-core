package io.udash.rpc.serialization

class DefaultExceptionCodecRegistry extends ClassNameBasedECR {
  override def name[T <: Throwable](ex: T): String =
    throw new NotImplementedError("This method is implemented only in the JVM version.")
}