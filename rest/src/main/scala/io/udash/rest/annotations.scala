package io.udash
package rest

import com.avsystem.commons.annotation.{AnnotationAggregate, defaultsToName}
import com.avsystem.commons.rpc._
import io.udash.rest.raw._

/**
  * Base trait for tag annotations that determine how a REST method is translated into actual HTTP request.
  * A REST method may be annotated with one of HTTP method tags ([[io.udash.rest.GET GET]], [[io.udash.rest.PUT PUT]],
  * [[io.udash.rest.POST POST]], [[io.udash.rest.PATCH PATCH]], [[io.udash.rest.DELETE DELETE]])
  * which means that this method represents actual HTTP call and is expected to return a `AsyncWrapper[Result]` where
  * `Result` is encodable as [[io.udash.rest.raw.RestResponse RestResponse]] and `AsyncWrapper` represents some
  * abstraction over asynchronous computations (`Future` by default - see
  * [[io.udash.rest.DefaultRestApiCompanion DefaultRestApiCompanion]]).
  *
  * If a REST method is not annotated with any of HTTP method tags, then either [[io.udash.rest.POST POST]] is
  * assumed (if result type is a valid result type for HTTP method) or [[io.udash.rest.Prefix Prefix]] is assumed
  * (if result type is another REST trait). [[io.udash.rest.Prefix Prefix]] means that this method only contributes
  * to URL path, HTTP headers and query parameters but does not yet represent an actual HTTP request.
  * Instead, it is expected to return instance of some other REST API trait which will ultimately determine the
  * actual HTTP call.
  */
