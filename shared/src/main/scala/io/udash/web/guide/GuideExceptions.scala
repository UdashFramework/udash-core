package io.udash.web.guide

import com.avsystem.commons.serialization.GenCodec
import io.udash.i18n.TranslationKey0
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}

object GuideExceptions {
  case class ExampleException(msg: String) extends Exception(msg)
  case class TranslatableExampleException(trKey: TranslationKey0) extends Exception

  val registry: ExceptionCodecRegistry = {
    val registry = new DefaultExceptionCodecRegistry
    registry.register(GenCodec.materialize[ExampleException])
    registry.register(GenCodec.materialize[TranslatableExampleException])
    registry
  }
}
