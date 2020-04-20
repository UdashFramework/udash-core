package io.udash.i18n

import com.avsystem.commons.misc.{AbstractCase, CaseMethods}
import com.avsystem.commons.serialization.{HasGenCodec, transparent}

@transparent final case class Lang(lang: String) extends AnyVal with CaseMethods
object Lang extends HasGenCodec[Lang]

@transparent final case class BundleHash(hash: String) extends AnyVal with CaseMethods
object BundleHash extends HasGenCodec[BundleHash]

final case class Bundle(hash: BundleHash, translations: Map[String, String]) extends AbstractCase
object Bundle extends HasGenCodec[Bundle]

@transparent final case class Translated(string: String) extends AnyVal with CaseMethods
object Translated extends HasGenCodec[Translated]