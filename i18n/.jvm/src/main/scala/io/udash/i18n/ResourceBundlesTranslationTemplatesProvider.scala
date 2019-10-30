package io.udash.i18n

import java.{util => ju}

import com.avsystem.commons._

/** Loads translations from provided `java.util.ResourceBundles`. */
class ResourceBundlesTranslationTemplatesProvider(bundles: Map[Lang, Seq[ju.ResourceBundle]]) extends TranslationTemplatesProvider {
  private val cache: MMap[Lang, Bundle] = MMap.empty

  override def template(key: String)(implicit lang: Lang): String =
    bundle.translations(key)

  override def allTemplates(implicit lang: Lang): Map[String, String] =
    bundle.translations

  override def langHash(implicit lang: Lang): BundleHash =
    bundle.hash

  private def bundle(implicit lang: Lang): Bundle = {
    def parseBundles(bundles: Seq[ju.ResourceBundle]): Bundle = {
      val templates = bundles
        .flatMap(resource =>
          resource.getKeys.asScala.map(k => {
            val template: String = resource.getString(k)

            if (TranslationProvider.indexedArgRegex.findFirstIn(template).nonEmpty &&
                TranslationProvider.unindexedArgRegex.findFirstIn(template).nonEmpty)
              throw IndexedAndUnindexedPlaceholdersMixed(template)

            k -> template
          })
        )
        .toMap
      Bundle(hash(templates), templates)
    }

    cache.getOrElseUpdate(lang, parseBundles(bundles.getOrElse(lang, Seq.empty)))
  }

  case class IndexedAndUnindexedPlaceholdersMixed(template: String) extends RuntimeException
}
