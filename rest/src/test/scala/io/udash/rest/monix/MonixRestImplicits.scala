package io.udash
package rest.monix

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.ImplicitNotFound
import io.udash.rest.raw.HttpResponseType
import io.udash.rest.{CatsEffectRestImplicits, GenCodecRestImplicits, OpenApiFullInstances, RestOpenApiCompanion}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.annotation.implicitNotFound

object MonixRestImplicits extends CatsEffectRestImplicits[Task] with GenCodecRestImplicits {
  @implicitNotFound("${T} is not a valid HTTP method result type - it must be wrapped into a Task")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()
}

abstract class MonixRestApiCompanion[Real](
  implicit macroInstances: MacroInstances[MonixRestImplicits.type, OpenApiFullInstances[Real]]
) extends RestOpenApiCompanion[MonixRestImplicits.type, Real](MonixRestImplicits)
