package io.udash.rpc

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}
import io.udash.testing.UdashSharedTest

private sealed trait RootTrait extends Throwable
private sealed trait SubTrait extends RootTrait
private case class SubTraitImpl() extends SubTrait

class DefaultExceptionCodecRegistryTest extends UdashSharedTest with Utils  {
  val exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry
  exceptionsRegistry.register(GenCodec.materialize[CustomException])
  exceptionsRegistry.register(GenCodec.materialize[SealedExceptions])
  exceptionsRegistry.register(GenCodec.materialize[RootTrait])

  "DefaultExceptionCodecRegistry" should {
    "find name of GenCodec for class" in {
      exceptionsRegistry.name(new RuntimeException("???")) should be(new RuntimeException("???").getClass.getName)
      exceptionsRegistry.name(new NullPointerException("???")) should be(new NullPointerException("???").getClass.getName)
      exceptionsRegistry.name(CustomException("???", 7)) should be(CustomException("???", 7).getClass.getName)
      exceptionsRegistry.name(SealedExceptionsA(42)) should be(classOf[SealedExceptions].getName)
      exceptionsRegistry.name(SealedExceptionsB(42)) should be(classOf[SealedExceptions].getName)
      exceptionsRegistry.name(SubTraitImpl()) should be(classOf[RootTrait].getName)
    }
  }
}