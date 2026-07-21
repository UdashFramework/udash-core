package io.udash.rest

import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.openapi.{OpenApiMetadata, RestResultType}
import io.udash.rest.raw.{HttpResponseType, RawRest, RestMetadata, RestResponse}
import monix.eval.Task

import scala.util.Try

/**
 * Represents a value `R` that can only be computed given some context `Ctx` - simply a wrapper over a
 * `Ctx => R` function. It is the building block of *contextual* REST APIs: APIs whose server-side method
 * implementations need access to request-scoped context (e.g. the authenticated user) which must **not**
 * appear in the client-side interface.
 *
 * The typical usage is as the result type of contextual REST method via the
 * `CtxTask[T] = WithCtx[Ctx, Task[T]]` alias defined by [[ContextualServerRestApis]] and
 * [[ContextualServerAndClientRestApis]]. The context is usually extracted by some lower layer
 * (e.g. a servlet) and supplied as an implicit when the API implementation is turned into a raw REST handler.
 *
 * @example
 * {{{
 *   import io.udash.rest._
 *   import monix.eval.Task
 *
 *   case class UserContext(userId: String)
 *
 *   // implicits injected into macro materialization; may add custom serialization for your own types
 *   object MyImplicits extends DefaultRestImplicits
 *   object UserContextRestApis
 *     extends ContextualServerRestApis[MyImplicits.type, UserContext](MyImplicits)
 *
 *   class SomeRestApi {
 *     @GET def handleStuff(stuff: String): UserContextRestApis.CtxTask[String] =
 *       UserContextRestApis.CtxTask { userContext =>
 *         Task.now(s"$stuff handled for " + userContext.userId)
 *       }
 *   }
 *   object SomeRestApi extends UserContextRestApis.ServerApiImplCompanion[SomeRestApi]
 * }}}
 */
final case class WithCtx[-Ctx, +R](fun: Ctx => R) extends AnyVal {
  /** Computes the wrapped value for an explicitly passed context. */
  def apply(ctx: Ctx): R = fun(ctx)
  /** Computes the wrapped value for a context taken from the implicit scope. */
  def result(implicit ctx: Ctx): R = fun(ctx)
}
object WithCtx {
  /**
   * The "empty" context used on the client side of a contextual REST API. A client never provides any
   * server-side context, so it uses `WithCtx[NoCtx, R]` which is trivially resolvable via the implicit
   * [[NoCtx.noContext]]. See [[ContextualServerAndClientRestApis]] where the client type is fixed to
   * `Real[NoCtx]`.
   */
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

/**
 * Monadic operations on a `CtxTask[A] = WithCtx[Ctx, Task[A]]`, made available through the implicit
 * [[WithCtx.ctxTaskOps]] conversion. They let you transform a context-dependent [[monix.eval.Task Task]]
 * without manually unwrapping and re-wrapping the context function - the same context is threaded through.
 */
final class CtxTaskOps[Ctx, A](private val ctxTask: Ctx => Task[A]) extends AnyVal {
  type CtxTask[T] = WithCtx[Ctx, Task[T]]

  private def modifyTask[B](f: Task[A] => Task[B]): CtxTask[B] =
    WithCtx(ctx => f(ctxTask(ctx)))

  /** Maps the successful result of the underlying task. */
  def map[B](f: A => B): CtxTask[B] = modifyTask(_.map(f))

  /** Sequences another context-dependent task, threading the same context into both. */
  def flatMap[B](f: A => CtxTask[B]): CtxTask[B] =
    WithCtx(ctx => ctxTask(ctx).flatMap(a => f(a)(ctx)))

  /** Recovers from a failure of the underlying task. */
  def onErrorRecover[U >: A](pf: PartialFunction[Throwable, U]): CtxTask[U] =
    modifyTask(_.onErrorRecover(pf))

  /** Exposes the failure of the underlying task as a successful `Throwable` result. */
  def failed: CtxTask[Throwable] =
    modifyTask(_.failed)
}

/**
 * Factory for `CtxTask[A] = WithCtx[Ctx, Task[A]]` values, mirroring the most common
 * [[monix.eval.Task Task]] constructors. Obtain an instance for a fixed context type via
 * [[CtxTaskCompanion.apply]] (the `CtxTask` accessor on [[ContextualServerRestApis]] /
 * [[ContextualServerAndClientRestApis.Api]] returns one).
 */
final class CtxTaskCompanion[Ctx] {
  type CtxTask[T] = WithCtx[Ctx, Task[T]]

  /** A `CtxTask` that always succeeds with `()`, ignoring the context. */
  val unit: CtxTask[Unit] = now(())
  /** Builds a `CtxTask` directly from a context-consuming task-returning function. */
  def apply[A](f: Ctx => Task[A]): CtxTask[A] = WithCtx(f)
  /** Builds a `CtxTask` from a synchronous, context-consuming computation (suspended in a `Task`). */
  def sync[A](f: Ctx => A): CtxTask[A] = WithCtx(ctx => Task(f(ctx)))
  /** Suspends an arbitrary expression in a `CtxTask`, ignoring the context. */
  def eval[A](expr: => A): CtxTask[A] = WithCtx(_ => Task(expr))
  /** A `CtxTask` that always succeeds with an already-computed value, ignoring the context. */
  def now[A](value: A): CtxTask[A] = WithCtx(_ => Task.now(value))
  /** A `CtxTask` that always fails with the given error. */
  def raiseError[A](ex: Throwable): CtxTask[A] = apply(_ => Task.raiseError(ex))
  /** Defers construction of a `CtxTask` until it is run. */
  def defer[A](ctxTask: => CtxTask[A]): CtxTask[A] = WithCtx(c => Task.defer(ctxTask(c)))

  /** A `CtxTask` that yields the context itself. */
  def readCtx: CtxTask[Ctx] = WithCtx(Task.now)
  /** A `CtxTask` that yields a value derived from the context. */
  def readCtx[A](f: Ctx => A): CtxTask[A] = WithCtx(ctx => Task(f(ctx)))
}
object CtxTaskCompanion {
  private val reusable = new CtxTaskCompanion[Any]

  def apply[Ctx]: CtxTaskCompanion[Ctx] = reusable.asInstanceOf[CtxTaskCompanion[Ctx]]
}
