package io.udash.i18n

import java.{util => ju}

import org.scalajs.dom.ext.Storage

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js
import scala.util.Try

/**
  * TranslationProvider dedicated to applications using RPC system.
  *
  * @param translationsEndpoint RPC endpoint serving translations.
  * @param cache Optional `org.scalajs.dom.ext.Storage`, it will be used as translations cache.
  * @param ttl Time period between translations refresh, if using `cache`.
  * @param missingTranslationError This text will be used in place of missing translations.
  */
class RemoteTranslationProvider(translationsEndpoint: RemoteTranslationRPC,
                                cache: Option[Storage], ttl: FiniteDuration,
                                missingTranslationError: String = "Missing translation")
  extends FrontendTranslationProvider {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  protected def storageKey(key: String)(implicit lang: Lang) = s"udash-i18n_${lang.lang}_$key"
  protected val cacheHashKey = "udash-i18n-cache-hash"
  protected val cacheTTLKey = "udash-i18n-cache-ttl"

  private var reloading: Future[Option[Bundle]] = null

  if (cache.isEmpty) logger.warn("RemoteTranslationProvider has no cache, so it will request server for every translation.")

  def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated] =
    fromCache(key)
      .recoverWith { case _ => translationsEndpoint.loadTemplate(key) }
      .recoverWith { case _ => Future.successful(missingTranslationError) }
      .map(template => putArgs(template, argv:_*))

  private def fromCache(key: String)(implicit lang: Lang): Future[String] =
    cache match {
      case Some(storage) =>
        reloadCache(storage)
          .map(_ => storage(storageKey(key)))
          .flatMap {
            case Some(translationString) =>
              Future.successful(translationString)
            case None =>
              logger.warn(s"Key $key not found in cache!")
              Future.failed(new IllegalArgumentException(s"Key $key not found in cache!"))
          }
      case None =>
        Future.failed(new UnsupportedOperationException)
    }

  /** Reloads cache if needed. Return true if cache was reloaded. */
  private def reloadCache(storage: Storage)(implicit lang: Lang): Future[Boolean] = {
    def isCacheValid(timestamp: String): Boolean =
      Try(timestamp.toLong > now()).getOrElse(false)

    storage(storageKey(cacheTTLKey)) match {
      case Some(value) if isCacheValid(value) =>
        Future.successful(false)
      case _ if reloading != null =>
        reloading.map(_ => true)
      case _ =>
        reloading = translationsEndpoint.loadTranslations(BundleHash(storage(storageKey(cacheHashKey)).getOrElse("")))
        reloading.map {
          case Some(Bundle(hash, translations)) =>
            translations.foreach {
              case (key, value) =>
                storage(storageKey(key)) = value
            }
            storage(storageKey(cacheHashKey)) = hash.hash
            storage(storageKey(cacheTTLKey)) = (js.Date.now() + ttl.fromNow.timeLeft.toMillis).toString
            reloading = null
            true
          case None =>
            storage(storageKey(cacheTTLKey)) = (js.Date.now() + ttl.fromNow.timeLeft.toMillis).toString
            reloading = null
            true
        }
    }
  }

  private def now() = new ju.Date().getTime
}