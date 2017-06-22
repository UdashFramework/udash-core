package io.udash.rest

import com.avsystem.commons.rpc.MetadataAnnotation

/** Marker trait for REST interfaces. */
class REST extends io.udash.rpc.RPC

/** Annotated method name will be skipped in the REST path.
  * This annotation cannot be used in interface which you are going to expose.
  * It should be used in case of wrapping external API. */
class SkipRESTName extends MetadataAnnotation
/** Forces name of a method used in the interface to REST mapping.
  * This annotation has no effect on exposed interface, it should be used in case of wrapping external API.
  * If you want to overwrite method name in exposed interface you should use @RPCName annotation. */
class RESTName(val restName: String) extends MetadataAnnotation
/** Forces name of an argument used in the interface to REST mapping. It also affects exposed interfaces. */
class RESTParamName(val restName: String) extends MetadataAnnotation

sealed trait RESTMethod
/** Annotated method will be send using `GET` HTTP method. */
class GET extends MetadataAnnotation with RESTMethod
/** Annotated method will be send using `POST` HTTP method. */
class POST extends MetadataAnnotation with RESTMethod
/** Annotated method will be send using `PATCH` HTTP method. */
class PATCH extends MetadataAnnotation with RESTMethod
/** Annotated method will be send using `PUT` HTTP method. */
class PUT extends MetadataAnnotation with RESTMethod
/** Annotated method will be send using `DELETE` HTTP method. */
class DELETE extends MetadataAnnotation with RESTMethod

sealed trait ArgumentType
/** Annotated argument will be send as an URL part, eg. /method/{value}. */
class URLPart extends MetadataAnnotation with ArgumentType
/** Annotated argument will be send as a query argument, eg. /method/?arg=value. */
class Query extends MetadataAnnotation with ArgumentType
/** Annotated argument will be send as a body part, eg. /method/ JSON(value). */
class Body extends MetadataAnnotation with ArgumentType // TODO support for body-part arguments
/** Annotated argument will be send as a header. */
class Header extends MetadataAnnotation with ArgumentType