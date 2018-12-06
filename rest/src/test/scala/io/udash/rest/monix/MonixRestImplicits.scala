package io.udash
package rest.monix

import com.avsystem.commons._
import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.openapi.{RestResponses, RestResultType}
import io.udash.rest.raw.{HttpResponseType, RawRest, RestResponse}
import io.udash.rest.{GenCodecRestImplicits, OpenApiFullInstances, RestOpenApiCompanion}
import monix.eval.Task
import monix.execution.Scheduler

import scala.annotation.implicitNotFound

trait MonixRestImplicits extends GenCodecRestImplicits {
  implicit def scheduler: Scheduler = Scheduler.global

  implicit def taskToAsyncResp[T](
    implicit respAsRaw: AsRaw[RestResponse, T]
  ): AsRaw[RawRest.Async[RestResponse], Try[Task[T]]] =
    AsRaw.create { triedtask =>
      val task = triedtask.fold(Task.raiseError, identity).map(respAsRaw.asRaw)
      callback => task.runAsync(r => callback(r.fold(Failure(_), Success(_))))
    }

  implicit def taskFromAsyncResp[T](
    implicit respAsReal: AsReal[RestResponse, T]
  ): AsReal[RawRest.Async[RestResponse], Try[Task[T]]] =
    AsReal.create { async =>
      val task = Task.async[RestResponse](callback => async(_.fold(callback.onError, callback.onSuccess)))
      Success(task.map(respAsReal.asReal))
    }

  @implicitNotFound("#{forResponse}")
  implicit def taskAsRawNotFound[T](
    implicit forResponse: ImplicitNotFound[AsRaw[RestResponse, T]]
  ): ImplicitNotFound[AsRaw[RawRest.Async[RestResponse], Try[Task[T]]]] = ImplicitNotFound()

  @implicitNotFound("#{forResponse}")
  implicit def taskAsRealNotFound[T](
    implicit forResponse: ImplicitNotFound[AsReal[RestResponse, T]]
  ): ImplicitNotFound[AsReal[RawRest.Async[RestResponse], Try[Task[T]]]] = ImplicitNotFound()

  implicit def taskHttpResponseType[T]: HttpResponseType[Task[T]] =
    HttpResponseType[Task[T]]()

  @implicitNotFound("${T} is not a valid REST HTTP method result type - it must be wrapped into a Task")
  implicit def httpResponseTypeNotFound[T]: ImplicitNotFound[HttpResponseType[T]] =
    ImplicitNotFound()

  implicit def taskRestResultType[T: RestResponses]: RestResultType[Task[T]] =
    RestResultType[Task[T]](RestResponses[T].responses)

  @implicitNotFound("#{forRestResponses}")
  implicit def taskRestResultTypeNotFound[T](
    implicit forRestResponses: ImplicitNotFound[RestResponses[T]]
  ): ImplicitNotFound[RestResultType[Task[T]]] = ImplicitNotFound()
}
object MonixRestImplicits extends MonixRestImplicits

abstract class MonixRestApiCompanion[Real](
  implicit macroInstances: MacroInstances[MonixRestImplicits, OpenApiFullInstances[Real]]
) extends RestOpenApiCompanion[MonixRestImplicits, Real](MonixRestImplicits)
