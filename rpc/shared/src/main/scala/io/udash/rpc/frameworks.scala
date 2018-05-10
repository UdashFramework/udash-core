package io.udash.rpc

import com.avsystem.commons.rpc.{FunctionRPCFramework, RPCFramework}
import com.avsystem.commons.serialization._
import io.udash.rpc.serialization.DefaultUdashSerialization

trait GenCodecSerializationFramework { this: RPCFramework =>
  override type Writer[T] = GenCodec[T]
  override type Reader[T] = GenCodec[T]
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
object DefaultClientUdashRPCFramework extends ClientUdashRPCFramework with DefaultUdashSerialization

/** Default Udash server application RPC framework. */
object DefaultServerUdashRPCFramework extends ServerUdashRPCFramework with DefaultUdashSerialization
