package io.udash.rpc

import com.avsystem.commons.misc.MacroGenerated
import com.avsystem.commons.serialization.GenCodec
import io.udash.properties.{MacroModelPropertyCreator, ModelPropertyCreator}

abstract class HasGenCodecAndModelPropertyCreator[T](implicit
  mpc: MacroModelPropertyCreator[T], macroCodec: MacroGenerated[Any, GenCodec[T]]
) {
  /**
    * Use this constructor and pass `ModelPropertyCreator.materialize` and `GenCodec.materialize` explicitly
    * if you're getting the "super constructor cannot be passed a self reference unless parameter is declared by-name" error.
    */
  def this(creator: => ModelPropertyCreator[T], codec: => GenCodec[T]) = this()(MacroModelPropertyCreator(creator), MacroGenerated(codec))

  implicit val modelPropertyCreator: ModelPropertyCreator[T] = mpc.pc
  implicit val codec: GenCodec[T] = macroCodec.forCompanion(this)
}
