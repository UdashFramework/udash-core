package io.udash
package rest

import com.avsystem.commons.annotation.{AnnotationAggregate, defaultsToName}
import com.avsystem.commons.rpc._
import io.udash.rest.raw._

import scala.annotation.StaticAnnotation

/**
  * Base trait for tag annotations that determine how a REST method is translated into actual HTTP request.
  * A REST method may be annotated with one of HTTP method tags ([[GET]], [[PUT]], [[POST]], [[PATCH]], [[DELETE]])
  * which means that this method represents actual HTTP call and is expected to return a `AsyncWrapper[Result]` where
  * `Result` is encodable as [[RestResponse]] and `AsyncWrapper` represents some abstraction over asynchronous
  * computations (`Future` by default - see [[DefaultRestApiCompanion]]).
  *
  * If a REST method is not annotated with any of HTTP method tags, then either [[POST]] is assumed (if result type
  * is a valid result type for HTTP method) or [[Prefix]] is assumed (if result type is another REST trait).
  * [[Prefix]] means that this method only contributes to URL path, HTTP headers and query parameters but does not yet
  * represent an actual HTTP request. Instead, it is expected to return instance of some other REST API trait
  * which will ultimately determine the actual HTTP call.
  */
sealed trait RestMethodTag extends RpcTag {
  /**
    * HTTP URL path segment associated with REST method annotated with this tag. This path may be multipart
    * (i.e. contain slashes). It may also be empty which means that this particular REST method does not contribute
    * anything to URL path. Any special characters must already be URL-encoded. If path is not specified explicitly,
    * method name is used (the actual method name, not `rpcName`).
    *
    * @example
    * {{{
    *   trait SomeRestApi {
    *     @GET("users/find")
    *     def findUser(userId: String): Future[User]
    *   }
    *   object SomeRestApi extends RestApiCompanion[SomeRestApi]
    * }}}
    */
  @defaultsToName def path: String
}
object RestMethodTag {
  /**
    * Used as fake default value for `path` parameter. Replaced with actual method name by annotation processing
    * in RPC macro engine.
    */
  def methodName: String = throw new NotImplementedError("stub")
}

/**
  * Base class for [[RestMethodTag]]s representing actual HTTP methods, as opposed to [[Prefix]] methods.
  */
sealed abstract class HttpMethodTag(val method: HttpMethod) extends RestMethodTag with AnnotationAggregate

/**
  * Base trait for annotations representing HTTP methods which may define a HTTP body. This includes
  * [[PUT]], [[POST]], [[PATCH]] and [[DELETE]]. Parameters of REST methods annotated with one of these tags are
  * by default serialized into JSON (through encoding to [[JsonValue]]) and combined into JSON object that is sent
  * as HTTP body.
  *
  * Parameters may also contribute to URL path, HTTP headers and query parameters if annotated as
  * [[Path]], [[Header]] or [[Query]].
  *
  * REST method may also take a single parameter representing the entire HTTP body. Such parameter must be annotated
  * as [[Body]] and must be the only body parameter of that method. Value of this parameter will be encoded as
  * [[HttpBody]] which doesn't necessarily have to be JSON (it may define its own MIME type).
  *
  * @example
  * {{{
  *   trait SomeRestApi {
  *     @POST("users/create") def createUser(@Body user: User): Future[Unit]
  *     @PATCH("users/update") def updateUser(id: String, name: String): Future[User]
  *   }
  *   object SomeRestApi extends RestApiCompanion[SomeRestApi]
  * }}}
  */
sealed abstract class BodyMethodTag(method: HttpMethod) extends HttpMethodTag(method)

/**
  * REST method annotated as `@GET` will translate to HTTP GET request. By default, parameters of such method
  * are translated into URL query parameters (encoded as [[QueryValue]]). Alternatively, each parameter
  * may be annotated as [[Path]] or [[Header]] which means that it will be translated into HTTP header value
  *
  * @param path see [[RestMethodTag.path]]
  */
class GET(val path: String = RestMethodTag.methodName) extends HttpMethodTag(HttpMethod.GET) {
  @rpcNamePrefix("get_", overloadedOnly = true) type Implied
}

/**
  * See [[BodyMethodTag]].
  * This is the default tag for untagged methods which are not recognized as [[Prefix]] methods
  * (i.e. their result type is not another REST trait).
  */
