package io.udash.i18n

import com.avsystem.commons.serialization.{GenCodec, Input, Output}

import scala.concurrent.Future
import scala.util.Try

sealed trait TranslationKey {
  def key: String
}

class TranslationKey0(override val key: String) extends TranslationKey {
  def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key)
}

class TranslationKey1[T](override val key: String) extends TranslationKey {
  def apply(arg1: T)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1)

  def reduce(arg1: T): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1)
}

class TranslationKey2[T1, T2](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2)

  def reduce(arg1: T1, arg2: T2): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2)
}

class TranslationKey3[T1, T2, T3](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3)

  def reduce(arg1: T1, arg2: T2, arg3: T3): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3)
}

class TranslationKey4[T1, T2, T3, T4](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4)
}

class TranslationKey5[T1, T2, T3, T4, T5](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5)
}

class TranslationKey6[T1, T2, T3, T4, T5, T6](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6)
}

class TranslationKey7[T1, T2, T3, T4, T5, T6, T7](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
}

class TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
}

class TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](override val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)

  def reduce(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
}

class TranslationKeyX(override val key: String) extends TranslationKey {
  def apply(argv: Any*)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, argv: _*)

  def reduce(argv: Any*): TranslationKey0 =
    new TranslationKey.ReducedTranslationKey(key, argv: _*)
}

object TranslationKey {
  def key(key: String): TranslationKey0 =
    new TranslationKey0(key)

  def key1[T](key: String): TranslationKey1[T] =
    new TranslationKey1[T](key)

  def key2[T1, T2](key: String): TranslationKey2[T1, T2] =
    new TranslationKey2[T1, T2](key)

  def key3[T1, T2, T3](key: String): TranslationKey3[T1, T2, T3] =
    new TranslationKey3[T1, T2, T3](key)

  def key4[T1, T2, T3, T4](key: String): TranslationKey4[T1, T2, T3, T4] =
    new TranslationKey4[T1, T2, T3, T4](key)

  def key5[T1, T2, T3, T4, T5](key: String): TranslationKey5[T1, T2, T3, T4, T5] =
    new TranslationKey5[T1, T2, T3, T4, T5](key)

  def key6[T1, T2, T3, T4, T5, T6](key: String): TranslationKey6[T1, T2, T3, T4, T5, T6] =
    new TranslationKey6[T1, T2, T3, T4, T5, T6](key)

  def key7[T1, T2, T3, T4, T5, T6, T7](key: String): TranslationKey7[T1, T2, T3, T4, T5, T6, T7] =
    new TranslationKey7[T1, T2, T3, T4, T5, T6, T7](key)

  def key8[T1, T2, T3, T4, T5, T6, T7, T8](key: String): TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8] =
    new TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](key)

  def key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key: String): TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9] =
    new TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key)

  def keyX(key: String): TranslationKeyX =
    new TranslationKeyX(key)

  def untranslatable(key: String): TranslationKey0 =
    new Untranslatable(key)

  private[i18n] class ReducedTranslationKey(override val key: String, val argv: Any*) extends TranslationKey0(key) {
    override def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
      provider.translate(key, argv: _*)
  }

  private[i18n] class Untranslatable(override val key: String) extends TranslationKey0(key) {
    override def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
      Future.successful(Translated(key))
  }
}

object TranslationKey0 {
  implicit val codec: GenCodec[TranslationKey0] = GenCodec.create[TranslationKey0](
    (input: Input) => {
      Try(input.readList()).toOption match {
        case Some(list) =>
          val key = list.nextElement().readSimple().readString()
          val items = list.iterator(_.readSimple().readString()).toList
          list.skipRemaining()
          if (items.nonEmpty) new TranslationKey.ReducedTranslationKey(key, items: _*)
          else TranslationKey.key(key)
        case None =>
          TranslationKey.untranslatable(input.readSimple().readString())
      }
    },
    (output: Output, value: TranslationKey0) => {
      value match {
        case reduced: TranslationKey.ReducedTranslationKey =>
          val data = output.writeList()
          data.writeElement().writeSimple().writeString(value.key)
          reduced.argv.foreach(item => data.writeElement().writeSimple().writeString(item.toString))
          data.finish()
        case untranslatable: TranslationKey.Untranslatable =>
          output.writeSimple().writeString(untranslatable.key)
        case std: TranslationKey =>
          val data = output.writeList()
          data.writeElement().writeSimple().writeString(std.key)
          data.finish()
      }
    }
  )
}