sealed trait RestMethodTag extends RpcTag {
  /**
    * HTTP URL path segment associated with REST method annotated with this tag. This path may be multipart
    * (i.e. contain slashes). It may also be empty which means that this particular REST method does not contribute
    * anything to URL path. Any special characters must already be URL-encoded (spaces should be encoded as `%20`,
    * not as `+`). If path is not specified explicitly, method name is used (the actual method name, not `rpcName`).
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
  * Base class for [[io.udash.rest.RestMethodTag RestMethodTag]]s representing actual HTTP methods, as opposed to
  * [[io.udash.rest.Prefix Prefix]] methods.
  */
sealed abstract class HttpMethodTag(val method: HttpMethod) extends RestMethodTag with AnnotationAggregate

/**
  * Base trait for annotations representing HTTP methods which may define a HTTP body. This includes
  * [[io.udash.rest.PUT PUT]], [[io.udash.rest.POST POST]], [[io.udash.rest.PATCH PATCH]] and
  * [[io.udash.rest.DELETE DELETE]]. Parameters of REST methods annotated with one of these tags are by default
  * serialized into JSON (through encoding to [[io.udash.rest.raw.JsonValue JsonValue]]) and combined into JSON
  * object that is sent as HTTP body.
  *
  * Parameters may also contribute to URL path, HTTP headers and query parameters if annotated with
  * [[io.udash.rest.Path Path]], [[io.udash.rest.Header Header]] or [[io.udash.rest.Query Query]].
  *
  * REST method may also take a single parameter representing the entire HTTP body. Such parameter must be annotated
  * as [[io.udash.rest.Body Body]] and must be the only body parameter of that method. Value of this parameter will
  * be encoded as [[io.udash.rest.raw.HttpBody HttpBody]] which doesn't necessarily have to be JSON
  * (it may define its own MIME type).
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
  * REST method annotated with `@GET` will translate to HTTP GET request. By default, parameters of such method
  * are translated into URL query parameters (encoded as [[io.udash.rest.raw.QueryValue QueryValue]]).
  * Alternatively, each parameter may be annotated with [[io.udash.rest.Path Path]] or [[io.udash.rest.Header Header]]
  * which means that it will be translated into HTTP header value.
  *
  * @param path see [[RestMethodTag.path]]
  */
class GET(val path: String = RestMethodTag.methodName) extends HttpMethodTag(HttpMethod.GET) {
  @rpcNamePrefix("get_", overloadedOnly = true) type Implied
}

/**
  * See [[io.udash.rest.BodyMethodTag BodyMethodTag]].
  * This is the default tag for untagged methods which are not recognized as [[io.udash.rest.Prefix Prefix]] methods
  * (i.e. their result type is not another REST trait).
  */
class POST(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.POST) {
  @rpcNamePrefix("post_", overloadedOnly = true) type Implied
}
/** See [[io.udash.rest.BodyMethodTag BodyMethodTag]] */
class PATCH(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.PATCH) {
  @rpcNamePrefix("patch_", overloadedOnly = true) type Implied
}
/** See [[io.udash.rest.BodyMethodTag BodyMethodTag]] */
class PUT(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.PUT) {
  @rpcNamePrefix("put_", overloadedOnly = true) type Implied
}
/** See [[io.udash.rest.BodyMethodTag BodyMethodTag]] */
class DELETE(val path: String = RestMethodTag.methodName) extends BodyMethodTag(HttpMethod.DELETE) {
  @rpcNamePrefix("delete_", overloadedOnly = true) type Implied
}

/**
  * Base trait for tag annotations which specify how a HTTP body is built for invocation of particular
  * method. The default one is [[io.udash.rest.JsonBody JsonBody]].
  */
sealed trait BodyTypeTag extends RpcTag

/**
  * Indicates that a HTTP REST method takes no body. This annotation is assumed by default
  * for [[io.udash.rest.GET GET]] and [[io.udash.rest.Prefix Prefix]] methods. There should be no reason to use it
  * explicitly.
  */
class NoBody extends BodyTypeTag

sealed trait SomeBodyTag extends BodyTypeTag

/**
  * Causes the [[io.udash.rest.Body Body]] parameters of a HTTP REST method to be encoded as `application/json`.
  * Each parameter value itself will be first serialized to [[io.udash.rest.raw.JsonValue JsonValue]].
  * This annotation only applies to methods which may include HTTP body (i.e. not [[io.udash.rest.GET GET]])
  * and is assumed by default, so there should be no reason to apply it explicitly.
  */
class JsonBody extends SomeBodyTag

/**
  * Causes the [[io.udash.rest.Body Body]] parameters of a HTTP REST method to be encoded as
  * `application/x-www-form-urlencoded`. Each parameter value itself will be first serialized to
  * [[io.udash.rest.raw.QueryValue QueryValue]].
  * This annotation only applies to methods which may include HTTP body (i.e. not [[io.udash.rest.GET GET]]).
  */
class FormBody extends SomeBodyTag

/**
  * Requires that a method takes exactly one [[io.udash.rest.Body Body]] parameter which serializes directly into
  * [[io.udash.rest.raw.HttpBody HttpBody]]. Serialization may then use arbitrary body format.
  * This annotation only applies to methods which may include HTTP body (i.e. not [[io.udash.rest.GET GET]]).
  */
class CustomBody extends SomeBodyTag

/**
  * REST methods annotated with [[io.udash.rest.Prefix Prefix]] are expected to return another REST API trait as their
  * result. They do not yet represent an actual HTTP request but contribute to URL path, HTTP headers and query
  * parameters.
  *
  * By default, parameters of a prefix method are interpreted as URL path fragments. Their values are encoded as
  * [[io.udash.rest.raw.PathValue PathValue]] and appended to URL path. Alternatively, each parameter may also be
  * explicitly annotated with [[io.udash.rest.Header Header]] or [[io.udash.rest.Query Query]].
  *
  * NOTE: REST method is interpreted as prefix method by default which means that there is no need to apply
  * [[io.udash.rest.Prefix Prefix]] annotation explicitly unless you want to specify a custom path.
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

/**
  * REST method parameters annotated with [[io.udash.rest.Path Path]] will be encoded as
  * [[io.udash.rest.raw.PathValue PathValue]] and appended to URL path, in the declaration order.
  * Parameters of [[io.udash.rest.Prefix Prefix]] REST methods are interpreted as [[io.udash.rest.Path Path]]
  * parameters by default.
  */
class Path(val pathSuffix: String = "") extends NonBodyTag

/**
  * REST method parameters annotated with [[io.udash.rest.Header Header]] will be encoded as
  * [[io.udash.rest.raw.HeaderValue HeaderValue]] and added to HTTP headers.
  * Header name must be explicitly given as argument of this annotation.
  */
class Header(override val name: String)
  extends rpcName(name) with NonBodyTag

/**
  * REST method parameters annotated with [[io.udash.rest.Query Query]] will be encoded as
  * [[io.udash.rest.raw.QueryValue QueryValue]] and added to URL query parameters.
  * Parameters of [[io.udash.rest.GET GET]] REST methods are interpreted as [[io.udash.rest.Query Query]]
  * parameters by default.
  */
class Query(@defaultsToName override val name: String = RestParamTag.paramName)
  extends rpcName(name) with NonBodyTag

/**
  * REST method parameters annotated with [[io.udash.rest.Body Body]] will be used to build HTTP request body.
  * How exactly that happens depends on [[io.udash.rest.BodyTypeTag BodyTypeTag]] applied on a method. By default,
  * [[io.udash.rest.JsonBody JsonBody]] is assumed which means that body parameters will be combined into a single
  * JSON object sent as body.
  *
  * Body parameters are allowed only in REST methods annotated with [[io.udash.rest.POST POST]],
  * [[io.udash.rest.PATCH PATCH]], [[io.udash.rest.PUT PUT]] or [[io.udash.rest.DELETE DELETE]]. Actually, for these
  * methods, an unannotated parameter is assumed to be a body parameter by default. This means that there's usually
  * no reason to apply this annotation explicitly. It may only be useful when wanting to customize JSON/form field name
  * which this annotation takes as its argument
  */
class Body(@defaultsToName override val name: String = RestParamTag.paramName)
  extends rpcName(name) with RestParamTag
