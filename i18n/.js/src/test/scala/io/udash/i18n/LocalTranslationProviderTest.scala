package io.udash.i18n

import io.udash.testing.UdashFrontendTest

class LocalTranslationProviderTest extends UdashFrontendTest {
  import Utils._

  implicit val lang: Lang = Lang("en")

  "LocalTranslationProvider" should {
    "provide translations without argument" in {
      implicit val translator = new LocalTranslationProvider(
        Map(
          Lang("en") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation",
            "tr2" -> "Translation2",
            "tr3" -> "Translation3"
          ))
        ), missingTranslationError = "ERROR"
      )

      getTranslatedString(TranslationKey.key("tr1")) should be("Translation")
      getTranslatedString(TranslationKey.key("tr2")) should be("Translation2")
      getTranslatedString(TranslationKey.key("tr3")) should be("Translation3")
      getTranslatedString(TranslationKey.key("trMissing")) should be("ERROR")
    }

    "provide translations with arguments" in {
      implicit val translator = new LocalTranslationProvider(
        Map(
          Lang("en") -> Bundle(BundleHash("hash1"), Map(
            "tr1" -> "Translation {0}",
            "tr2" -> "Translation2 {1} {0}",
            "tr3" -> "Translation3 {}",
            "tr4" -> "Translation4 {1} {} {}"
          ))
        ), missingTranslationError = "ERROR"
      )

      getTranslatedString(TranslationKey.key1("tr1")(123.3)) should be("Translation 123.3")
      getTranslatedString(TranslationKey.key2("tr2")("test", true)) should be("Translation2 true test")
      getTranslatedString(TranslationKey.key1("tr3")(8)) should be("Translation3 8")
      getTranslatedString(TranslationKey.key4("tr4")("test", true, 1, 2)) should be("Translation4 true 1 2")
      getTranslatedString(TranslationKey.key("trMissing")) should be("ERROR")
    }

    "handle languages" in {
      implicit val translator = new LocalTranslationProvider(
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

      getTranslatedString(TranslationKey.key1("tr1")(123.3)) should be("Translation 123.3")
      getTranslatedString(TranslationKey.key2("tr2")("test", true)) should be("Translation2 true test")
      getTranslatedString(TranslationKey.key1("tr3")(8)) should be("Translation3 8")
      getTranslatedString(TranslationKey.key4("tr4")("test", true, 1, 2)) should be("Translation4 true 1 2")
      getTranslatedString(TranslationKey.key("trMissing")) should be("ERROR")

      {
        implicit val lang = Lang("pl")
        getTranslatedString(TranslationKey.key1("tr1")(123.3)) should be("Translation 123.3 pl")
        getTranslatedString(TranslationKey.key2("tr2")("test", true)) should be("Translation2 true test pl")
        getTranslatedString(TranslationKey.key1("tr3")(8)) should be("Translation3 8 pl")
        getTranslatedString(TranslationKey.key4("tr4")("test", true, 1, 2)) should be("Translation4 true 1 2 pl")
        getTranslatedString(TranslationKey.key("trMissing")) should be("ERROR")
      }
    }
  }

}
