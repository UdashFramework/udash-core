package io.udash.i18n

import io.udash.testing.UdashSharedTest

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class TranslationKeyTest extends UdashSharedTest {
  import Utils._

  implicit val lang = Lang("en")

  implicit val provider = new TranslationProvider {
    override def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated] = {
      val sb = new StringBuilder
      sb.append(key)
      sb.append(":")
      sb.append(argv.toSeq.map(_.toString).mkString(","))
      Future.successful(Translated(sb.result()))
    }

    override protected def handleMixedPlaceholders(template: String): Unit = ()
  }

  val testKey0 = TranslationKey.key("test0")
  val testKey1 = TranslationKey.key1[Int]("test1")
  val testKey2 = TranslationKey.key2[Int, String]("test2")
  val testKey3 = TranslationKey.key3[Int, String, Int]("test3")
  val testKey4 = TranslationKey.key4[Int, String, Int, String]("test4")
  val testKey5 = TranslationKey.key5[Int, String, Int, String, Int]("test5")
  val testKey6 = TranslationKey.key6[Int, String, Int, String, Int, String]("test6")
  val testKey7 = TranslationKey.key7[Int, String, Int, String, Int, String, Int]("test7")
  val testKey8 = TranslationKey.key8[Int, String, Int, String, Int, String, Int, String]("test8")
  val testKey9 = TranslationKey.key9[Int, String, Int, String, Int, String, Int, String, Int]("test9")
  val testKeyX = TranslationKey.keyX("testX")

  "TranslationKey" should {
    "obtain translation from provider" in {
      getTranslatedString(testKey0()) should be("test0:")
      getTranslatedString(testKey1(1)) should be("test1:1")
      getTranslatedString(testKey2(1, "2")) should be("test2:1,2")
      getTranslatedString(testKey3(1, "2", 3)) should be("test3:1,2,3")
      getTranslatedString(testKey4(1, "2", 3, "4")) should be("test4:1,2,3,4")
      getTranslatedString(testKey5(1, "2", 3, "4", 5)) should be("test5:1,2,3,4,5")
      getTranslatedString(testKey6(1, "2", 3, "4", 5, "6")) should be("test6:1,2,3,4,5,6")
      getTranslatedString(testKey7(1, "2", 3, "4", 5, "6", 7)) should be("test7:1,2,3,4,5,6,7")
      getTranslatedString(testKey8(1, "2", 3, "4", 5, "6", 7, "8")) should be("test8:1,2,3,4,5,6,7,8")
      getTranslatedString(testKey9(1, "2", 3, "4", 5, "6", 7, "8", 9)) should be("test9:1,2,3,4,5,6,7,8,9")
      getTranslatedString(testKeyX(1, 2, "3", 4.5)) should be("testX:1,2,3,4.5")
      getTranslatedString(testKeyX((1, 2, "3", 4.5))) should be("testX:(1,2,3,4.5)")
    }

    "compile only with valid types" in {
      "testKey1(1)" should compile
      "testKey1(\"1\")" shouldNot typeCheck
      "testKey1(1.5)" shouldNot typeCheck
    }
  }
}
