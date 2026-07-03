package io.udash.rest

import com.avsystem.commons.meta.MacroInstances
import io.udash.rest.WithCtx.NoCtx
import io.udash.rest.openapi.OpenApiMetadata
import io.udash.rest.raw.{RawRest, RestMetadata}
import monix.eval.Task

/** TODO doc: why trait: allow client apps to create more custom companions */
trait AbstractContextualServerAndClientRestApis[Implicits] extends ApiDataWithCustomImplicits[Implicits] {

  trait Api[Ctx] {
    type CtxTask[+T] = WithCtx[Ctx, Task[T]]
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
   *   // reusable boilerplate
   *   trait MyRestImplicits extends MonixRestImplicits
   *   object MyRestImplicits extends MyRestImplicits
   *
   *   object MyRestApis extends ContextualServerAndClientRestApis[MyRestImplicits](MyRestImplicits)
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

/** TODO doc */
abstract class ContextualServerAndClientRestApis[Implicits](override protected val implicits: Implicits)
  extends AbstractContextualServerAndClientRestApis[Implicits]

trait GenericContextualApiInstances[Real[Ctx]] {
  def restAsRaw[Ctx](implicit ctx: Ctx): RawRest.AsRawRpc[Real[Ctx]]
  def restAsReal: RawRest.AsRealRpc[Real[NoCtx]]
  def restMetadata[Ctx]: RestMetadata[Real[Ctx]]
  def openapiMetadata[Ctx]: OpenApiMetadata[Real[Ctx]]
}
