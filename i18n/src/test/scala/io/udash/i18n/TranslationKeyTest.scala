package io.udash.i18n

import com.avsystem.commons.serialization.GenCodec
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.JsonStr
import io.udash.testing.UdashSharedTest

import scala.concurrent.Future

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

  def write[T: GenCodec](value: T): JsonStr =
    JsonStr(JsonStringOutput.write(value))

  def read[T: GenCodec](jsonStr: JsonStr): T =
    JsonStringInput.read[T](jsonStr.json)

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
  val testKeyU = TranslationKey.untranslatable("testUntranslatable")

  "Test template placeholders substitution" in {
    implicit val provider: TranslationProvider = new TranslationProvider {
      override def translate(key: String, argv: Any*)(implicit lang: Lang): Future[Translated] = {
        Future.successful(putArgs(key, argv: _*))
      }

      override protected def handleMixedPlaceholders(template: String): Unit = ()
    }

    //escape regex chars in replacement (actually: putArgs test)
    val plainKey = TranslationKey.key1[String]("This is {}")
    getTranslatedString(plainKey("plain string")) should be("This is plain string")
    getTranslatedString(plainKey("${foo}")) should be("This is ${foo}")
    getTranslatedString(plainKey("<([{\\^-=$!|]})?*+.>")) should be("This is <([{\\^-=$!|]})?*+.>") //regex special chars

    //indexed template
    val indexedKey = TranslationKey.key3[Int, Int, Int]("This is {2} {1} {0}")
    getTranslatedString(indexedKey(1,2,3)) should be("This is 3 2 1")

    //mixed templates are actually unhandled
    val mixedKey = TranslationKey.key3[Int, Int, Int]("This is {2} {} {0}")
    getTranslatedString(mixedKey(1,2,3)) should be("This is 3 {} 1")
  }

  "TranslationKey" should {
    "obtain translation from provider" in {
      getTranslatedString(testKey0) should be("test0:")
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
      getTranslatedString(testKeyU) should be("testUntranslatable")
    }

    "compile only with valid types" in {
      "testKey1(1)" should compile
      "testKey1(\"1\")" shouldNot typeCheck
      "testKey1(1.5)" shouldNot typeCheck
    }

    "serialize and deserialize TranslationKey0" in {
      val serialized = write(testKey0)
      val deserialized = read[TranslationKey0](serialized)
      getTranslatedString(deserialized) should be(getTranslatedString(testKey0))
    }

    "serialize and deserialize Untranslatable" in {
      val serialized = write(testKeyU)
      val deserialized = read[TranslationKey0](serialized)
      getTranslatedString(deserialized) should be(getTranslatedString(testKeyU))
    }

    "serialize and deserialize reduced keys" in {
      val serialized = write(testKey1(5))
      val deserialized = read[TranslationKey0](serialized)
      getTranslatedString(deserialized) should be(getTranslatedString(testKey1(5)))

      val serialized2 = write(testKey5(1, "2", 3, "4", 5))
      val deserialized2 = read[TranslationKey0](serialized2)
      getTranslatedString(deserialized2) should be(getTranslatedString(testKey5(1, "2", 3, "4", 5)))

      val serializedX = write(testKeyX((1, 2, "3", 4.5)))
      val deserializedX = read[TranslationKey0](serializedX)
      getTranslatedString(deserializedX) should be(getTranslatedString(testKeyX((1, 2, "3", 4.5))))
    }
  }
}
