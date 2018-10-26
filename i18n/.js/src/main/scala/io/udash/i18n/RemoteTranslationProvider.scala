package io.udash.i18n

import java.{util => ju}

import com.avsystem.commons._
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

  protected def storageKey(key: String)(implicit lang: Lang) = s"udash-i18n_${lang.lang}_$key"
  protected val cacheHashKey = "udash-i18n-cache-hash"
  protected val cacheTTLKey = "udash-i18n-cache-ttl"

  private var reloading: Future[Option[Bundle]] = _

  if (cache.isEmpty) logger.warn("RemoteTranslationProvider has no cache, so it will request server for every translation.")

  def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated] =
    fromCache(key)
      .recoverWithNow { case _ => translationsEndpoint.loadTemplate(key) }
      .recoverWithNow { case _ => Future.successful(missingTranslationError) }
      .mapNow(template => putArgs(template, argv: _*))

  private def fromCache(key: String)(implicit lang: Lang): Future[String] =
    cache match {
      case Some(storage) =>
        reloadCache(storage)
          .mapNow(_ => storage(storageKey(key)))
          .flatMapNow {
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
        reloading.mapNow(_ => true)
      case _ =>
        reloading = translationsEndpoint.loadTranslations(BundleHash(storage(storageKey(cacheHashKey)).getOrElse("")))
        reloading.mapNow {
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