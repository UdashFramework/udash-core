package io.udash.rest

import com.avsystem.commons.rpc.MetadataAnnotation

/** Marker trait for REST interfaces. */
class REST extends io.udash.rpc.RPC

/** Forces name of a method or an argument used in the interface to REST mapping. */
class RESTName(val restName: String) extends MetadataAnnotation
class SkipRESTName extends MetadataAnnotation

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
class Body extends MetadataAnnotation with ArgumentType
/** Annotated argument will be send as a header. */
class Header extends MetadataAnnotation with ArgumentType