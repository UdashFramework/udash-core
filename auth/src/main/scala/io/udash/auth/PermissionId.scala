package io.udash.auth

import com.avsystem.commons.serialization.{HasGenCodec, transparent}

@transparent
final case class PermissionId(value: String) extends AnyVal
object PermissionId extends HasGenCodec[PermissionId]
