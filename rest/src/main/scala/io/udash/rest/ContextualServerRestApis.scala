package io.udash.rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.meta.MacroInstances.materializeWith
import com.avsystem.commons.rpc.AsRaw
import io.udash.rest.openapi.OpenApiMetadata
import io.udash.rest.raw.{RawRest, RestMetadata}
import monix.eval.Task

/**
 * Provides companion base classes for **server-only, contextual** REST APIs - APIs whose method
 * implementations receive a request-scoped context of type `Ctx` (e.g. an authenticated user) via
 * [[WithCtx]], while the wire protocol stays context-free. Methods typically return the [[CtxTask]]
 * alias, i.e. `WithCtx[Ctx, Task[T]]`.
 *
 * This is a `trait` (rather than only the ready-to-use [[ContextualServerRestApis]] abstract class) so
 * that applications may mix it into their own base and layer additional custom companions on top of the
 * ones defined here.
 *
 * The `Implicits` type parameter is a bundle of implicits (typically extending [[DefaultRestImplicits]])
 * injected into macro materialization - it is how custom serialization for your own types is picked up.
 *
 * @see [[ContextualServerAndClientRestApis]] for APIs shared between server and client.
 */
trait AbstractContextualServerRestApis[Implicits, Ctx] extends ApiDataWithCustomImplicits[Implicits] {

  /** Result type of contextual server method: a [[monix.eval.Task Task]] awaiting the context `Ctx`. */
  type CtxTask[+T] = WithCtx[Ctx, Task[T]]
  /** Bare context-consuming, task-returning function underlying [[CtxTask]]. */
  type InCtx[+T] = Ctx => Task[T]

  /** Factory for [[CtxTask]] values for this API's context type. */
  def CtxTask: CtxTaskCompanion[Ctx] = CtxTaskCompanion[Ctx]

  /**
   * Base class for companion objects of contextual REST API *traits* (with a separate implementation
   * class). Provides [[RestMetadata]], [[OpenApiMetadata]] and a
   * context-parameterized `AsRaw[RawRest, Real]` (available once a `Ctx` is in implicit scope), plus
   * `restAsContextualRaw` which defers the context so a handler can be built per request.
   */
  abstract class ServerApiCompanion[Real](
    implicit inst: MacroInstances[Implicits, CtxServerOpenApiInstances[Ctx, Real]]
  ) {
    implicit lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
    implicit def restAsRaw(implicit ctx: Ctx): AsRaw[RawRest, Real] = inst(implicits, this).asRaw
    implicit lazy val openapiMetadata: OpenApiMetadata[Real] = inst(implicits, this).openapiMetadata

    implicit lazy val restAsContextualRaw: AsRaw[WithCtx[Ctx, RawRest], Real] =
      real => WithCtx(implicit ctx => restAsRaw.asRaw(real))
  }

  /** Like [[ServerApiCompanion]], but without OpenAPI generation. */
  abstract class ServerNoDocApiCompanion[Real](
    implicit inst: MacroInstances[Implicits, CtxServerApiInstances[Ctx, Real]]
  ) {
    implicit lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
    implicit def restAsRaw(implicit ctx: Ctx): AsRaw[RawRest, Real] = inst(implicits, this).asRaw

    implicit lazy val restAsContextualRaw: AsRaw[WithCtx[Ctx, RawRest], Real] =
      real => WithCtx(implicit ctx => restAsRaw.asRaw(real))
  }

  /**
   * Like [[ServerApiCompanion]], but for contextual REST API *implementation classes* that have their
   * methods already implemented (no separate API trait). Fewer macro-generated implicits are required.
   */
  abstract class ServerApiImplCompanion[Real](
    implicit inst: MacroInstances[Implicits, CtxOpenApiServerImplInstances[Ctx, Real]]
  ) {
    implicit lazy val restMetadata: RestMetadata[Real] = inst(implicits, this).metadata
    implicit def restAsRaw(implicit ctx: Ctx): AsRaw[RawRest, Real] = inst(implicits, this).asRaw
    implicit lazy val openapiMetadata: OpenApiMetadata[Real] = inst(implicits, this).openapiMetadata

    implicit lazy val restAsContextualRaw: AsRaw[WithCtx[Ctx, RawRest], Real] =
      real => WithCtx(implicit ctx => restAsRaw.asRaw(real))
  }
}

/**
 * Ready-to-use entry point for [[AbstractContextualServerRestApis]]. Extend it with an `object`, fixing
 * the implicits bundle and context type, e.g.
 * {{{
 *   object MyImplicits extends DefaultRestImplicits
 *   case class UserContext(userId: String)
 *   object MyCtxApis extends ContextualServerRestApis[MyImplicits.type, UserContext](MyImplicits)
 * }}}
 */
abstract class ContextualServerRestApis[Implicits, Ctx](override protected val implicits: Implicits)
  extends AbstractContextualServerRestApis[Implicits, Ctx]

/** Instances required by [[AbstractContextualServerRestApis.ServerApiCompanion]] for an API trait. */
trait CtxServerApiInstances[Ctx, Real] {
  def asRaw(implicit ctx: Ctx): RawRest.AsRawRpc[Real]
  def metadata: RestMetadata[Real]
}

/** [[CtxServerApiInstances]] extended with OpenAPI metadata. */
trait CtxServerOpenApiInstances[Ctx, Real] extends CtxServerApiInstances[Ctx, Real] with OpenApiInstances[Real]

/** Like [[CtxServerApiInstances]] but for an already-implemented class (materialized via `@materializeWith`). */
trait CtxServerImplInstances[Ctx, Real] {
  @materializeWith(RawRest, "materializeApiAsRaw")
  def asRaw(implicit ctx: Ctx): RawRest.AsRawRpc[Real]
  @materializeWith(RestMetadata, "materializeForImpl")
  def metadata: RestMetadata[Real]
}

/** [[CtxServerImplInstances]] extended with OpenAPI metadata for an already-implemented class. */
trait CtxOpenApiServerImplInstances[Ctx, Real] extends CtxServerImplInstances[Ctx, Real] with OpenApiImplInstances[Real]
