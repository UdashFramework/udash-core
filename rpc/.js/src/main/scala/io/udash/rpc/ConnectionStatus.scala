package io.udash.rpc

import com.avsystem.commons.misc.{AbstractValueEnum, EnumCtx, ValueEnumCompanion}

sealed class ConnectionStatus(implicit enumCtx: EnumCtx) extends AbstractValueEnum
object ConnectionStatus extends ValueEnumCompanion[ConnectionStatus] {
  final val Open, Closed: Value = new ConnectionStatus
}

