package io.udash.rpc

import com.avsystem.commons.meta.MacroInstances
import io.udash.rpc.serialization.DefaultUdashSerialization

trait ServerRpcInstances[T] {
  def asRaw: ServerRawRpc.AsRawRpc[T]
  def asReal: ServerRawRpc.AsRealRpc[T]
  def metadata: ServerRpcMetadata[T]
}

trait ClientRpcInstances[T] {
  def asRaw: ClientRawRpc.AsRawRpc[T]
  def asReal: ClientRawRpc.AsRealRpc[T]
}

abstract class ServerRpcCompanion[Serialization, ServerRpc](serialization: Serialization)(
  implicit instances: MacroInstances[Serialization, ServerRpcInstances[ServerRpc]]
) {
  implicit lazy val asRaw: ServerRawRpc.AsRawRpc[ServerRpc] = instances(serialization, this).asRaw
  implicit lazy val asReal: ServerRawRpc.AsRealRpc[ServerRpc] = instances(serialization, this).asReal
  implicit lazy val metadata: ServerRpcMetadata[ServerRpc] = instances(serialization, this).metadata
}

abstract class ClientRpcCompanion[Serialization, ClientRpc](serialization: Serialization)(
  implicit instances: MacroInstances[Serialization, ClientRpcInstances[ClientRpc]]
) {
  implicit lazy val asRaw: ClientRawRpc.AsRawRpc[ClientRpc] = instances(serialization, this).asRaw
  implicit lazy val asReal: ClientRawRpc.AsRealRpc[ClientRpc] = instances(serialization, this).asReal
}

abstract class DefaultServerRpcCompanion[ServerRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ServerRpcInstances[ServerRpc]]
) extends ServerRpcCompanion[DefaultUdashSerialization, ServerRpc](DefaultUdashSerialization)

abstract class DefaultClientRpcCompanion[ClientRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ClientRpcInstances[ClientRpc]]
) extends ClientRpcCompanion[DefaultUdashSerialization, ClientRpc](DefaultUdashSerialization)
