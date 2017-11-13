package io.udash.rpc

import com.avsystem.commons.rpc.{FunctionRPCFramework, RPCFramework}
import com.avsystem.commons.serialization._

import scala.language.postfixOps

trait GenCodecSerializationFramework { this: RPCFramework =>
  type Writer[T] = GenCodec[T]
  type Reader[T] = GenCodec[T]

  /** Converts value of type `T` into `RawValue`. */
  def write[T: Writer](value: T): RawValue = {
    var result: RawValue = null.asInstanceOf[RawValue]
    GenCodec.write[T](outputSerialization(result = _), value)
    result
  }

  /** Converts `RawValue` into value of type `T`. */
  def read[T: Reader](raw: RawValue): T =
    GenCodec.read[T](inputSerialization(raw))

  /** Returns `Input` for data marshalling. */
  def inputSerialization(value: RawValue): Input
  /** Returns `Output` for data unmarshalling. */
  def outputSerialization(valueConsumer: RawValue => Unit): Output
}

/** Mixin for RPC framework with automatic `GenCodec` to `String` serialization. */
trait AutoUdashRPCFramework extends GenCodecSerializationFramework { this: RPCFramework =>
  type RawValue = String

  val RawValueCodec = implicitly[GenCodec[String]]

  def stringToRaw(string: String): RawValue = string
  def rawToString(raw: RawValue): String = raw
}

/** Base RPC framework for client RPC interface. This one does not allow RPC interfaces to contain methods with return type `Future[T]`. */
trait ClientUdashRPCFramework extends UdashRPCFramework {
  trait RawRPC extends GetterRawRPC with ProcedureRawRPC
  trait FullRPCInfo[T] extends BaseFullRPCInfo[T] // for better ScalaJS DCE, MUST be separate from server framework
}

/** Base RPC framework for server RPC interface. This one allows RPC interfaces to contain methods with return type `Future[T]`. */
trait ServerUdashRPCFramework extends UdashRPCFramework with FunctionRPCFramework {
  trait RawRPC extends GetterRawRPC with FunctionRawRPC with ProcedureRawRPC
  trait FullRPCInfo[T] extends BaseFullRPCInfo[T] // for better ScalaJS DCE, MUST be separate from client framework
}

/** Default Udash client application RPC framework. */
object DefaultClientUdashRPCFramework extends AutoUdashRPCFramework with ClientUdashRPCFramework with DefaultUdashSerialization
/** Default Udash server application RPC framework. */
object DefaultServerUdashRPCFramework extends AutoUdashRPCFramework with ServerUdashRPCFramework with DefaultUdashSerialization
