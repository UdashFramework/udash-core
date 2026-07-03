package io.udash.rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.meta.MacroInstances.materializeWith
import com.avsystem.commons.rpc.AsRaw
import io.udash.rest.openapi.OpenApiMetadata
import io.udash.rest.raw.{RawRest, RestMetadata}
import monix.eval.Task

/** TODO doc: why trait: allow client apps to create more custom companions */
trait AbstractContextualServerRestApis[Implicits, Ctx] extends ApiDataWithCustomImplicits[Implicits] {

  type CtxTask[+T] = WithCtx[Ctx, Task[T]]
  type InCtx[+T] = Ctx => Task[T]

  def CtxTask: CtxTaskCompanion[Ctx] = CtxTaskCompanion[Ctx]

  /**
   * Base class for companion objects of contextual REST API *traits*.
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

  /**
   * Base class for companion objects of contextual REST API *implementation classes* (which have no API traits).
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

/** TODO doc */
abstract class ContextualServerRestApis[Implicits, Ctx](override protected val implicits: Implicits)
  extends AbstractContextualServerRestApis[Implicits, Ctx]

trait CtxServerApiInstances[Ctx, Real] {
  def asRaw(implicit ctx: Ctx): RawRest.AsRawRpc[Real]
  def metadata: RestMetadata[Real]
}

trait CtxServerOpenApiInstances[Ctx, Real] extends CtxServerApiInstances[Ctx, Real] with OpenApiInstances[Real]

trait CtxServerImplInstances[Ctx, Real] {
  @materializeWith(RawRest, "materializeApiAsRaw")
  def asRaw(implicit ctx: Ctx): RawRest.AsRawRpc[Real]
  @materializeWith(RestMetadata, "materializeForImpl")
  def metadata: RestMetadata[Real]
}

trait CtxOpenApiServerImplInstances[Ctx, Real] extends CtxServerImplInstances[Ctx, Real] with OpenApiImplInstances[Real]
