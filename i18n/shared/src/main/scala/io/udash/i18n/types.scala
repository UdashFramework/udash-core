package io.udash.i18n

import com.avsystem.commons.serialization.GenCodec

case class Lang(lang: String) extends AnyVal
case class BundleHash(hash: String) extends AnyVal
case class Bundle(hash: BundleHash, translations: Map[String, String])
case class Translated(string: String) extends AnyVal

object Lang {
  implicit val codec = GenCodec.auto[Lang]
}

object BundleHash {
  implicit val codec = GenCodec.auto[BundleHash]
}

object Bundle {
  implicit val codec = GenCodec.auto[Bundle]
}

object Translated {
  implicit val codec = GenCodec.auto[Translated]
}