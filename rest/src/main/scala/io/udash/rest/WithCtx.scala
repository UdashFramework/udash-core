package io.udash.rest

import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.openapi.{OpenApiMetadata, RestResultType}
import io.udash.rest.raw.{HttpResponseType, RawRest, RestMetadata, RestResponse}
import monix.eval.Task

import scala.util.Try

/**
 * Use this as a result of contextual REST API method, e.g.
 * {{{
 *   import monix.eval.Task
 *
 *   case class UserContext(...)
 *
 *   object SomeImplicits extends MonixRestImplicits {
 *     ...
 *   }
 *
 *   abstract class UserContextRestApis
 *     extends ContextualServerRestApis[SomeImplicits.type, UserContext](SomeImplicits)
 *
 *   class SomeRestApi {
 *     @GET def handleStuff(stuff: String): UserContextRestApis.CtxTask[String] =
 *       UserContextRestApis.CtxTask { userContext => Task(s"$stuff handled") }
 *   }
 *   object SomeRestApi extends UserContextRestApis.ServerApiImplCompanion[SomeRestApi]
 * }}}
 *
 * This assumes the usage of [[ContextualServerRestApis]] with appropriate context type baked in.
 * The context itself is usually extracted by some lower layer, e.g. a servlet.
 */
final case class WithCtx[-Ctx, +R](fun: Ctx => R) extends AnyVal {
  def apply(ctx: Ctx): R = fun(ctx)
  def result(implicit ctx: Ctx): R = fun(ctx)
}
object WithCtx {
  sealed trait NoCtx extends Any
  object NoCtx extends NoCtx {
    implicit def noContext: NoCtx = this
  }

  implicit def withCtxAsResponse[Ctx, R](implicit
    ctx: Ctx,
    asResponseTask: AsRaw[Task[RestResponse], Try[R]],
  ): AsRaw[Task[RestResponse], Try[WithCtx[Ctx, R]]] =
    ctxFunTry => asResponseTask.asRaw(ctxFunTry.map(_.fun(ctx)))

  implicit def withCtxAsSubapi[Ctx, R](implicit
    ctx: Ctx,
    asSubapi: AsRaw[RawRest, R],
  ): AsRaw[RawRest, WithCtx[Ctx, R]] =
    withCtx => asSubapi.asRaw(withCtx(ctx))

  // metadata rewrapping implicits

  implicit def withCtxRestMetadata[Ctx, R](implicit
    metadata: RestMetadata[R],
  ): RestMetadata[WithCtx[Ctx, R]] =
    metadata.asInstanceOf[RestMetadata[WithCtx[Ctx, R]]]

  implicit def withCtxOpenapiMetadata[Ctx, R](implicit
    metadata: OpenApiMetadata[R],
  ): OpenApiMetadata[WithCtx[Ctx, R]] =
    metadata.asInstanceOf[OpenApiMetadata[WithCtx[Ctx, R]]]

  implicit def withCtxResponseType[Ctx, R](implicit
    responseType: HttpResponseType[R],
  ): HttpResponseType[WithCtx[Ctx, R]] =
    HttpResponseType()

  implicit def withCtxRestResultType[Ctx, R](implicit
    resultType: RestResultType[R],
  ): RestResultType[WithCtx[Ctx, R]] =
    resultType.asInstanceOf[RestResultType[WithCtx[Ctx, R]]]

  // implicits necessary for client side

  implicit def withNoCtxFromResponse[R](implicit
    asResponseTask: AsReal[Task[RestResponse], Try[R]],
  ): AsReal[Task[RestResponse], Try[WithCtx[NoCtx, R]]] =
    respTask => asResponseTask.asReal(respTask).map(r => WithCtx(_ => r))

  implicit def withNoCtxFromSubapi[R](implicit
    fromSubapi: AsReal[RawRest, R],
  ): AsReal[RawRest, WithCtx[NoCtx, R]] =
    rawRest => WithCtx(_ => fromSubapi.asReal(rawRest))

  implicit def ctxTaskOps[Ctx, A](ctxTask: WithCtx[Ctx, Task[A]]): CtxTaskOps[Ctx, A] =
    new CtxTaskOps(ctxTask.fun)
}

final class CtxTaskOps[Ctx, A](private val ctxTask: Ctx => Task[A]) extends AnyVal {
  type CtxTask[T] = WithCtx[Ctx, Task[T]]

  private def modifyTask[B](f: Task[A] => Task[B]): CtxTask[B] =
    WithCtx(ctx => f(ctxTask(ctx)))

  def map[B](f: A => B): CtxTask[B] = modifyTask(_.map(f))

  def flatMap[B](f: A => CtxTask[B]): CtxTask[B] =
    WithCtx(ctx => ctxTask(ctx).flatMap(a => f(a)(ctx)))

  def onErrorRecover[U >: A](pf: PartialFunction[Throwable, U]): CtxTask[U] =
    modifyTask(_.onErrorRecover(pf))

  def failed: CtxTask[Throwable] =
    modifyTask(_.failed)
}

final class CtxTaskCompanion[Ctx] {
  type CtxTask[T] = WithCtx[Ctx, Task[T]]

  val unit: CtxTask[Unit] = now(())
  def apply[A](f: Ctx => Task[A]): CtxTask[A] = WithCtx(f)
  def sync[A](f: Ctx => A): CtxTask[A] = WithCtx(ctx => Task(f(ctx)))
  def eval[A](expr: => A): CtxTask[A] = WithCtx(_ => Task(expr))
  def now[A](value: A): CtxTask[A] = WithCtx(_ => Task.now(value))
  def raiseError[A](ex: Throwable): CtxTask[A] = apply(_ => Task.raiseError(ex))
  def defer[A](ctxTask: => CtxTask[A]): CtxTask[A] = WithCtx(c => Task.defer(ctxTask(c)))

  def readCtx: CtxTask[Ctx] = WithCtx(Task.now)
  def readCtx[A](f: Ctx => A): CtxTask[A] = WithCtx(ctx => Task(f(ctx)))
}
object CtxTaskCompanion {
  private val reusable = new CtxTaskCompanion[Any]

  def apply[Ctx]: CtxTaskCompanion[Ctx] = reusable.asInstanceOf[CtxTaskCompanion[Ctx]]
}
