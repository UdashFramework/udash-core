package io.udash
package rest.monix

import com.avsystem.commons._
import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.ImplicitNotFound
import io.udash.rest.raw.HttpResponseType
import io.udash.rest.raw.RawRest.{Async, AsyncEffect}
import io.udash.rest.{GenCodecRestImplicits, OpenApiFullInstances, RestOpenApiCompanion}
import monix.eval.Task
import monix.execution.Scheduler

import scala.annotation.implicitNotFound

trait MonixRestImplicits extends GenCodecRestImplicits {
  implicit def scheduler: Scheduler = Scheduler.global

  implicit def taskToAsync: AsyncEffect[Task] =
    new AsyncEffect[Task] {
      def toAsync[A](task: Task[A]): Async[A] =
        callback => task.runAsync(res => callback(res.fold(Failure(_), Success(_))))
      def fromAsync[A](async: Async[A]): Task[A] =
        Task.async(callback => async(res => callback(res.fold(Left(_), Right(_)))))
    }

  @implicitNotFound("${T} is not a valid HTTP method result type - it must be wrapped into a Task")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()
}
object MonixRestImplicits extends MonixRestImplicits

abstract class MonixRestApiCompanion[Real](
  implicit macroInstances: MacroInstances[MonixRestImplicits, OpenApiFullInstances[Real]]
) extends RestOpenApiCompanion[MonixRestImplicits, Real](MonixRestImplicits)
