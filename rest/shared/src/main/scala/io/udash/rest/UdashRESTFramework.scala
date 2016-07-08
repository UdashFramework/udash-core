package io.udash.rest

import com.avsystem.commons.rpc.{FunctionRPCFramework, GetterRPCFramework}

trait UdashRESTFramework extends GetterRPCFramework with FunctionRPCFramework {
  trait RawRPC extends GetterRawRPC with FunctionRawRPC

  /** Transform `String` received from HTTP response to `RawValue`. */
  def stringToRaw(string: String): RawValue
  /** Transform `RawValue` to `String` for HTTP request body. */
  def rawToString(raw: RawValue): String

  trait ValidREST[T]

  object ValidREST {
    def apply[T](implicit validREST: ValidREST[T]): ValidREST[T] = validREST
  }

  implicit def materializeValidREST[T]: ValidREST[T] = macro macros.RESTMacros.asValidRest[T]
}

