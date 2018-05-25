package io.udash.i18n

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

import scala.concurrent.{Future, Promise}

class BindingsTest extends AsyncUdashFrontendTest {
  import scalatags.JsDom.all._

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

      for {
        _ <- retrying {
          template.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
        }
        r <- retrying {
          template2.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")
        }
      } yield r
    }

    "handle custom placeholder" in {
      val p: Promise[Translated] = Promise()
      val template = {
        div(
          translated(p.future, Some(span("placeholder").render))
        ).render
      }

      for {
        _ <- retrying {
          template.textContent should be("placeholder")
        }
        _ <- Future {
          p.success(Translated("Udash"))
        }
        r <- retrying {
          template.textContent should be("Udash")
        }
      } yield r
    }

    "handle None as placeholder" in {
      val p: Promise[Translated] = Promise()
      val template = {
        div(
          translated(p.future, None)
        ).render
      }

      for {
        _ <- retrying {
          template.textContent should be("")
        }
        _ <- Future {
          p.success(Translated("Udash"))
        }
        r <- retrying {
          template.textContent should be("Udash")
        }
      } yield r
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

      for {
        _ <- retrying {
          template.getAttribute("translation") should be("Translation test")
        }
        r <- retrying {
          template2.getAttribute("translation") should be("Translation test pl")
        }
      } yield r
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

      for {
        _ <- retrying {
          template.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
          template2.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")
        }
        _ <- Future {
          en.set(Lang("pl"))
          pl.set(Lang("en"))
        }
        r <- retrying {
          template.textContent should be("Translation: Translation test plTranslation2 3.14 3 plTranslation3 0.99 plTranslation4 1 true 3.1415 plTranslation5 Udash pl")
          template2.textContent should be("Translation: Translation testTranslation2 3.14 3Translation3 0.99Translation4 1 true 3.1415Translation5 Udash")
        }
      } yield r
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

      for {
        _ <- retrying {
          template.getAttribute("translation") should be("Translation test")
          template2.getAttribute("translation") should be("Translation test pl")
        }
        _ <- Future {
          en.set(Lang("pl"))
          pl.set(Lang("en"))
        }
        r <- retrying {
          template.getAttribute("translation") should be("Translation test pl")
          template2.getAttribute("translation") should be("Translation test")
        }
      } yield r
    }
  }

  "TranslationKey" should {
    "be valid Property value" in {
      val en = Lang("en")
      val pl = Lang("pl")
      implicit val lang = LangProperty(en)
      val pKey1 = TranslationKey.key1[String]("tr1")
      val pKey2 = TranslationKey.key1[String]("tr3")
      val pKey3 = TranslationKey.key1[String]("tr1")
      val pKey4 = TranslationKey.key1[String]("tr3")

      val translations = SeqProperty[TranslationKey1[String]](pKey1, pKey2, pKey3, pKey4)

      val el = div(
        repeat(translations)(key =>
          span(translatedDynamic(key.get)(k => k("test"))).render
        )
      ).render

      for {
        _ <- retrying {
          el.textContent should be("Translation testTranslation3 testTranslation testTranslation3 test")
        }
        _ <- Future {
          lang.set(pl)
        }
        _ <- retrying {
          el.textContent should be("Translation test plTranslation3 test plTranslation test plTranslation3 test pl")
        }
        _ <- Future {
          lang.set(en)
        }
        _ <- retrying {
          el.textContent should be("Translation testTranslation3 testTranslation testTranslation3 test")
        }
        _ <- Future {
          translations.append(pKey1)
        }
        _ <- retrying {
          el.textContent should be("Translation testTranslation3 testTranslation testTranslation3 testTranslation test")
        }
        _ <- Future {
          lang.set(pl)
        }
        _ <- retrying {
          el.textContent should be("Translation test plTranslation3 test plTranslation test plTranslation3 test plTranslation test pl")
        }
        _ <- Future {
          lang.set(en)
        }
        _ <- retrying {
          el.textContent should be("Translation testTranslation3 testTranslation testTranslation3 testTranslation test")
        }
        _ <- Future {
          translations.remove(1, 3)
        }
        _ <- retrying {
          el.textContent should be("Translation testTranslation test")
        }
        _ <- Future {
          lang.set(pl)
        }
        r <- retrying {
          el.textContent should be("Translation test plTranslation test pl")
        }
      } yield r
    }

    "work with reduced keys in property" in {
      implicit val en = Lang("en")
      val p: Property[TranslationKey0] = Property[TranslationKey0](null: TranslationKey0)
      val key1 = TranslationKey.key1[String]("tr1")
      p.set(key1.reduce("test"))
      retrying(p.get.apply().value.get.get.string should be("Translation test"))
    }

    "work with keys in property" in {
      implicit val en = Lang("en")
      val p: Property[TranslationKey] = Property[TranslationKey](null: TranslationKey)
      val key1 = TranslationKey.key1[String]("tr1")
      p.set(key1)

      for {
        _ <- retrying {
          (p.get match {
            case _: TranslationKey1[_] => true
            case _ => false
          }) should be(true)
        }
        _ <- Future {
          p.set(key1.reduce("asd"))
        }
        r <- retrying {
          (p.get match {
            case key: TranslationKey0 => key.apply().value.get.get.string
            case _ => "false"
          }) should be("Translation asd")
        }
      } yield r
    }
  }
}
