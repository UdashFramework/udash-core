package io.udash.i18n

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.testing.UdashFrontendTest

import scala.concurrent.Promise

class BindingsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  @silent
  implicit val ec = scalajs.concurrent.JSExecutionContext.Implicits.runNow

  implicit val provider: TranslationProvider = new LocalTranslationProvider(Map(
    Lang("en") -> Bundle(BundleHash("hash1"), Map(
      "tr1" -> "Translation {0}",
      "tr2" -> "Translation2 {1} {0}",
      "tr3" -> "Translation3 {}",
      "tr4" -> "Translation4 {1} {} {}",
      "tr5" -> "Translation5 {4}"
    )),
    Lang("pl") -> Bundle(BundleHash("hash1"), Map(
      "tr1" -> "Translation {0} pl",
      "tr2" -> "Translation2 {1} {0} pl",
      "tr3" -> "Translation3 {} pl",
      "tr4" -> "Translation4 {1} {} {} pl",
      "tr5" -> "Translation5 {4} pl"
    ))
  ), missingTranslationError = "ERROR")

  val key1 = TranslationKey.key1[String]("tr1")
  val key2 = TranslationKey.key2[Int, Double]("tr2")
  val key3 = TranslationKey.key1[Double]("tr3")
  val key4 = TranslationKey.key4[String, Int, Boolean, Double]("tr4")
  val keyX = TranslationKey.keyX("tr5")

  "translated" should {
    "put translation in DOM" in {
      val template = {
        implicit val lang = Lang("en")
        div(
          "Translation: ",
          translated(key1("test")),
          translated(key2(3, 3.14)),
          translated(key3(0.99)),
          translated(key4("test", 1, true, 3.1415)),
          translated(keyX("test", 1, true, 3.1415, "Udash".asInstanceOf[Any]))
        ).render
      }
      val template2 = {
        implicit val lang = Lang("pl")
        div(
          "Translation: ",
          translated(key1("test")),
          translated(key2(3, 3.14)),
          translated(key3(0.99)),
          translated(key4("test", 1, true, 3.1415)),
          translated(keyX("test", 1.asInstanceOf[Any], true, 3.1415, "Udash"))
        ).render
      }

      template.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
      template2.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")
    }

    "handle custom placeholder" in {
      val p: Promise[Translated] = Promise()
      val template = {
        implicit val lang = Lang("en")
        div(
          translated(p.future, Some(span("placeholder").render))
        ).render
      }

      template.textContent should be("placeholder")

      p.success(Translated("Udash"))

      template.textContent should be("Udash")
    }

    "handle None as placeholder" in {
      val p: Promise[Translated] = Promise()
      val template = {
        implicit val lang = Lang("en")
        div(
          translated(p.future, None)
        ).render
      }

      template.textContent should be("")

      p.success(Translated("Udash"))

      template.textContent should be("Udash")
    }
  }

  "translatedAttr" should {
    "put translation in DOM element attribute" in {
      val template = {
        implicit val lang = Lang("en")
        div(
          translatedAttr(key1("test"), "translation")
        ).render
      }
      val template2 = {
        implicit val lang = Lang("pl")
        div(
          translatedAttr(key1("test"), "translation")
        ).render
      }

      template.getAttribute("translation") should be("Translation test")
      template2.getAttribute("translation") should be("Translation test pl")
    }
  }

  "translatedDynamic" should {
    "put translation in DOM and update it after language change" in {
      val en = LangProperty(Lang("en"))
      val pl = LangProperty(Lang("pl"))
      val template = {
        implicit val langProperty = en
        div(
          "Translation: ",
          translatedDynamic(key1)(key => key("test")),
          translatedDynamic(key2)(key => key(3, 3.14)),
          translatedDynamic(key3)(key => key(0.99)),
          translatedDynamic(key4)(key => key("test", 1, true, 3.1415)),
          translatedDynamic(keyX)(key => key("test", 1, true, 3.1415, "Udash".asInstanceOf[Any]))
        ).render
      }
      val template2 = {
        implicit val langProperty = pl
        div(
          "Translation: ",
          translatedDynamic(key1)(key => key("test")),
          translatedDynamic(key2)(key => key(3, 3.14)),
          translatedDynamic(key3)(key => key(0.99)),
          translatedDynamic(key4)(key => key("test", 1, true, 3.1415)),
          translatedDynamic(keyX)(key => key("test", 1.asInstanceOf[Any], true, 3.1415, "Udash"))
        ).render
      }

      template.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
      template2.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")

      en.set(Lang("pl"))
      pl.set(Lang("en"))

      template.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")
      template2.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
    }
  }

  "translatedAttrDynamic" should {
    "put translation in DOM element attribute and update it after language change" in {
      val en = LangProperty(Lang("en"))
      val pl = LangProperty(Lang("pl"))
      val template = {
        implicit val langProperty = en
        div(
          translatedAttrDynamic(key1, "translation")(key => key("test"))
        ).render
      }
      val template2 = {
        implicit val langProperty = pl
        div(
          translatedAttrDynamic(key1, "translation")(key => key("test"))
        ).render
      }

      template.getAttribute("translation") should be("Translation test")
      template2.getAttribute("translation") should be("Translation test pl")

      en.set(Lang("pl"))
      pl.set(Lang("en"))

      template.getAttribute("translation") should be("Translation test pl")
      template2.getAttribute("translation") should be("Translation test")
    }
  }
}
