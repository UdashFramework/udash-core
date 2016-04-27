package io.udash.i18n

import com.github.ghik.silencer.silent
import io.udash.testing.UdashFrontendTest
import org.scalajs.dom.ext.LocalStorage
import org.scalatest.BeforeAndAfter

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class RemoteTranslationProviderTest extends UdashFrontendTest with BeforeAndAfter {
  import Utils._

  @silent
  implicit val ec = scalajs.concurrent.JSExecutionContext.Implicits.runNow
  implicit val lang = Lang("en")

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
    LocalStorage.clear()
  }

  "RemoteTranslationProvider" should {
    "provide translations without argument" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(LocalStorage), 1 second, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash1"), Map(
        "tr1" -> "Translation",
        "tr2" -> "Translation2",
        "tr3" -> "Translation3"
      ))
      getTranslatedString(translator.translate("tr1")) should be("Translation")
      getTranslatedString(translator.translate("tr2")) should be("Translation2")
      getTranslatedString(translator.translate("tr3")) should be("Translation3")
      rpc.loadTemplateForLangCalls should be(0)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(1)
      rpc.loadTranslationsForLangCalls should be(1)
    }

    "provide translations with arguments" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(LocalStorage), 1 second, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash2"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      rpc.loadTemplateForLangCalls should be(0)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(1)
      rpc.loadTranslationsForLangCalls should be(1)
    }

    "try to reload cache after TTL" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(LocalStorage), 0 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash3"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      rpc.loadTemplateForLangCalls should be(0)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(1)

      rpc.updateTranslations(BundleHash("hash4"), Map(
        "tr1" -> "Translation {0} reloaded",
        "tr2" -> "Translation2 {1} {0} reloaded",
        "tr3" -> "Translation3 {} reloaded",
        "tr4" -> "Translation4 {1} {} {} reloaded"
      ))
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3 reloaded")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test reloaded")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8 reloaded")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2 reloaded")
      rpc.loadTemplateForLangCalls should be(1)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(2)
      rpc.loadTranslationsForLangCalls should be(10)
    }

    "not try to reload cache before TTL" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(LocalStorage), 10 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash3"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      rpc.loadTemplateForLangCalls should be(0)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(1)

      rpc.updateTranslations(BundleHash("hash4"), Map(
        "tr1" -> "Translation {0} reloaded",
        "tr2" -> "Translation2 {1} {0} reloaded",
        "tr3" -> "Translation3 {} reloaded",
        "tr4" -> "Translation4 {1} {} {} reloaded"
      ))
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      rpc.loadTemplateForLangCalls should be(1)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(2)
      rpc.loadTranslationsForLangCalls should be(1)
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
      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3 remote")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test remote")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8 remote")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2 remote")
      rpc.loadTemplateForLangCalls should be(4)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(5)
      rpc.loadTranslationsForLangCalls should be(0)
    }

    "handle languages" in {
      val rpc = new RemoteTranslationRPCMock
      val translator = new RemoteTranslationProvider(rpc, Some(LocalStorage), 10 seconds, missingTranslationError = "ERROR")

      rpc.updateTranslations(BundleHash("hash2"), Map(
        "tr1" -> "Translation {0}",
        "tr2" -> "Translation2 {1} {0}",
        "tr3" -> "Translation3 {}",
        "tr4" -> "Translation4 {1} {} {}"
      ))

      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      rpc.loadTemplateForLangCalls should be(0)
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(1)

      getTranslatedString(translator.translate("tr1", 123.3)(Lang("pl"))) should be("Translation 123.3 remotepl")
      getTranslatedString(translator.translate("tr2", "test", true)(Lang("pl"))) should be("Translation2 true test remotepl")
      getTranslatedString(translator.translate("tr3", 8)(Lang("pl"))) should be("Translation3 8 remotepl")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)(Lang("pl"))) should be("Translation4 true 1 2 remotepl")
      rpc.loadTemplateForLangCalls should be(5)
      getTranslatedString(translator.translate("trMissing")(Lang("pl"))) should be("ERROR")
      rpc.loadTemplateForLangCalls should be(6)
      rpc.loadTranslationsForLangCalls should be(2)
    }
  }

}
