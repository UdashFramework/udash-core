package io.udash
package rpc.utils

import com.avsystem.commons.serialization.{HasGenCodec, transparent}

@transparent case class ClientId(id: String) extends AnyVal
object ClientId extends HasGenCodec[ClientId]
