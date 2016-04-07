package io.udash.i18n

import scala.concurrent.Future

sealed trait TranslationKey {
  def key: String
}

class TranslationKey0(val key: String) extends TranslationKey {
  def apply()(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key)
}

class TranslationKey1[T](val key: String) extends TranslationKey {
  def apply(arg1: T)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1)
}

class TranslationKey2[T1, T2](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2)
}

class TranslationKey3[T1, T2, T3](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3)
}

class TranslationKey4[T1, T2, T3, T4](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4)
}

class TranslationKey5[T1, T2, T3, T4, T5](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5)
}

class TranslationKey6[T1, T2, T3, T4, T5, T6](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6)
}

class TranslationKey7[T1, T2, T3, T4, T5, T6, T7](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
}

class TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
}

class TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](val key: String) extends TranslationKey {
  def apply(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
}

class TranslationKeyX(val key: String) extends TranslationKey {
  def apply(argv: Any*)(implicit provider: TranslationProvider, lang: Lang): Future[Translated] =
    provider.translate(key, argv:_*)
}

object TranslationKey {
  def key(key: String) = new TranslationKey0(key)
  def key1[T](key: String) = new TranslationKey1[T](key)
  def key2[T1, T2](key: String) = new TranslationKey2[T1, T2](key)
  def key3[T1, T2, T3](key: String) = new TranslationKey3[T1, T2, T3](key)
  def key4[T1, T2, T3, T4](key: String) = new TranslationKey4[T1, T2, T3, T4](key)
  def key5[T1, T2, T3, T4, T5](key: String) = new TranslationKey5[T1, T2, T3, T4, T5](key)
  def key6[T1, T2, T3, T4, T5, T6](key: String) = new TranslationKey6[T1, T2, T3, T4, T5, T6](key)
  def key7[T1, T2, T3, T4, T5, T6, T7](key: String) = new TranslationKey7[T1, T2, T3, T4, T5, T6, T7](key)
  def key8[T1, T2, T3, T4, T5, T6, T7, T8](key: String) = new TranslationKey8[T1, T2, T3, T4, T5, T6, T7, T8](key)
  def key9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key: String) = new TranslationKey9[T1, T2, T3, T4, T5, T6, T7, T8, T9](key)
  def keyX(key: String) = new TranslationKeyX(key)
}