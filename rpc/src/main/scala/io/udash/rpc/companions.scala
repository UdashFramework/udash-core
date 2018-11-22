package io.udash.rpc

import com.avsystem.commons.meta.MacroInstances
import io.udash.rpc.serialization.DefaultUdashSerialization

trait RpcInstances[T] {
  def asRaw: RawRpc.AsRawRpc[T]
  def asReal: RawRpc.AsRealRpc[T]
  def metadata: RpcMetadata[T]
}

abstract class RpcCompanion[Serialization, RpcApi](serialization: Serialization)(
  implicit instances: MacroInstances[Serialization, RpcInstances[RpcApi]]
) {
  implicit lazy val asRaw: RawRpc.AsRawRpc[RpcApi] = instances(serialization, this).asRaw
  implicit lazy val asReal: RawRpc.AsRealRpc[RpcApi] = instances(serialization, this).asReal
  implicit lazy val metadata: RpcMetadata[RpcApi] = instances(serialization, this).metadata
}

abstract class DefaultRpcCompanion[ServerRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, RpcInstances[ServerRpc]]
) extends RpcCompanion[DefaultUdashSerialization, ServerRpc](DefaultUdashSerialization)
