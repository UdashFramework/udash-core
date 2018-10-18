package io.udash.i18n

import com.avsystem.commons.serialization.{GenCodec, transparent}

@transparent case class Lang(lang: String) extends AnyVal
object Lang {
  implicit val codec: GenCodec[Lang] = GenCodec.materialize[Lang]
}
@transparent case class BundleHash(hash: String) extends AnyVal
object BundleHash {
  implicit val codec: GenCodec[BundleHash] = GenCodec.materialize[BundleHash]
}
case class Bundle(hash: BundleHash, translations: Map[String, String])
object Bundle {
  implicit val codec: GenCodec[Bundle] = GenCodec.materialize[Bundle]
}
@transparent case class Translated(string: String) extends AnyVal
object Translated {
  implicit val codec: GenCodec[Translated] = GenCodec.materialize[Translated]
}
