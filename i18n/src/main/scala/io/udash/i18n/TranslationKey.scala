package io.udash.i18n

import com.avsystem.commons.misc.AbstractCase
import com.avsystem.commons.serialization.{GenCodec, HasGenCodec}

import scala.concurrent.Future

sealed trait TranslationKey extends AbstractCase {
  def key: String
}

sealed trait TranslationKey0 extends TranslationKey {
  def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] = provider.translate(key)
}

object TranslationKey0 extends HasGenCodec[TranslationKey0] {
  def apply(key: String): TranslationKey0 = TranslationKey.SimpleTranslationKey0(key)
}

final case class TranslationKey1[T](key: String) extends TranslationKey {
  def apply(arg1: T): TranslationKey0 = TranslationKey.ReducedTranslationKey(key, arg1)
}

final case class TranslationKey2[T1, T2](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2): TranslationKey0 = TranslationKey.ReducedTranslationKey(key, arg1, arg2)
}

final case class TranslationKey3[T1, T2, T3](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3): TranslationKey0 = TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3)
}

final case class TranslationKey4[T1, T2, T3, T4](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4)
}

final case class TranslationKey5[T1, T2, T3, T4, T5](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5)
}

final case class TranslationKey6[T1, T2, T3, T4, T5, T6](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6)
}

final case class TranslationKey7[T1, T2, T3, T4, T5, T6, T7](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
}

final case class TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
}

final case class TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9): TranslationKey0 =
    TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
}

final case class TranslationKeyX(key: String) extends TranslationKey {
  def apply(argv: Any*): TranslationKey0 = TranslationKey.ReducedTranslationKey(key, argv: _*)
}

object TranslationKey {
  def key(key: String): TranslationKey0 =
    SimpleTranslationKey0(key)

  def key1[T](key: String): TranslationKey1[T] =
    TranslationKey1[T](key)

  def key2[T1, T2](key: String): TranslationKey2[T1, T2] =
    TranslationKey2[T1, T2](key)

  def key3[T1, T2, T3](key: String): TranslationKey3[T1, T2, T3] =
    TranslationKey3[T1, T2, T3](key)

  def key4[T1, T2, T3, T4](key: String): TranslationKey4[T1, T2, T3, T4] =
    TranslationKey4[T1, T2, T3, T4](key)

  def key5[T1, T2, T3, T4, T5](key: String): TranslationKey5[T1, T2, T3, T4, T5] =
    TranslationKey5[T1, T2, T3, T4, T5](key)

  def key6[T1, T2, T3, T4, T5, T6](key: String): TranslationKey6[T1, T2, T3, T4, T5, T6] =
    TranslationKey6[T1, T2, T3, T4, T5, T6](key)

  def key7[T1, T2, T3, T4, T5, T6, T7](key: String): TranslationKey7[T1, T2, T3, T4, T5, T6, T7] =
    TranslationKey7[T1, T2, T3, T4, T5, T6, T7](key)

  def key8[T1, T2, T3, T4, T5, T6, T7, T8](key: String): TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8] =
    TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](key)

  def key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key: String): TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9] =
    TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key)

  def keyX(key: String): TranslationKeyX =
    TranslationKeyX(key)

  def untranslatable(key: String): TranslationKey0 =
    Untranslatable(key)

  private[i18n] final case class SimpleTranslationKey0(key: String) extends TranslationKey0

  private[i18n] final case class ReducedTranslationKey(key: String, argv: Any*) extends TranslationKey0 {
    override def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
      provider.translate(key, argv: _*)
    // default toString puts argv inside "WrappedArray()"
    override def toString(): String = s"$productPrefix($key,${argv.mkString(",")})"
  }

  private[i18n] object ReducedTranslationKey {
    // note: serialization loses information, as argv is converted to strings
    implicit val codec: GenCodec[ReducedTranslationKey] = GenCodec.create[ReducedTranslationKey](
      input => {
        val list = input.readList()
        val key = list.nextElement().readSimple().readString()
        val items = list.iterator(_.readSimple().readString()).toList
        ReducedTranslationKey(key, items: _*)
      },
      (output, value) => {
        val list = output.writeList()
        list.writeElement().writeSimple().writeString(value.key)
        value.argv.foreach(arg => list.writeElement().writeSimple().writeString(arg.toString))
        list.finish()
      }
    )
  }

  private[i18n] final case class Untranslatable(key: String) extends TranslationKey0 {
    override def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
      Future.successful(Translated(key))
  }
}