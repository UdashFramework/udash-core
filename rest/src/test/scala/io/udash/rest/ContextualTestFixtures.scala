package io.udash
package rest

import com.avsystem.commons.rpc.AsRawReal
import io.udash.rest.openapi.{RestSchema, Schema}
import io.udash.rest.raw.PlainValue

/**
 * A data type with NO default `GenCodec` or `RestSchema`. Its REST serialization exists ONLY through
 * [[TestRestImplicits]]. This is the crux of the contextual / custom-implicits tests: any REST API that
 * mentions [[Tag]] compiles (and round-trips) only if the injected implicits bundle is actually collected
 * by the derivation macros - otherwise there is no way to (de)serialize it at all.
 */
final case class Tag(value: String)

/**
 * Custom implicits bundle: [[DefaultRestImplicits]] plus a bespoke, easily recognizable plain-value
 * serialization and schema for [[Tag]]. The `tag:` prefix on the wire is what proves the injected
 * implicit (rather than some default) was used.
 */
trait TestRestImplicits extends DefaultRestImplicits {
  implicit val tagPlainAsRealRaw: AsRawReal[PlainValue, Tag] =
    AsRawReal.create(tag => PlainValue(s"tag:${tag.value}"), pv => Tag(pv.value.stripPrefix("tag:")))
  implicit val tagSchema: RestSchema[Tag] = RestSchema.plain(Schema.String)
}
object TestRestImplicits extends TestRestImplicits

/** Sample request-scoped context threaded into contextual server APIs. */
final case class UserCtx(user: String)

/** API companion factory for non-contextual APIs using the custom [[TestRestImplicits]] bundle. */
object CustomRestApis extends RestApisWithCustomImplicits[TestRestImplicits.type](TestRestImplicits)

/** API companion factory for server-only contextual APIs with [[UserCtx]] baked in. */
object CtxRestApis extends ContextualServerRestApis[TestRestImplicits.type, UserCtx](TestRestImplicits)

/** API companion factory for contextual APIs shared between server and client. */
object CtxSharedRestApis extends ContextualServerAndClientRestApis[TestRestImplicits.type](TestRestImplicits)
