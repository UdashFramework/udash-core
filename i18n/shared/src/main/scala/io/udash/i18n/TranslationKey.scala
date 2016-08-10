package io.udash.i18n

import scala.concurrent.Future

sealed trait TranslationKey {
  def key: String
}

trait TranslationKey0 extends TranslationKey {
  def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key)
}

trait TranslationKey1[T] extends TranslationKey {
  def apply(arg1: T)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1)
}

trait TranslationKey2[T1, T2] extends TranslationKey {
  def apply(arg1: T1, arg2: T2)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2)
}

trait TranslationKey3[T1, T2, T3] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3)
}

trait TranslationKey4[T1, T2, T3, T4] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4)
}

trait TranslationKey5[T1, T2, T3, T4, T5] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5)
}

trait TranslationKey6[T1, T2, T3, T4, T5, T6] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6)
}

trait TranslationKey7[T1, T2, T3, T4, T5, T6, T7] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
}

trait TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
}

trait TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9] extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
}

trait TranslationKeyX extends TranslationKey {
  def apply(argv: Any*)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, argv:_*)
}

object TranslationKey {
  def key(key: String): TranslationKey0 =
    new Key0(key)

  def key1[T](key: String): TranslationKey1[T] =
    new Key1[T](key)

  def key2[T1, T2](key: String): TranslationKey2[T1, T2] =
    new Key2[T1, T2](key)

  def key3[T1, T2, T3](key: String): TranslationKey3[T1, T2, T3] =
    new Key3[T1, T2, T3](key)

  def key4[T1, T2, T3, T4](key: String): TranslationKey4[T1, T2, T3, T4] =
    new Key4[T1, T2, T3, T4](key)

  def key5[T1, T2, T3, T4, T5](key: String): TranslationKey5[T1, T2, T3, T4, T5] =
    new Key5[T1, T2, T3, T4, T5](key)

  def key6[T1, T2, T3, T4, T5, T6](key: String): TranslationKey6[T1, T2, T3, T4, T5, T6] =
    new Key6[T1, T2, T3, T4, T5, T6](key)

  def key7[T1, T2, T3, T4, T5, T6, T7](key: String): TranslationKey7[T1, T2, T3, T4, T5, T6, T7] =
    new Key7[T1, T2, T3, T4, T5, T6, T7](key)

  def key8[T1, T2, T3, T4, T5, T6, T7, T8](key: String): TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8] =
    new Key8[T1, T2, T3, T4, T5, T6, T7, T8](key)

  def key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key: String): TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9] =
    new Key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key)

  def keyX(key: String): TranslationKeyX =
    new KeyX(key)

  private class Key0(override val key: String) extends TranslationKey0
  private class Key1[T](override val key: String) extends TranslationKey1[T]
  private class Key2[T1, T2](override val key: String) extends TranslationKey2[T1, T2]
  private class Key3[T1, T2, T3](override val key: String) extends TranslationKey3[T1, T2, T3]
  private class Key4[T1, T2, T3, T4](override val key: String) extends TranslationKey4[T1, T2, T3, T4]
  private class Key5[T1, T2, T3, T4, T5](override val key: String) extends TranslationKey5[T1, T2, T3, T4, T5]
  private class Key6[T1, T2, T3, T4, T5, T6](override val key: String) extends TranslationKey6[T1, T2, T3, T4, T5, T6]
  private class Key7[T1, T2, T3, T4, T5, T6, T7](override val key: String) extends TranslationKey7[T1, T2, T3, T4, T5, T6, T7]
  private class Key8[T1, T2, T3, T4, T5, T6, T7, T8](override val key: String) extends TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8]
  private class Key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](override val key: String) extends TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9]
  private class KeyX(override val key: String) extends TranslationKeyX
}