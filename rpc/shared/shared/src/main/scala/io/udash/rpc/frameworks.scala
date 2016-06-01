package io.udash.rpc

import com.avsystem.commons.rpc.FunctionRPCFramework
import com.avsystem.commons.serialization._

import scala.language.postfixOps

/** Mixin for RPC framework with automatic `GenCodec` to `String` serialization. */
trait AutoUdashRPCFramework { this: UdashRPCFramework =>
  type RawValue = String

  override val RawValueCodec = implicitly[GenCodec[String]]

  override def stringToRaw(string: String): RawValue = string
  override def rawToString(raw: RawValue): String = raw
}

/** Base RPC framework for client RPC interface. This one does not allow RPC interfaces to contain methods with return type `Future[T]`. */
trait ClientUdashRPCFramework extends UdashRPCFramework {
  trait RawRPC extends GetterRawRPC with ProcedureRawRPC
}

/** Base RPC framework for server RPC interface. This one allows RPC interfaces to contain methods with return type `Future[T]`. */
trait ServerUdashRPCFramework extends UdashRPCFramework with FunctionRPCFramework {
  trait RawRPC extends GetterRawRPC with FunctionRawRPC with ProcedureRawRPC
}

/** Default Udash client application RPC framework. */
object DefaultClientUdashRPCFramework extends AutoUdashRPCFramework with ClientUdashRPCFramework with DefaultUdashSerialization
/** Default Udash server application RPC framework. */
object DefaultServerUdashRPCFramework extends AutoUdashRPCFramework with ServerUdashRPCFramework with DefaultUdashSerialization