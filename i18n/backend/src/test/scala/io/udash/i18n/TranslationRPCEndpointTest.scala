package io.udash.i18n

import io.udash.testing.UdashRpcBackendTest

import scala.util.{Failure, Success}

class TranslationRPCEndpointTest extends UdashRpcBackendTest {
  import scala.concurrent.ExecutionContext.Implicits.global

  val provider = new TranslationTemplatesProvider {
    private def templates(implicit lang: Lang) = Map(
      "tk1" -> ("template 1" + lang.lang),
      "tk2" -> ("template 2 {}" + lang.lang),
      "tk3" -> ("template {1} 3 {0}" + lang.lang)
    )

    override def allTemplates(implicit lang: Lang): Map[String, String] =
      templates

    override def template(key: String)(implicit lang: Lang): String =
      templates(lang)(key)

    override def langHash(implicit lang: Lang): BundleHash =
      hash(allTemplates)
  }

  val endpoint = new TranslationRPCEndpoint(provider)

  "TranslationRPCEndpoint" should {
    "provide single templates" in {
      val t1 = endpoint.loadTemplate("tk1")(Lang("en"))
      val t2 = endpoint.loadTemplate("tk2")(Lang("pl"))
      val t3 = endpoint.loadTemplate("tk3")(Lang("en"))

      eventually {
        t1.value.get.get should be("template 1en")
        t2.value.get.get should be("template 2 {}pl")
        t3.value.get.get should be("template {1} 3 {0}en")
      }
    }

    "provide bundle of templates" in {
      val translationsEN = endpoint.loadTranslations(BundleHash(""))(Lang("en"))
      val translationsPL = endpoint.loadTranslations(BundleHash(""))(Lang("pl"))

      eventually {
        val bundleEN = translationsEN.value.get.get.get
        bundleEN.translations("tk1") should be("template 1en")
        bundleEN.translations("tk2") should be("template 2 {}en")
        bundleEN.translations("tk3") should be("template {1} 3 {0}en")

        val bundlePL = translationsPL.value.get.get.get
        bundlePL.translations("tk1") should be("template 1pl")
        bundlePL.translations("tk2") should be("template 2 {}pl")
        bundlePL.translations("tk3") should be("template {1} 3 {0}pl")
      }
    }

    "return templates bundle only when oldHash is not up to date" in {
      val translationsEN = endpoint.loadTranslations(BundleHash(""))(Lang("en"))

      eventually {
        val bundleEN = translationsEN.value.get.get.get
        val reloading = endpoint.loadTranslations(bundleEN.hash)(Lang("en"))
        eventually {
          reloading.value should be(Some(Success(None)))
        }
        val anotherLang = endpoint.loadTranslations(bundleEN.hash)(Lang("pl"))
        eventually {
          val bundlePL = anotherLang.value.get.get.get
          bundlePL.translations("tk1") should be("template 1pl")
          bundlePL.translations("tk2") should be("template 2 {}pl")
          bundlePL.translations("tk3") should be("template {1} 3 {0}pl")
        }
      }
    }
  }
}