class POST(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.POST) {
  @rpcNamePrefix("post_", overloadedOnly = true) type Implied
}
/** See [[BodyMethodTag]] */
class PATCH(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.PATCH) {
  @rpcNamePrefix("patch_", overloadedOnly = true) type Implied
}
/** See [[BodyMethodTag]] */
class PUT(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.PUT) {
  @rpcNamePrefix("put_", overloadedOnly = true) type Implied
}
/** See [[BodyMethodTag]] */
class DELETE(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.DELETE) {
  @rpcNamePrefix("delete_", overloadedOnly = true) type Implied
}

/**
  * Causes the body parameters of a HTTP REST method to be encoded as `application/x-www-form-urlencoded`.
  * Each parameter value itself will be first serialized to [[QueryValue]].
  * This annotation only applies to methods which include HTTP body (i.e. not `GET`) and it must not be
  * a method with a single body parameter ([[Body]]). Methods with single body parameter can send their body
  * as `application/x-www-form-urlencoded` by defining custom serialization of its parameter into [[HttpBody]].
  */
class FormBody extends StaticAnnotation

/**
  * REST methods annotated as [[Prefix]] are expected to return another REST API trait as their result.
  * They do not yet represent an actual HTTP request but contribute to URL path, HTTP headers and query parameters.
  *
  * By default, parameters of a prefix method are interpreted as URL path fragments. Their values are encoded as
  * [[PathValue]] and appended to URL path. Alternatively, each parameter may also be explicitly annotated as
  * [[Header]] or [[Query]].
  *
  * NOTE: REST method is interpreted as prefix method by default which means that there is no need to apply [[Prefix]]
  * annotation explicitly unless you want to specify a custom path.
  *
  * @param path see [[RestMethodTag.path]]
  */
class Prefix(val path: String = RestMethodTag.methodName) extends RestMethodTag

sealed trait RestParamTag extends RpcTag
object RestParamTag {
  /**
    * Used as fake default value for `name` parameter. Replaced with actual param name by annotation processing
    * in RPC macro engine.
    */
  def paramName: String = throw new NotImplementedError("stub")
}

sealed trait NonBodyTag extends RestParamTag {
  def isPath: Boolean = this match {
    case _: Path => true
    case _ => false
  }
  def isHeader: Boolean = this match {
    case _: Header => true
    case _ => false
  }
  def isQuery: Boolean = this match {
    case _: Query => true
    case _ => false
  }
}
sealed trait BodyTag extends RestParamTag

/**
  * REST method parameters annotated as [[Path]] will be encoded as [[PathValue]] and appended to URL path, in the
  * declaration order. Parameters of [[Prefix]] REST methods are interpreted as [[Path]] parameters by default.
  */
class Path(val pathSuffix: String = "") extends NonBodyTag

/**
  * REST method parameters annotated as [[Header]] will be encoded as [[HeaderValue]] and added to HTTP headers.
  * Header name must be explicitly given as argument of this annotation.
  */
class Header(override val name: String)
  extends rpcName(name) with NonBodyTag

/**
  * REST method parameters annotated as [[Query]] will be encoded as [[QueryValue]] and added to URL query
  * parameters. Parameters of [[GET]] REST methods are interpreted as [[Query]] parameters by default.
  */
class Query(@defaultsToName override val name: String = RestParamTag.paramName)
  extends rpcName(name) with NonBodyTag

/**
  * REST method parameters annotated as [[BodyField]] will be encoded as either [[JsonValue]] and combined into
  * a JSON object that will be sent as HTTP body. Body parameters are allowed only in REST methods annotated as
  * [[POST]], [[PATCH]], [[PUT]] or [[DELETE]]. Actually, parameters of these methods are interpreted as
  * [[BodyField]] by default which means that this annotation rarely needs to be applied explicitly.
  */
class BodyField(@defaultsToName override val name: String = RestParamTag.paramName)
  extends rpcName(name) with BodyTag

/**
  * REST methods that can send HTTP body ([[POST]], [[PATCH]], [[PUT]] and [[DELETE]]) may take a single
  * parameter annotated as [[Body]] which will be encoded as [[HttpBody]] and sent as the body of HTTP request.
  * Such a method may not define any other body parameters (although it may take additional [[Path]], [[Header]]
  * or [[Query]] parameters).
  *
  * The single body parameter may have a completely custom encoding to [[HttpBody]] which may define its own MIME type
  * and doesn't necessarily have to be JSON.
  */
final class Body extends BodyTag
