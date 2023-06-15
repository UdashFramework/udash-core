package io.udash.i18n

import io.udash.testing.AsyncUdashFrontendTest
import org.scalajs.dom
import org.scalajs.dom.Storage
import org.scalatest.BeforeAndAfter

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class RemoteTranslationProviderTest extends AsyncUdashFrontendTest with BeforeAndAfter {
  implicit val lang: Lang = Lang("en")

  private def localStorage: Storage = dom.window.localStorage

  class RemoteTranslationRPCMock extends RemoteTranslationRPC {
    private var translations: Bundle = Bundle(BundleHash(""), Map.empty)
    var loadTemplateForLangCalls = 0
    var loadTranslationsForLangCalls = 0

    def updateTranslations(hash: BundleHash, tr: Map[String, String]): Unit =
      translations = Bundle(hash, tr)

    override def loadTranslationsForLang(lang: Lang, oldHash: BundleHash): Future[Option[Bundle]] = {
      loadTranslationsForLangCalls += 1
      Future.successful(if (lang == RemoteTranslationProviderTest.this.lang) Some(translations) else Some(Bundle(oldHash, Map.empty)))
    }

    override def loadTemplateForLang(lang: Lang, key: String): Future[String] = {
      loadTemplateForLangCalls += 1
      Future { translations.translations(key) + " remote" + (if (lang != RemoteTranslationProviderTest.this.lang) lang.lang else "") }
    }
  }

  before {
    localStorage.clear()
  }

  "RemoteTranslationProvider" should {
    "provide translations without argument" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(localStorage), 1 second, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash1"), Map(
        "tr1" -> "Translation",
        "tr2" -> "Translation2",
        "tr3" -> "Translation3"
      ))

      for {
        t1 <- translator.translate("tr1")
        _ <- retrying(t1.string should be("Translation"))
        t2 <- translator.translate("tr2")
        _ <- retrying(t2.string should be("Translation2"))
        t3 <- translator.translate("tr3")
        _ <- retrying(t3.string should be("Translation3"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(0))
        t4 <- translator.translate("trMissing")
        _ <- retrying(t4.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))
        r <- retrying(rpc.loadTranslationsForLangCalls should be(1))
      } yield r
    }

    "provide translations with arguments" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(localStorage), 1 second, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash2"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      for {
        t1 <- translator.translate("tr1", 123.3)
        _ <- retrying(t1.string should be("Translation 123.3"))
        t2 <- translator.translate("tr2", "test", true)
        _ <- retrying(t2.string should be("Translation2 true test"))
        t3 <- translator.translate("tr3", 8)
        _ <- retrying(t3.string should be("Translation3 8"))
        t4 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t4.string should be("Translation4 true 1 2"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(0))
        t5 <- translator.translate("trMissing")
        _ <- retrying(t5.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))
        //error fallback in io.udash.i18n.RemoteTranslationProvider.translate can add one
        _ <- retrying(rpc.loadTranslationsForLangCalls should be > 0)
        r <- retrying(rpc.loadTranslationsForLangCalls should be < 3)
      } yield r
    }

    "try to reload cache after TTL" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(localStorage), 0 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash3"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      for {
        t1 <- translator.translate("tr1", 123.3)
        _ <- retrying(t1.string should be("Translation 123.3"))
        t2 <- translator.translate("tr2", "test", true)
        _ <- retrying(t2.string should be("Translation2 true test"))
        t3 <- translator.translate("tr3", 8)
        _ <- retrying(t3.string should be("Translation3 8"))
        t4 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t4.string should be("Translation4 true 1 2"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(0))
        t5 <- translator.translate("trMissing")
        _ <- retrying(t5.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))

        _ <- Future {
          rpc.updateTranslations(BundleHash("hash4"), Map(
            "tr1" -> "Translation {0} reloaded",
            "tr2" -> "Translation2 {1} {0} reloaded",
            "tr3" -> "Translation3 {} reloaded",
            "tr4" -> "Translation4 {1} {} {} reloaded"
          ))
        }

        t6 <- translator.translate("tr1", 123.3)
        _ <- retrying(t6.string should be("Translation 123.3 reloaded"))
        t7 <- translator.translate("tr2", "test", true)
        _ <- retrying(t7.string should be("Translation2 true test reloaded"))
        t8 <- translator.translate("tr3", 8)
        _ <- retrying(t8.string should be("Translation3 8 reloaded"))
        t9 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t9.string should be("Translation4 true 1 2 reloaded"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))
        t10 <- translator.translate("trMissing")
        _ <- retrying(t10.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(2))
        r <- retrying(rpc.loadTranslationsForLangCalls should be(10))
      } yield r
    }

    "not try to reload cache before TTL" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(localStorage), 10 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash3"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      for {
        t1 <- translator.translate("tr1", 123.3)
        _ <- retrying(t1.string should be("Translation 123.3"))
        t2 <- translator.translate("tr2", "test", true)
        _ <- retrying(t2.string should be("Translation2 true test"))
        t3 <- translator.translate("tr3", 8)
        _ <- retrying(t3.string should be("Translation3 8"))
        t4 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t4.string should be("Translation4 true 1 2"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(0))
        t5 <- translator.translate("trMissing")
        _ <- retrying(t5.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))

        _ <- Future {
          rpc.updateTranslations(BundleHash("hash4"), Map(
            "tr1" -> "Translation {0} reloaded",
            "tr2" -> "Translation2 {1} {0} reloaded",
            "tr3" -> "Translation3 {} reloaded",
            "tr4" -> "Translation4 {1} {} {} reloaded"
          ))
        }

        t6 <- translator.translate("tr1", 123.3)
        _ <- retrying(t6.string should be("Translation 123.3"))
        t7 <- translator.translate("tr2", "test", true)
        _ <- retrying(t7.string should be("Translation2 true test"))
        t8 <- translator.translate("tr3", 8)
        _ <- retrying(t8.string should be("Translation3 8"))
        t9 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t9.string should be("Translation4 true 1 2"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))
        t10 <- translator.translate("trMissing")
        _ <- retrying(t10.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(2))
        r <- retrying(rpc.loadTranslationsForLangCalls should be(1))
      } yield r
    }

    "fall back to remote calls when no cache storage provided" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, None, 1 second, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash2"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      for {
        t1 <- translator.translate("tr1", 123.3)
        _ <- retrying(t1.string should be("Translation 123.3 remote"))
        t2 <- translator.translate("tr2", "test", true)
        _ <- retrying(t2.string should be("Translation2 true test remote"))
        t3 <- translator.translate("tr3", 8)
        _ <- retrying(t3.string should be("Translation3 8 remote"))
        t4 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t4.string should be("Translation4 true 1 2 remote"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(4))
        t5 <- translator.translate("trMissing")
        _ <- retrying(t5.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(5))
        r <- retrying(rpc.loadTranslationsForLangCalls should be(0))
      } yield r
    }

    "handle languages" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(localStorage), 10 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash2"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      for {
        t1 <- translator.translate("tr1", 123.3)
        _ <- retrying(t1.string should be("Translation 123.3"))
        t2 <- translator.translate("tr2", "test", true)
        _ <- retrying(t2.string should be("Translation2 true test"))
        t3 <- translator.translate("tr3", 8)
        _ <- retrying(t3.string should be("Translation3 8"))
        t4 <- translator.translate("tr4", "test", true, 1, 2)
        _ <- retrying(t4.string should be("Translation4 true 1 2"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(0))
        t5 <- translator.translate("trMissing")
        _ <- retrying(t5.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(1))

        t6 <- translator.translate("tr1", 123.3)(Lang("pl"))
        _ <- retrying(t6.string should be("Translation 123.3 remotepl"))
        t7 <- translator.translate("tr2", "test", true)(Lang("pl"))
        _ <- retrying(t7.string should be("Translation2 true test remotepl"))
        t8 <- translator.translate("tr3", 8)(Lang("pl"))
        _ <- retrying(t8.string should be("Translation3 8 remotepl"))
        t9 <- translator.translate("tr4", "test", true, 1, 2)(Lang("pl"))
        _ <- retrying(t9.string should be("Translation4 true 1 2 remotepl"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(5))
        t10 <- translator.translate("trMissing")(Lang("pl"))
        _ <- retrying(t10.string should be("ERROR"))
        _ <- retrying(rpc.loadTemplateForLangCalls should be(6))
        r <- retrying(rpc.loadTranslationsForLangCalls should be(2))
      } yield r
    }
  }

}
