package io.udash.i18n

/** Server-side translations provider. */
trait TranslationTemplatesProvider {
  /** Returns translation template for provided `key` and `lang`. */
  def template(key: String)(implicit lang: Lang): String

  /** Returns all translation templates for provided `lang`. */
  def allTemplates(implicit lang: Lang): Map[String, String]

  /** Returns `true` if provided translations `hash` is up to date. */
  def langHash(implicit lang: Lang): BundleHash

  protected def hash(data: Map[String, String]): BundleHash =
    BundleHash(new String(
      java.security.MessageDigest.getInstance("MD5")
        .digest(
          data.map {
            case (key, value) => key + value
          }.mkString.getBytes
        )
    ))
}

