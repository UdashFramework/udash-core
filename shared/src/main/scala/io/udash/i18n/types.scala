package io.udash.i18n

case class Lang(lang: String) extends AnyVal
case class BundleHash(hash: String) extends AnyVal
case class Bundle(hash: BundleHash, translations: Map[String, String])
case class Translated(string: String) extends AnyVal