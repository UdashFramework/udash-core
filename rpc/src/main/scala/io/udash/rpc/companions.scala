package io.udash.rpc

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.ValueOf
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

abstract class ServerRpcCompanion[Deps, ServerRpc](deps: Deps)(
  implicit instances: MacroInstances[Deps, ServerRpcInstances[ServerRpc]]
) {
  implicit lazy val asRaw: ServerRawRpc.AsRawRpc[ServerRpc] = instances(deps, this).asRaw
  implicit lazy val asReal: ServerRawRpc.AsRealRpc[ServerRpc] = instances(deps, this).asReal
  implicit lazy val metadata: ServerRpcMetadata[ServerRpc] = instances(deps, this).metadata
}

abstract class ClientRpcCompanion[Deps, ClientRpc](deps: Deps)(
  implicit instances: MacroInstances[Deps, ClientRpcInstances[ClientRpc]]
) {
  implicit lazy val asRaw: ClientRawRpc.AsRawRpc[ClientRpc] = instances(deps, this).asRaw
  implicit lazy val asReal: ClientRawRpc.AsRealRpc[ClientRpc] = instances(deps, this).asReal
}

abstract class DefaultServerRpcCompanion[ServerRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ServerRpcInstances[ServerRpc]]
) extends ServerRpcCompanion[DefaultUdashSerialization, ServerRpc](DefaultUdashSerialization)

abstract class DefaultServerRpcCompanionWithDeps[Deps, ServerRpc](implicit
  deps: ValueOf[Deps],
  instances: MacroInstances[(Deps, DefaultUdashSerialization), ServerRpcInstances[ServerRpc]],
) extends ServerRpcCompanion[(Deps, DefaultUdashSerialization), ServerRpc](deps.value -> DefaultUdashSerialization)

abstract class DefaultClientRpcCompanion[ClientRpc](
  implicit instances: MacroInstances[DefaultUdashSerialization, ClientRpcInstances[ClientRpc]]
) extends ClientRpcCompanion[DefaultUdashSerialization, ClientRpc](DefaultUdashSerialization)

abstract class DefaultClientRpcCompanionWithDeps[Deps, ClientRpc](implicit
  deps: ValueOf[Deps],
  instances: MacroInstances[(Deps, DefaultUdashSerialization), ClientRpcInstances[ClientRpc]]
) extends ClientRpcCompanion[(Deps, DefaultUdashSerialization), ClientRpc](deps.value -> DefaultUdashSerialization)