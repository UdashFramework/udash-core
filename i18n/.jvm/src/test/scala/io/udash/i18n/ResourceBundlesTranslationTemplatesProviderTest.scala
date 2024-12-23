package io.udash.i18n

import io.udash.testing.UdashRpcBackendTest

import java.util as ju

class ResourceBundlesTranslationTemplatesProviderTest extends UdashRpcBackendTest {
  val testBundlesNames = Seq("test_translations", "test2_translations")
  val bundles = Seq("en", "pl")
    .map(lang => Lang(lang) -> testBundlesNames.map(name => ju.ResourceBundle.getBundle(name, new ju.Locale.Builder().setLanguage(lang).build())))
    .toMap

  val provider = new ResourceBundlesTranslationTemplatesProvider(bundles)

  "ResourceBundlesTranslationTemplatesProvider" should {
    "provide translations from ResourceBundles" in {
      provider.template("test1.key1")(Lang("en")) should be("Test Key 1")
      provider.template("test1.key1")(Lang("pl")) should be("Klucz testowy 1")
      provider.template("test1.key2")(Lang("en")) should be("Test Key 2")
      provider.template("test1.key2")(Lang("pl")) should be("Klucz testowy 2")
      provider.template("test2.key1")(Lang("en")) should be("Key 1")
      provider.template("test2.key1")(Lang("pl")) should be("Klucz 1")
      provider.template("test2.key2")(Lang("en")) should be("Key 2")
      provider.template("test2.key2")(Lang("pl")) should be("Klucz 2")
    }

    "provide one bundle from all ResourceBundles" in {
      val en = provider.allTemplates(Lang("en"))
      val pl = provider.allTemplates(Lang("pl"))

      en.size should be(4)
      pl.size should be(4)
    }

    "provide bundle hash" in {
      provider.langHash(Lang("en")) should be(provider.langHash(Lang("en")))
      provider.langHash(Lang("pl")) should be(provider.langHash(Lang("pl")))
      provider.langHash(Lang("en")) shouldNot be(provider.langHash(Lang("pl")))
    }

    "throw an exception when mixed placeholders occurs" in {
      val mixedProvider = new ResourceBundlesTranslationTemplatesProvider(Map(Lang("en") -> Seq(ju.ResourceBundle.getBundle("mixed", new ju.Locale.Builder().setLanguage("en").build()))))
      intercept[ResourceBundlesTranslationTemplatesProvider#IndexedAndUnindexedPlaceholdersMixed](
        mixedProvider.langHash(Lang("en")) should be(provider.langHash(Lang("en")))
      )
    }
  }
}
