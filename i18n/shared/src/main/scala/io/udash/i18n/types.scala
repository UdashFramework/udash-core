package io.udash.i18n

import com.avsystem.commons.serialization.transparent

@transparent case class Lang(lang: String) extends AnyVal
@transparent case class BundleHash(hash: String) extends AnyVal
case class Bundle(hash: BundleHash, translations: Map[String, String])
@transparent case class Translated(string: String) extends AnyVal