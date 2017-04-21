package io.udash.rpc.serialization

class DefaultExceptionCodecRegistry extends ClassNameBasedECR {
  override def name[T <: Throwable](ex: T): String = {
    import com.avsystem.commons._
    def find(cls: Class[_]): Opt[String] = {
      if (cls == null) Opt.Empty
      else if (codecs.contains(cls.getName)) Opt(cls.getName)
      else {
        cls.getInterfaces.iterator
          .flatMap(find)
          .filter(_.nonEmpty)
          .nextOpt
          .orElse(find(cls.getSuperclass))
      }
    }
    find(ex.getClass).getOrElse(ex.getClass.getName)
  }
}