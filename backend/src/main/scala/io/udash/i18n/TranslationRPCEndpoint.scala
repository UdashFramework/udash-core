package io.udash.i18n
import scala.concurrent.{ExecutionContext, Future}

/** Default implementation of [[io.udash.i18n.RemoteTranslationRPC]]. */
class TranslationRPCEndpoint(provider: TranslationTemplatesProvider)(implicit ec: ExecutionContext) extends RemoteTranslationRPC {
  override def loadTemplateForLang(lang: Lang, key: String): Future[String] = Future {
    provider.template(key)(lang)
  }

  override def loadTranslationsForLang(lang: Lang, oldHash: BundleHash): Future[Option[Bundle]] = Future {
    val hash: BundleHash = provider.langHash(lang)
    if (hash == oldHash) None
    else Some(Bundle(hash, provider.allTemplates(lang)))
  }
}
