package io.udash.i18n

import io.udash.testing.UdashFrontendTest

class LocalTranslationProviderTest extends UdashFrontendTest {
  import Utils._

  implicit val lang = Lang("en")

  "LocalTranslationProvider" should {
    "provide translations without argument" in {
      val translator = new LocalTranslationProvider(
        Map(
          Lang("en") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation",
            "tr2" -> "Translation2",
            "tr3" -> "Translation3"
          ))
        ), missingTranslationError = "ERROR"
      )

      getTranslatedString(translator.translate("tr1")) should be("Translation")
      getTranslatedString(translator.translate("tr2")) should be("Translation2")
      getTranslatedString(translator.translate("tr3")) should be("Translation3")
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
    }

    "provide translations with arguments" in {
      val translator = new LocalTranslationProvider(
        Map(
          Lang("en") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation {0}",
            "tr2" -> "Translation2 {1} {0}",
            "tr3" -> "Translation3 {}",
            "tr4" -> "Translation4 {1} {} {}"
          ))
        ), missingTranslationError = "ERROR"
      )

      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")
    }

    "handle languages" in {
      val translator = new LocalTranslationProvider(
        Map(
          Lang("en") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation {0}",
            "tr2" -> "Translation2 {1} {0}",
            "tr3" -> "Translation3 {}",
            "tr4" -> "Translation4 {1} {} {}"
          )),
          Lang("pl") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation {0} pl",
            "tr2" -> "Translation2 {1} {0} pl",
            "tr3" -> "Translation3 {} pl",
            "tr4" -> "Translation4 {1} {} {} pl"
          ))
        ), missingTranslationError = "ERROR"
      )

      getTranslatedString(translator.translate("tr1", 123.3)) should be("Translation 123.3")
      getTranslatedString(translator.translate("tr2", "test", true)) should be("Translation2 true test")
      getTranslatedString(translator.translate("tr3", 8)) should be("Translation3 8")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)) should be("Translation4 true 1 2")
      getTranslatedString(translator.translate("trMissing")) should be("ERROR")

      getTranslatedString(translator.translate("tr1", 123.3)(Lang("pl"))) should be("Translation 123.3 pl")
      getTranslatedString(translator.translate("tr2", "test", true)(Lang("pl"))) should be("Translation2 true test pl")
      getTranslatedString(translator.translate("tr3", 8)(Lang("pl"))) should be("Translation3 8 pl")
      getTranslatedString(translator.translate("tr4", "test", true, 1, 2)(Lang("pl"))) should be("Translation4 true 1 2 pl")
      getTranslatedString(translator.translate("trMissing")(Lang("pl"))) should be("ERROR")
    }
  }

}
