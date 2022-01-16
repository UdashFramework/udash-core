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

trait BiDirectionRpcInstances[T] {
  def asServerRaw: ServerRawRpc.AsRawRpc[T]
  def asServerReal: ServerRawRpc.AsRealRpc[T]
  def serverMetadata: ServerRpcMetadata[T]
  def asClientRaw: ClientRawRpc.AsRawRpc[T]
  def asClientReal: ClientRawRpc.AsRealRpc[T]
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

abstract class BiDirectionRpcCompanion[Serialization, BiDirectionRpc](serialization: Serialization)(
  implicit instances: MacroInstances[Serialization, BiDirectionRpcInstances[BiDirectionRpc]]
) {
  implicit lazy val asServerRaw: ServerRawRpc.AsRawRpc[BiDirectionRpc] = instances(serialization, this).asServerRaw
  implicit lazy val asServerReal: ServerRawRpc.AsRealRpc[BiDirectionRpc] = instances(serialization, this).asServerReal
  implicit lazy val serverMetadata: ServerRpcMetadata[BiDirectionRpc] = instances(serialization, this).serverMetadata
  implicit lazy val asClientRaw: ClientRawRpc.AsRawRpc[BiDirectionRpc] = instances(serialization, this).asClientRaw
  implicit lazy val asClientReal: ClientRawRpc.AsRealRpc[BiDirectionRpc] = instances(serialization, this).asClientReal
}

abstract class DefaultServerRpcCompanion[ServerRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ServerRpcInstances[ServerRpc]]
) extends ServerRpcCompanion[DefaultUdashSerialization, ServerRpc](DefaultUdashSerialization)

abstract class DefaultClientRpcCompanion[ClientRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ClientRpcInstances[ClientRpc]]
) extends ClientRpcCompanion[DefaultUdashSerialization, ClientRpc](DefaultUdashSerialization)

abstract class DefaultBiDirectionRpcCompanion[BiDirectionRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, BiDirectionRpcInstances[BiDirectionRpc]]
) extends BiDirectionRpcCompanion[DefaultUdashSerialization, BiDirectionRpc](DefaultUdashSerialization)
