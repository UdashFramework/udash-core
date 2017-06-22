package io.udash.rest

import com.avsystem.commons.rpc.{FunctionRPCFramework, GetterRPCFramework}

trait UdashRESTFramework extends GetterRPCFramework with FunctionRPCFramework {
  trait RawRPC extends GetterRawRPC with FunctionRawRPC

  type ParamTypeMetadata[T] = SimplifiedType[T]

  /** Transform `String` received from HTTP response to `RawValue`. */
  def stringToRaw(string: String): RawValue
  /** Transform `RawValue` to `String` for HTTP request body. */
  def rawToString(raw: RawValue): String

  trait ValidREST[T]

  object ValidREST {
    def apply[T](implicit validREST: ValidREST[T]): ValidREST[T] = validREST
  }

  implicit def materializeValidREST[T]: ValidREST[T] = macro macros.RESTMacros.asValidRest[T]

  trait ValidServerREST[T]

  object ValidServerREST {
    def apply[T](implicit validREST: ValidServerREST[T]): ValidServerREST[T] = validREST
  }

  implicit def materializeValidServerREST[T]: ValidServerREST[T] = macro macros.RESTMacros.asValidServerRest[T]

  sealed trait SimplifiedType[T]
  object SimplifiedType {
    implicit object DoubleType extends SimplifiedType[Double]
    implicit object FloatType extends SimplifiedType[Float]
    implicit object LongType extends SimplifiedType[Long]
    implicit object IntType extends SimplifiedType[Int]
    implicit object CharType extends SimplifiedType[Char]
    implicit object ShortType extends SimplifiedType[Short]
    implicit object ByteType extends SimplifiedType[Byte]
    implicit object StringType extends SimplifiedType[String]
    class AnyRefSimplifiedType[T <: AnyRef] extends SimplifiedType[T]
    implicit def anyRefSimplifiedType[T <: AnyRef]: SimplifiedType[T] = new AnyRefSimplifiedType[T]
  }
}