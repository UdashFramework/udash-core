package io.udash.rest

import com.avsystem.commons.meta.MacroInstances
import io.udash.rest.WithCtx.NoCtx
import io.udash.rest.openapi.OpenApiMetadata
import io.udash.rest.raw.{RawRest, RestMetadata}
import monix.eval.Task

/**
 * Provides companion base classes for **contextual** REST APIs that are used on both the server and the
 * client side. Server-side implementations may accept a request-scoped context of type `Ctx` (e.g. an
 * authenticated user) via [[WithCtx]], while the client must stay context-free. Therefore, the API trait
 * is parameterized with the context type (`Real[Ctx]`) and the client fixes it to [[WithCtx.NoCtx NoCtx]].
 *
 * This is a `trait` (rather than only the ready-to-use [[ContextualServerAndClientRestApis]] abstract
 * class) so that applications may mix it into their own base and layer additional custom companions on top.
 *
 * The `Implicits` type parameter is a bundle of implicits (typically extending [[DefaultRestImplicits]])
 * injected into macro materialization - it is how custom serialization for your own types is picked up.
 *
 * @see [[ContextualServerRestApis]] for server-only contextual APIs.
 */
trait AbstractContextualServerAndClientRestApis[Implicits] extends ApiDataWithCustomImplicits[Implicits] {

  /** Base trait for context-parameterized REST API traits; supplies the [[CtxTask]] alias and its factory. */
  trait Api[Ctx] {
    /** Result type of contextual method: a [[monix.eval.Task Task]] awaiting the context `Ctx`. */
    type CtxTask[+T] = WithCtx[Ctx, Task[T]]
    /** Factory for [[CtxTask]] values for this API's context type. */
    def CtxTask: CtxTaskCompanion[Ctx] = CtxTaskCompanion[Ctx]
  }

  /**
   * Base class for companion objects of contextual REST APIs that are used on both server and client side.
   * Contextual REST APIs are APIs that wish to accept additional context into their server-side method implementations
   * via [[WithCtx]].
   *
   * However, the client side interface must be free of any context. Therefore, the API trait must be parameterized
   * with context type. Client can then use [[NoCtx]] as its context type.
   *
   * Example:
   * {{{
   *   // reusable boilerplate - implicits bundle injected into macro materialization
   *   // (extend it to plug in custom serialization for your own types)
   *   trait MyRestImplicits extends DefaultRestImplicits
   *   object MyRestImplicits extends MyRestImplicits
   *
   *   object MyRestApis extends ContextualServerAndClientRestApis[MyRestImplicits.type](MyRestImplicits)
   *
   *   // REST API trait definition
   *   trait MyApi[Ctx] extends MyRestApis.Api[Ctx] {
   *     @GET def stuff(param: Int): CtxTask[String]
   *   }
   *   object MyApi extends MyRestApis.ApiCompanion[MyApi]
   *
   *   // the context class and server side implementation
   *   case class UserContext(user: String)
   *
   *   class MyApiImpl extends MyApi[UserContext] {
   *     @GET def stuff(param: Int): CtxTask[String] = CtxTask { userContext => ... }
   *   }
   *
   *   // client-side proxy
   *   val client: MyApi.Client = JettyRestClient[MyApi.Client](...) // MyApi[NoCtx]
   *   val invocation: Task[String] = client.stuff(42).result
   * }}}
   */
  abstract class ApiCompanion[Real[Ctx]](
    implicit instances: MacroInstances[Implicits, GenericContextualApiInstances[Real]]
  ) {
    type Client = Real[NoCtx]

    implicit def restAsRaw[Ctx](implicit ctx: Ctx): RawRest.AsRawRpc[Real[Ctx]] =
      instances(implicits, this).restAsRaw[Ctx]
    implicit lazy val restAsReal: RawRest.AsRealRpc[Client] = instances(implicits, this).restAsReal

    private lazy val reusableRestMetadata: RestMetadata[Client] = instances(implicits, this).restMetadata
    implicit def restMetadata[Ctx]: RestMetadata[Real[Ctx]] = reusableRestMetadata.asInstanceOf[RestMetadata[Real[Ctx]]]

    private lazy val reusableOpenApiMetadata: OpenApiMetadata[Client] = instances(implicits, this).openapiMetadata
    implicit def openapiMetadata[Ctx]: OpenApiMetadata[Real[Ctx]] =
      reusableOpenApiMetadata.asInstanceOf[OpenApiMetadata[Real[Ctx]]]
  }
}

/**
 * Ready-to-use entry point for [[AbstractContextualServerAndClientRestApis]]. Extend it with an `object`,
 * fixing the implicits bundle, e.g.
 * {{{
 *   object MyImplicits extends DefaultRestImplicits
 *   object MyRestApis extends ContextualServerAndClientRestApis[MyImplicits.type](MyImplicits)
 * }}}
 */
abstract class ContextualServerAndClientRestApis[Implicits](override protected val implicits: Implicits)
  extends AbstractContextualServerAndClientRestApis[Implicits]

/** Instances required by [[AbstractContextualServerAndClientRestApis.ApiCompanion]] for a `Real[Ctx]` API. */
trait GenericContextualApiInstances[Real[Ctx]] {
  def restAsRaw[Ctx](implicit ctx: Ctx): RawRest.AsRawRpc[Real[Ctx]]
  def restAsReal: RawRest.AsRealRpc[Real[NoCtx]]
  def restMetadata[Ctx]: RestMetadata[Real[Ctx]]
  def openapiMetadata[Ctx]: OpenApiMetadata[Real[Ctx]]
}
