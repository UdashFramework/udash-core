package io.udash
package rest.raw

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.RawJson

/**
  * Value used as encoding of [[io.udash.rest.Body Body]] parameters of
  * [[io.udash.rest.JsonBody JsonBody]] methods. Wrapped value MUST be a valid JSON.
  */
case class JsonValue(value: String) extends AnyVal
object JsonValue extends (String => JsonValue) {
  implicit val codec: GenCodec[JsonValue] = GenCodec.create(
    i => JsonValue(i.readCustom(RawJson).getOrElse(i.readSimple().readString())),
    (o, v) => if (!o.writeCustom(RawJson, v.value)) o.writeSimple().writeString(v.value)
  )
}
