package io.udash.web.guide

import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.serialization.GenCodec
import io.udash.i18n.TranslationKey0
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}

object GuideExceptions {
  final case class ExampleException(msg: String) extends Exception(msg)
  final case class TranslatableExampleException(trKey: TranslationKey0) extends Exception

  val registry: ExceptionCodecRegistry = (new DefaultExceptionCodecRegistry).setup { registry =>
    registry.register(GenCodec.materialize[ExampleException])
    registry.register(GenCodec.materialize[TranslatableExampleException])
    registry
  }
}
