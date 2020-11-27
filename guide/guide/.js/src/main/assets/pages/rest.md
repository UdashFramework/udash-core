# Udash REST

Udash framework contains an RPC based REST framework for defining REST services using plain Scala traits.
It may be used for implementing both client and server side and works in both JVM and JS, as long as
appropriate network layer is implemented. By default, Udash provides Java Servlet based server
implementation and [sttp](https://github.com/softwaremill/sttp) based client implementation 
(which works in both JVM and JS).

Udash REST is a module completely independent of other parts of Udash.
This means you can use it as a pure REST library without bringing unwanted stuff
into your dependencies (e.g. UI related modules).

## Table of contents

[TOC levels=2-4]

## Overview

Udash REST:

* Provides automatic translation of **plain Scala traits** into REST endpoints
  * Lets you cover your web endpoint with nice, typesafe, well organized, IDE-friendly language-level interface.
  * Forms a type safety layer between the client and the server
* Gives you a set of annotations for adjusting how the translation into an HTTP endpoint happens.
* Statically validates your trait, emitting **detailed and readable compilation errors** in case anything is wrong.
* Uses typeclass-based, boilerplate free, pluggable and extensible serialization. You can easily integrate your
  favorite serialization library into it.
* Uses pluggable and extensible effects for asynchronous IO. You can easily integrate your favorite async 
  IO effect with it, be it `Future`, Monix `Task`, one of the `IO` monad implementations, etc. Blocking API is 
  also possible.
* Is agnostic about being purely functional or not. You can use it with both programming styles.
* Automatically generates **OpenAPI** documents for your APIs.
* Has multiple ways of adjusting generated OpenAPI definition
  * Provides a set of standard adjusting annotations, e.g. `@description`
  * Lets you define your own adjusting annotations which may perform arbitrary modifications
  * Gives you a nice, case class based representation of OpenAPI document which can be modified programmatically
* Uses pluggable network layer. You can easily integrate it with your favorite HTTP client and server.

## Quickstart example

### Project setup

First, make sure appropriate dependencies are configured for your project.
Udash REST provides Servlet-based implementation for REST servers but a servlet must be run inside an HTTP server. 
In this example we will use [Jetty](https://www.eclipse.org/jetty/) for that purpose.

```scala
val udashVersion: String = ??? // appropriate version of Udash here
val jettyVersion: String = ??? // appropriate version of Jetty here
libraryDependencies ++= Seq(
  "io.udash" %% "udash-rest" % udashVersion
  "org.eclipse.jetty" % "jetty-server" % jettyVersion,
  "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
)
```

### The API trait

Then, define your REST API trait along with data types that it uses. These will be shared between
client and server code. If your client is in ScalaJS then this code will be cross compiled for JVM and JS.

```scala
import io.udash.rest._

case class UserId(id: String) extends AnyVal
object UserId extends RestDataWrapperCompanion[String, UserId]

case class User(id: UserId, name: String, birthYear: Int)
object User extends RestDataCompanion[User]

trait UserApi {
  /** Returns newly created user */
  def createUser(name: String, birthYear: Int): Future[User]
}
object UserApi extends DefaultRestApiCompanion[UserApi]
```

### Server

Then, implement your trait on server side:

```scala
import scala.concurrent.Future

class UserApiImpl extends UserApi {
  def createUser(name: String, birthYear: Int): Future[User] =
    Future.successful(User(UserId(s"$name-ID"), name, birthYear))
}
```

and expose it on localhost port 9090 using Jetty:

```scala
import io.udash.rest.RestServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

object ServerMain {
  def main(args: Array[String]): Unit = {
    // translate UserApiImpl into a Servlet
    val userApiServlet = RestServlet[UserApi](new UserApiImpl)
  
    // do all the Jetty related plumbing
    val server = new Server(9090)
    val handler = new ServletContextHandler
    handler.addServlet(new ServletHolder(userApiServlet), "/*")
    server.setHandler(handler)
    server.start()
    server.join()
  }
}
```

### Client

On the client side, obtain a client proxy for your API and make a call:

```scala
import com.softwaremill.sttp.SttpBackend
import io.udash.rest.SttpRestClient

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object ClientMain {
  def main(args: Array[String]): Unit = {
    // allocate an STTP backend
    implicit val sttpBackend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()
    
    // obtain a "proxy" instance of UserApi
    val client: UserApi = SttpRestClient[UserApi]("http://localhost:9090/")

    // make a remote REST call
    val result: Future[User] = client.createUser("Fred", 1990)

    // use whatever execution context is appropriate
    import scala.concurrent.ExecutionContext.Implicits.global

    // do something with the result
    result.onComplete {
      case Success(user) => println(s"User ${user.id} created")
      case Failure(cause) => cause.printStackTrace()
    }

    // just wait until the Future is complete so that main thread doesn't finish prematurely
    Await.ready(result, 10.seconds)
  }
}
```

### Resulting HTTP

If we look at HTTP traffic created by the previous example, that's what we'll see:

Request:
```
POST http://localhost:9090/createUser HTTP/1.1
Accept-Encoding: gzip
User-Agent: Jetty/9.3.23.v20180228
Host: localhost:9090
Content-Type: application/json;charset=utf-8
Content-Length: 32

{"name":"Fred","birthYear":1990}
```

Response:
```
HTTP/1.1 200 OK
Date: Wed, 18 Jul 2018 11:43:08 GMT
Content-Type: application/json;charset=utf-8
Content-Length: 47
Server: Jetty(9.3.23.v20180228)

{"id":"Fred-ID","name":"Fred","birthYear":1990}
```

## REST API traits

As we saw in the quickstart example, REST API is defined with a plain Scala trait.
This approach is analogous to various well-established REST frameworks for other languages, e.g. JAX-RS for Java.
However, such frameworks are usually based on runtime reflection while in Scala it can be
done using compile-time reflection through macros which offers several advantages:

* platform independence - REST traits are understood by both ScalaJVM and ScalaJS
* full type information - compile-time reflection is not limited by type erasure
* type safety - compile-time reflection can perform thorough validation of REST traits and
  raise compilation errors in case anything is wrong
* pluggable typeclass based serialization - for serialization of REST parameters and results,
  typeclasses are used which also offers strong compile-time safety. If any of your parameters or
  method results cannot be serialized, a detailed compilation error will be raised.
* significantly better annotation processing

### Companion objects

In order for a trait to be understood as REST API, it must have a well defined companion object that contains
appropriate implicits:

* in order to expose REST API on a server, implicit instances of `RawRest.AsRawRpc` and `RestMetadata` for API trait are required.
* in order to use REST API client, implicit instances of `RawRest.AsRealRpc` and `RestMetadata` for API trait are required.
* when API trait is used by both client and server, `RawRest.AsRawRpc` and `RawRest.AsRealRpc` may be provided by a single
  combined instance of `RawRest.AsRawRealRpc` for API trait.
* additionally, if you want to [generate OpenAPI documents](#generating-openapi-30-specifications) then you need an instance of `OpenApiMetadata`

Usually there is no need to declare these implicit instances manually because you can use one of the convenience
base classes for REST API companion objects, e.g.

```scala
import io.udash.rest._

trait MyApi { ... }
object MyApi extends DefaultRestApiCompanion[MyApi]
```

`DefaultRestApiCompanion` takes a magic implicit parameter generated by a macro which will effectively
materialize all the necessary typeclass instances mentioned earlier. The "`Default`" in its name means that
`DefaultRestImplicits` is used as a provider of serialization-related implicits. This effectively plugs
[`GenCodec`](https://github.com/AVSystem/scala-commons/blob/master/docs/GenCodec.md) as the default serialization
library and `Future` as the default asynchronous effect for method results. 
See [serialization](#serialization) for more details on customizing serialization.

`DefaultRestApiCompanion` provides all the implicit instances necessary for both the client and server.
If you intend to use your API trait only on the server or only on the client, you may want to use more lightweight
`DefaultRestClientApiCompanion` or `DefaultRestServerApiCompanion`. This may help you reduce the amount of macro
generated code and make compilation faster.

#### Manual declaration of implicits

On less frequent occasions you might be unable to use one of the companion base classes. This is usually necessary
when macro materialization requires some additional implicits or when your API trait takes type parameters.
The recommended way of dealing with this situation is to design your own version of base companion class specialized
for your use case. For more details on how to do this, consult the Scaladoc of 
[`MacroInstances`](https://github.com/AVSystem/scala-commons/blob/master/commons-core/src/main/scala/com/avsystem/commons/meta/MacroInstances.scala).

Ultimately, you can resort to declaring all the implicit instances manually (however, they will still be implemented 
with a macro). For example:

```scala
import io.udash.rest._
import io.udash.rest.raw._

trait GenericApi[T] {
  def process(value: T): Future[T]
}
object GenericApi {
  import DefaultRestImplicits._
  implicit def restAsRawReal[T: GenCodec]: RawRest.AsRawRealRpc[GenericApi[T]] = RawRest.materializeAsRawReal
  implicit def restMetadata[T]: RestMetadata[GenericApi[T]] = RestMetadata.materialize

  import openapi._
  implicit def openApiMetadata[T: RestSchema]: OpenApiMetadata[GenericApi[T]] = OpenApiMetadata.materialize
}
```

### Data types

When a data type is used in a REST API trait as parameter type or result type, it must also come with an appropriate
set of implicits. This includes [serialization-related implicits](#serialization-implicits-summary) and
[OpenAPI related implicits](#openapi-implicits-summary). Just like for traits, these implicits can be provided by
giving your data type a well-defined companion object.

#### `RestDataCompanion`

`RestDataCompanion` is a base class for companion objects of algebraic data types (case classes, sealed hierarchies)
used in REST API traits. This base class automatically derives instances of `GenCodec` (for serialization) and
`RestSchema` (for OpenAPI generation).

```scala
case class Address(city: String, zip: String)
object Address extends RestDataCompanion[Address]
```

#### `RestDataWrapperCompanion`

`RestDataWrapperCompanion` is a handy base companion class which you can use for data types which simply wrap
another type. It will establish a relation between the wrapping and wrapped types so that all REST-related implicits 
for the wrapping type are automatically derived from corresponding implicits for the wrapped type.

```scala
case class UserId(id: String) extends AnyVal
object UserId extends RestDataWrapperCompanion[String, UserId]
```

### Use of annotations

REST framework relies on annotations for customization of REST API traits. All annotations are governed by
the same [annotation processing](https://github.com/AVSystem/scala-commons/blob/master/docs/Annotations.md) rules
and extensions, implemented by the underlying macro engine from 
[AVSystem Commons](https://github.com/AVSystem/scala-commons) library.
To use annotations more effectively and with less boilerplate, it is highly recommended to be familiar with these rules.

The most important feature of annotation processing engine is an ability to create 
[`AnnotationAggregate`s](http://avsystem.github.io/scala-commons/api/com/avsystem/commons/annotation/AnnotationAggregate.html). An annotation aggregate is a user-defined annotation which effectively applies a bunch of other annotations.
This is a primary mechanism of code reuse in the area of annotations. It lets you significantly reduce annotation
related boilerplate.

### HTTP REST methods

REST macro engine inspects an API trait and looks for all abstract methods. It then tries to translate every abstract
method into an HTTP REST call.

* By default (if not annotated explicitly) each method is interpreted as HTTP `POST`.
* Method name is appended to the URL path. This can also be customized with annotations.
* Every parameter is interpreted as part of the body - by default all the body parameters will be
  combined into a JSON object sent through HTTP body. If your method is annotated with [`@GET`](#get-methods)
  then it cannot send a body and method parameters are interpreted as query parameters rather than body fields.
  You may also use other body formats by annotating your method as [`@FormBody`](#formbody) or 
  [`@CustomBody`](#custombody).
* Result type of each method is typically expected to be a `Future` wrapping some
  arbitrary response type. This response type will be serialized into HTTP response which
  by default uses JSON for response body and creates a `200 OK` response with `application/json`
  content type. If response type is `Unit` (method result type is `Future[Unit]`) then a `204 No Content`
  response with empty body is created when serializing and body is ignored when deseriarlizing.
* Each method may also throw a `HttpErrorException` (or return failed `Future` with it). It will be
  automatically translated into appropriate HTTP error response with given status code and
  plaintext message.

For details on how exactly serialization works and how to customize it, see [serialization](#serialization).
Note that if you don't want to use `Future`, this customization allows you to use other wrappers for method 
result types. Through customized serialization it is also possible to signal HTTP errors without relying on
`HttpErrorException` or generally on throwing exceptions. This way you can customize the framework for more purely 
functional programming style.

### Choosing the HTTP method

As mentioned earlier, each trait method is by default translated into a `POST` request.
You can specify which HTTP method you want by explicitly annotating trait method as
`@GET`/`@POST`/`@PATCH`/`@PUT` or `@DELETE` (from `io.udash.rest` package).

```scala
@DELETE def deleteUser(id: UserId): Future[Unit]
```

Currently it is not possible define methods to handle `HEAD`, `OPTIONS`, `TRACE` and `CONNECT`
HTTP methods. However, `HEAD` and `OPTIONS` are handled automatically. `HEAD` requests are
handled in exactly the same way as `GET` requests except the body is ultimately stripped from
the response. `OPTIONS` is handled by returning a `200 OK` empty response with an
`Allow` header containing the list of allowed HTTP methods on given path. For example, if you
have `@GET` and `@POST` annotated methods in your REST API trait for some path, `OPTIONS` request
for this path will return `GET,HEAD,POST,OPTIONS`.

#### `GET` methods

Trait method annotated with `@GET` is interpreted somewhat differently from other HTTP methods.
Its parameters are interpreted as _query_ parameters rather than _body_ parameters. For example:

```scala
@GET def getUsername(id: UserId): Future[String]
```

Calling `getUsername("ID")` on the client will result in HTTP request:

```
GET http://localhost:9090/getUsername?userId=ID HTTP/1.1
Accept-Encoding: gzip
User-Agent: Jetty/9.3.23.v20180228
Host: localhost:9090

```

`@GET` annotated methods are also invoked to handle `HEAD` HTTP requests.
However, response to such request will have its body ultimately stripped.

### Customizing paths

By default, method name is appended to URL path when translating method call to HTTP request.
This can be customized. Every annotation specifying HTTP method (e.g. `GET`) takes an optional
`path` argument that you can use to customize your path:

```scala
@GET("username") def getUsername(id: UserId): Future[String]
```

The specified path may be multipart (it may contain slashes) or it may even be empty.
However, for server-side APIs all paths must be unambiguous, i.e. there must not be more
than one method translating to the same path. This is validated in runtime, upon
creating a server.

Empty paths may be especially useful for [prefix methods](#prefix-methods).

### Parameter flavors

#### Path parameters

If a parameter of REST API trait method is annotated with `@Path`, its value is appended to URL path.

```scala
@GET("username") def getUsername(@Path id: UserId): Future[String]
```

Calling `getUsername("ID")` will make an HTTP request on path `username/ID`.

If there are multiple `@Path` parameters, their values are appended to the path in the
order of declaration. Each path parameters may also optionally specify a _path suffix_ that
will be appended to path after value of each parameter:

```scala
@GET("users") def getUsername(@Path(pathSuffix = "name") id: UserId): Future[String]
```

Calling `getUsername("ID")` will make an HTTP request on path `users/ID/name`.

This way you can model completely arbitrary path patterns.

Values of path parameters are serialized into `PlainValue` objects.
See [serialization](#path-query-header-and-cookie-serialization) for more details.

#### Query parameters

You may explicitly request that some parameter is translated into URL query parameter
using `@Query` annotation. As mentioned earlier, parameters of `GET` methods are treated
as query parameters by default, so this annotation is necessary only for parameters of non-`GET` methods.

`@Query` annotation also takes optional `name` parameter which may be specified to customize
URL parameter name. If not specified, Scala parameter name is used.

Values of query parameters are serialized into `PlainValue` objects. 
See [serialization](#path-query-header-and-cookie-serialization) for more details.

#### Header parameters

You may also request that some parameter is translated into an HTTP header using `@Header` annotation.
It takes an obligatory `name` argument that specifies HTTP header name (case insensitive).

Values of header parameters are serialized into `PlainValue` objects.
See [serialization](#path-query-header-and-cookie-serialization) for more details.

#### Cookie parameters

You may also request that some parameter is translated into an HTTP cookie using `@Cookie` annotation.
It also takes optional `name` parameter which may be specified to customize cookie name. If not specified,
Scala parameter name is used.

#### Body parameters

Every parameter of an API trait method (except for `@GET`) is interpreted as a field of a JSON object sent as 
HTTP body. Just like for path, query, header and cookie parameters, there is a `@Body` annotation which requests this 
explicitly. However, the only reason to use it explicitly is in order to customize the name of JSON field.

Body parameters are serialized into `JsonValue` objects.
See [serialization](#body-parameter-serialization) for more details.

##### `@FormBody`

Non-`GET` methods may be annotated with `@FormBody`. This changes serialization of body parameters
from JSON object to HTTP form, encoded as `application/x-www-form-urlencoded`. Each body parameter
is then serialized into `PlainValue` rather than `JsonValue`.

##### `@CustomBody`

Methods annotated with `@CustomBody` are required to take **exactly one** body parameter. This body parameter will
be then serialized directly into `HttpBody`. This makes it possible to fully customize the way HTTP body is built.
See [`@CustomBody` serialization](#custom-body-serialization) for more details.

```scala
case class User(id: String, login: String)
object User extends RestDataCompanion[User]

@PUT @CustomBody def updateUser(user: User): Future[Unit]
```

#### Optional parameters

Instead of `@Query`, `@Header`, `@Cookie` and `@Body`, you can also use `@OptQuery`, `@OptHeader`, `@OptCookie` 
and `@OptBodyField` to make your parameters explicitly optional. The type of such a parameter must be wrapped into
an `Option`, `Opt`, `OptArg` or similar option-like wrapper, i.e.

```scala
@GET def pageContent(@OptQuery lang: Opt[String]): Future[String]
```

When `Opt.Empty` is passed, this query parameter will simply be omitted in the request.

Note how this is different from simply declaring a (non-optional) parameter typed as `Opt[String]`:

```scala
@GET def pageContent(@Query lang: Opt[String]): Future[String]
```

In the example above the framework will simply try to encode `Opt[String]` into
`PlainValue` (see [serialization](#path-query-header-and-cookie-serialization) for more details).
This will fail because an `Opt[String]` can't be unambiguously represented as a plain string - compilation
will fail because of lack of appropriate implicit (`AsRaw/AsReal[PlainValue, Opt[String]]`).

However, when the parameter is explicitly optional, the framework knows that the `Opt` is used to express
possible lack of this parameter while the actual type of that parameter (that needs to be serialized) is `String`.

### Prefix methods

Prefix methods are methods that return other REST API traits. They are useful for:

* capturing common path or path/query/header/cookie parameters in a single prefix call
* splitting your REST API into multiple smaller traits in order to organize it better

Just like HTTP API methods (`GET`, `POST`, etc.), prefix methods have their own
annotation that can be used explicitly when you want your trait method to be treated as
a prefix method. This annotation is `@Prefix` and just like HTTP method annotations, it
takes an optional `path` parameter. If you don't need to specify path explicitly then
annotation is not necessary as long as your method returns a valid REST API trait
(where "valid" is determined by presence of appropriate implicits -
see [companion objects](#companion-objects)).

Prefix methods may take path, header, query and cookie parameters. They cannot take body parameters.
By default, prefix method parameters are interpreted as `@Path` parameters.

Path and parameters collected by a prefix method will be prepended/added
to the HTTP request generated by an HTTP method call on the API trait returned by this
prefix method. This way prefix methods "contribute" to the final HTTP requests.

However, sometimes it may also be useful to create completely "transparent" prefix methods -
prefix methods with empty path and no parameters. This is useful when you simply want to refactor your
REST API trait by grouping methods into separate traits without changing the format of HTTP requests.

Example of prefix method that adds authorization header to the overall API:

```scala
trait UserApi { ... }
object UserApi extends DefaultRestApiCompanion[UserApi]

trait RootApi {
  @Prefix("") def auth(@Header("Authorization") token: String): UserApi
}
object RootApi extends DefaultRestApiCompanion[RootApi]
```

### Default parameter values

All parameters except for `@Path` parameters may accept a default value which
is picked up by REST framework macro engine and used as fallback value when actual value
is missing in the HTTP request. This is useful primarily for [API evolution](#api-evolution) -
it lets you add more parameters to your REST methods without breaking backwards compatibility
with clients not aware of these new parameters.

Assuming `GenCodec`-based serialization, default values may also be defined for fields of
case classes used as parameter types or result types of REST methods.

There are two ways to define default values:

* Scala-level default value

  You can simply use 
  [language level default parameter value](https://docs.scala-lang.org/tour/default-parameter-values.html)
  for your REST method parameters and case class parameters. They will be picked up during macro materialization and
  used as fallback values for missing parameters during deserialization. However, Scala-level default values cannot
  be picked up and included into [OpenAPI documents](#generating-openapi-30-specifications) due to how they are encoded
  by Scala compiler (obtaining such value requires an actual instance of API trait).
  Therefore, it's recommended to define default values using `@whenAbsent` annotation

* Using `@whenAbsent` annotation

  Instead of defining Scala-level default value, you can use `@whenAbsent` annotation:
  ```scala
  @GET def fetchUsers(@whenAbsent(".*") namePattern: String): List[User]
  ```
  This brings two advantages:
  * The default value is for deserialization _only_ and does not affect programmer API, which is often desired.
  * Value from `@whenAbsent` will be picked up by macro materialization of
    [OpenAPI documents](#generating-openapi-30-specifications) and included as default value in OpenAPI
    [Schema Objects](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject)

The two approaches can be mixed - you can define Scala-level default value and `@whenAbsent` annotation
at the same time. During deserialization, value from `@whenAbsent` annotation takes priority over Scala-level
default value. This way you can have different fallback value for deserialization and different one
for Scala programming API. And if you want these values to be the same, there's also a handy `whenAbsent.value`
macro which you can use to avoid writing the same default value twice:

```scala
@GET def fetchUsers(@whenAbsent(".*") namePattern: String = whenAbsent.value): List[User]
```

#### `@transientDefault`

If your REST method parameter or case class parameter has a default value defined, you can
also annotate it as `@transientDefault`. This way these parameters will be omitted during serialization
if their value is equal to the default value. During deserialization, of course, the default value
will be picked up for them. This way you can reduce the amount of network traffic by sending only
actually meaningful parameters.

## Serialization

REST macro engine must be able to generate code that serializes and deserializes
every parameter value and every method result into appropriate raw values which can
be easily sent through network. Serialization in REST framework is typeclass based,
which is a typical, functional and typesafe approach to serialization in Scala.

Examples of typeclass based serialization libraries include 
[GenCodec](https://github.com/AVSystem/scala-commons/blob/master/docs/GenCodec.md)
(which is the default serialization used by this REST framework), [circe](https://circe.github.io/circe/)
(one of the most popular JSON libraries for Scala) or [µPickle](http://www.lihaoyi.com/upickle/).
Any of these solutions can be plugged into REST framework.

### Real and raw values

Depending on the context where a type is used in a REST API trait, it will be serialized to a different
_raw value_:

* path/query/header parameters are serialized into `PlainValue`
* body parameters are serialized into `JsonValue` (by default), `PlainValue` (for [`@FormBody`](#formbody) methods)
  or directly into `HttpBody` (for [`@CustomBody`](#custombody) methods).
* Response types are serialized into `RestResponse`
* Prefix result types (other REST API traits) are "serialized" into an instance of `RawRest`.

When a macro needs to serialize a value of some type (let's call it `Real`) to one of these raw types
listed above (let's call it `Raw`) then it looks for an implicit instance of `AsRaw[Raw, Real]`.
In the same manner, implicit instance of `AsReal[Raw, Real]` is used for deserialization.
Additionally, an implicit instance of `AsRawReal[Raw, Real]` can serve as both.

These implicit instances may come from multiple sources:

* implicit scope of the `Raw` type (e.g. its companion object)
* implicit scope of the `Real` type (e.g. its companion object)
* implicits plugged by REST API trait companion
  (e.g. `DefaultRestApiCompanion` plugs in `DefaultRestImplicits`)
* imports

Of course, these implicits may also depend on other implicits which effectively means that
you can use whatever typeclass-based serialization library you want.
For example, you can define an instance of `AsRaw[JsonValue, Real]` which actually uses
`Encoder[Real]` from [circe](https://circe.github.io/circe/). 
See [Customizing serialization](#customizing-serialization) for more details.

### Serialization implicits summary

Below is a diagram that summarizes dependencies and defaults of implicits used to serialize parameters and results
of HTTP REST methods. `AsRaw/AsReal[Raw, Real]` indicates that the macro engine searches for either `AsRaw[Raw, Real]`
(for parameters on client side & results on server side) or `AsReal[Raw, Real]` (parameters on server side and
results on client side).

![REST implicits](assets/images/views/rest/serialization.svg)

### Path, query, header and cookie serialization

Path, query, header and cookie parameter values are serialized into `PlainValue` which is a simple `String` wrapper.
This means that the macro engine looks for an instance of `AsRaw[PlainValue, T]` and/or `AsReal[PlainValue, T]` for 
every parameter of type `T` (`AsRaw` for the client, `AsReal` for the server).

There are no "global" implicits defined for `PlainValue`. They must be either imported, defined by each
"real" type or plugged in by REST API trait companion. For example, the `DefaultRestApiCompanion` and its
variations automatically provide serialization to `PlainValue` based on `GenKeyCodec`
and additional instances for `Float` and `Double`. This effectively provides serialization for all the
primitive types, its Java boxed counterparts, `String`, all `NamedEnum`s, Java enums and `Timestamp`.
It's also easy to provide path/query/header serialization for any type which has a natural, unambiguous textual
representation.

Serialized values of path, query and cookie parameters are automatically URL-encoded when being embedded into
HTTP requests. This means that serialization should not worry about that.
URL-encoding is also applied to query and cookie parameter _names_, in both actual HTTP requests and
[OpenAPI documents](#generating-openapi-30-specifications).

### Body parameter serialization

Body parameters are by default serialized into `JsonValue` which is also a simple wrapper class over `String`,
but is importantly distinct from `PlainValue` because it must always contain a valid JSON string. 
This is required because JSON body parameters are ultimately composed into a single
JSON object sent as HTTP body. If a method is annotated with [`@FormBody`](#formbody), body parameters are 
serialized into `PlainValue` and combined into an URL-encoded form.

There are no "global" implicits defined for `JsonValue` - JSON serialization must be either imported,
defined by each "real" type manually or plugged in by REST API trait companion. For example,
`DefaultRestApiCompanion` and its variations automatically provide serialization to `JsonValue` based
on `GenCodec`, which means that if `DefaultRestApiCompanion` is used then every type used in REST API trait
that has a `GenCodec` instance will be serializable to `JsonValue`.

#### Custom body serialization

Body parameter of a [`@CustomBody`](#custombody) method is serialized straight into `HttpBody`, which encapsulates
not only raw content but also media type. This way you can define custom body serializations for your types and
you are not limited to `application/json` and `application/x-www-form-urlencoded`.

By default (if there are no more specific implicit instances defined),
serialization to `HttpBody` falls back to `JsonValue` and simply wraps JSON string into HTTP body
with `application/json` type. This means that all types serializable to `JsonValue` are automatically
serializable as `HttpBody`.

### Result serialization

Result type of every REST API method is wrapped into `Try` (in case the method throws an exception)
and translated into `Async[RestResponse]`. This means that the macro engine looks for an implicit instance of 
`AsRaw[Async[RestResponse], Try[R]]` and `AsReal[Async[RestResponse], Try[R]]` for every HTTP method with result type `R`.

* `Async` is a type alias defined in `RawRest` object.
  This is just a very raw, low-level representation of asynchronous computations which may be invoked many times. 
  It serves as a common denominator for all possible asynchronous abstractions like `Future`, Monix `Task`, etc.

* `RestResponse` itself is a simple class that aggregates HTTP status code, response headers and body.

`DefaultRestApiCompanion` and its friends introduce implicits which translate between `Async` and `Future`s.
This effectively means that if your method returns `Future[R]` then it's enough if `R` is serializable as `RestResponse`.

However, there are even more defaults provided: if `R` is serializable as `HttpBody` then it's automatically serializable
as `RestResponse`. This default translation of `HttpBody` into `RestResponse` always uses 200 as a status code
(or 204 for empty body) and empty response headers. When translating `RestResponse` into `HttpBody` and response
contains erroneous status code, `HttpErrorException` is thrown (which will be subsequently captured into failed `Future`).

Going even further with defaults, all types serializable as `JsonValue` are serializable as `HttpBody`.
This effectively means that when your method returns `Future[R]` then you can provide serialization
of `R` into any of the following: `JsonValue`, `HttpBody`, `RestResponse` - depending on how much control
you need. Also, remember that when using `DefaultRestApiCompanion`, `JsonValue` serialization is automatically
derived from `GenCodec` instance.

Ultimately, if you don't want to use `Future`s, you may replace it with some other asynchronous wrapper type,
e.g. Monix Task or some IO monad.
See [supporting result containers other than `Future`](#supporting-result-containers-other-than-future).

### Customizing serialization

#### Introduction

When Scala compiler needs to find an implicit, it searches two scopes: _lexical_ scope and _implicit_ scope.

_Lexical scope_ is made of locally visible and imported implicits. It has priority over implicit scope -
implicit scope is searched only when implicit could not be found in lexical scope.

_Implicit scope_ is made of companion objects of all traits and classes _associated_ with the
type of implicit being searched for. Consult
[Scala Language Specification](https://www.scala-lang.org/files/archive/spec/2.12/07-implicits.html)
for precise definition of the word "_associated_". As an example, implicit scope of type `AsRaw[JsonValue,MyClass]` is
made of companion objects of `AsRaw`, `JsonValue`, `MyClass` + companion objects of all supertraits, superclasses and
enclosing traits/classes of `MyClass`.

Implicits defined in _implicit scope_ are effectively global and don't need to be imported.

#### Customizing serialization for your own type

If you need to write manual serialization for your own type, the easiest way to do this is to
provide appropriate implicit in its companion object. In the example below, we provide custom serialization
to `JsonValue`:

```scala
class MyClass { ... }
object MyClass {
  implicit val jsonAsRawReal: AsRawReal[JsonValue, MyClass] = AsRawReal.create(...)
}
```

Apart from `JsonValue`, you can do the same for `PlainValue`, `HttpBody` and `RestResponse`, depending on the
level of control that you need.

**WARNING**: Remember that if you generate [OpenAPI documents](#generating-openapi-30-specifications) for your
REST API then you must also provide custom instance of one of the [OpenAPI typeclasses](#openapi-implicits-summary)
so that OpenAPI document properly reflects your custom serialization format. 

* If you have custom serialization to `JsonValue` or `PlainValue` then you should define custom 
  [`RestSchema`](#restschema-typeclass) instance
* If you have custom serialization to `HttpBody` then you should define custom 
  [`RestMediaTypes`](#restmediatypes-typeclass) instance
* If you have custom serialization to `RestResponse` then you should define custom
  [`RestResponses`](#restresponses-typeclass) instance

#### Providing serialization for third party type

If you need to define serialization implicits for a third party type, you can't do it through
implicit scope because you can't modify its companion object. Instead, you can adjust implicits injected
into REST API trait companion object.

Assume that companion objects of your REST API traits normally extend `DefaultRestApiCompanion`, i.e.
`GenCodec`-based serialization is used. Now, you can extend `DefaultRestImplicits` to add serialization for
third party types:

```scala
trait EnhancedRestImplicits extends DefaultRestImplicits {
  implicit val thirdPartyJsonAsRawReal: AsRawReal[JsonValue, ThirdParty] =
    AsRawReal.create(...)
}
object EnhancedRestImplicits extends EnhancedRestImplicits
```

Then, you need to define your REST API trait as:

```scala
trait MyRestApi { ... }
object MyRestApi extends RestApiCompanion[EnhancedRestImplicits, MyRestApi](EnhancedRestImplicits)
```

Also, if you generate [OpenAPI documents](#generating-openapi-30-specifications) for your
REST API then you must provide a [`RestSchema`](#restschema-typeclass) instance for your type that
will reflect its serialization format.

#### Plugging in entirely custom serialization

REST framework deliberately provides **no** default implicits for serialization and deserialization.
Instead, it introduces a mechanism through which serialization implicits are injected by
[companion objects](#companion-objects) of REST API traits. Thanks to this mechanism REST framework is
not bound to any specific serialization library. At the same time it provides a concise method to inject
serialization implicits that does not require importing them explicitly.

An example usage of this mechanism is `DefaultRestApiCompanion` which injects 
[`GenCodec`](https://github.com/AVSystem/scala-commons/blob/master/docs/GenCodec.md)-based
serialization.

Let's say you want to use e.g. [circe](https://circe.github.io/circe/) for serialization to `JsonValue`.

First, define a trait that contains implicits which translate Circe's `Encoder` and `Decoder` into
appropriate instances of `AsReal`/`AsRaw`:

```scala
import io.udash.rest._
import io.udash.rest.raw._
import com.avsystem.commons.rpc._
import io.circe._
import io.circe.parser._
import io.circe.syntax._

trait CirceRestImplicits {
  implicit def encoderBasedAsRawJson[T: Encoder]: Fallback[AsRaw[JsonValue, T]] =
    Fallback(AsRaw.create(v => JsonValue(v.asJson.noSpaces)))
  implicit def decoderBasedJsonAsReal[T: Decoder]: Fallback[AsReal[JsonValue, T]] =
    Fallback(AsReal.create(json => decode(json.value).fold(throw _, identity)))
}
object CirceRestImplicits extends CirceRestImplicits
```

Note that implicits are wrapped into `Fallback`. This is not strictly required, but it's recommended
because these implicits ultimately will have to be imported into *lexical scope* during macro materialization.
However, we don't want these implicits to have higher priority than implicits from the companion objects of some 
concrete classes which need custom (*implicit scope*). Because of that, we wrap our implicits into
`Fallback` which keeps them visible but without elevated priority. `Fallback` is then "unwrapped" by appropriate 
implicits defined in `AsRaw` and `AsReal` companion objects.

Now, in order to define a REST API trait that uses Circe-based serialization, you must appropriately
inject it into its companion object:

```scala
trait MyRestApi { ... }
object MyRestApi extends RestApiCompanion[CirceRestImplicits, MyRestApi](CirceRestImplicits)
```

If you happen to use this often (e.g. because you always want to use Circe) then it may be useful
to create convenience companion base class just for Circe:

```scala
abstract class CirceRestApiCompanion[Real](
  implicit inst: MacroInstances[CirceRestImplicits, FullInstances[Real]])
) extends RestApiCompanion[CirceRestImplicits, Real](CirceRestImplicits)
```

Now you can define your trait more concisely as:

```scala
trait MyRestApi { ... }
object MyRestApi extends CirceRestApiCompanion[MyRestApi]
```

**WARNING**: if you also generate [OpenAPI documents](#generating-openapi-30-specifications) for your
REST API, then along from custom serialization you must provide customized instances of
[`RestSchema`](#restschema-typeclass) that will adequately describe your new serialization format.

#### Supporting async effects other than `Future`

When using `DefaultRestApiCompanion` or one of its variations, every HTTP method in REST API trait must return 
its return wrapped into a `Future`. However, `Future` is low level and limited in many ways 
(e.g. there is no way to control when the actual asynchronous computation starts). 
It is possible to use other task-like containers, e.g. [Monix Task](https://monix.io/docs/2x/eval/task.html) 
or [Cats IO](https://typelevel.org/cats-effect/).

In order to do that, you must provide some additional implicits which will make the macro engine
understand how to translate between `Async[T]` and `MyFavoriteIOMonad[T]` for arbitrary type `T`. This is controlled by
`AsyncEffect` typeclass defined in `RawRest` object which represents a bidirectional polymorphic conversion between
some effect type constructor and `Async`. This means that you must provide implicit instance
of `AsyncEffect[MyFavoriteIOMonad]`.

Just like when [providing serialization for third party type](#providing-serialization-for-third-party-type),
you should put these implicits into a trait and inject them into REST API trait's companion object.

Udash repository contains an [example implementation of Monix Task support in its test sources](https://github.com/UdashFramework/udash-core/blob/master/rest/src/test/scala/io/udash/rest/monix/MonixRestImplicits.scala).

## API evolution

REST framework gives you a certain amount of guarantees about backwards compatibility of your API.
Here's a list of changes that you may safely do to your REST API traits without breaking clients
that still use the old version:

* Adding new REST methods, as long as paths are still the same and unambiguous.
* Renaming REST methods, as long as old `path` is configured for them explicitly (e.g. `@GET("oldname") def newname(...)`)
* Reordering parameters of your REST methods, except for `@Path` parameters which may be freely intermixed
  with other parameters but they must retain the same order relative to each other.
* Splitting parameters into multiple parameter lists or making them `implicit`.
* Extracting common path fragments or parameters into [prefix methods](#prefix-methods).
* Renaming `@Path` parameters - their names are not used in REST requests 
  (they are used when generating OpenAPI though)
* Renaming non-`@Path` parameters, as long as the previous name is explicitly configured by
  `@Query`, `@Header`, `@Cookie` or `@Body` annotation.
* Removing non-`@Path` parameters - even if the client sends them, the server will just ignore them.
* Adding new non-`@Path` parameters, as long as default value is provided for them - either as
  Scala-level default parameter value or by using `@whenAbsent` annotation. The server will simply
  use the default value if parameter is missing in incoming HTTP request.
* Changing parameter or result types or their serialization - as long as serialized formats of new and old type
  are compatible. This depends on on the serialization library you're using. If you're using `GenCodec`, consult
  [its documentation on retaining backwards compatibility](https://github.com/AVSystem/scala-commons/blob/master/docs/GenCodec.md#safely-introducing-changes-to-serialized-classes-retaining-backwards-compatibility).
  
Conversely, changes that would break your API include:
* Renaming REST methods without explicitly configuring path
* Renaming non-`@Path` parameters which don't have explicit name configured
* Adding or removing `@Path` parameters
* Adding non-`@Path` parameters without giving them default value
* Changing order of `@Path` parameters

## Implementing backends

Core REST framework has Servlet based server implementation and `sttp` based client implementation.
However, it's relatively easy to implement custom backends as most of the heavy-lifting is already done by the core
framework (its macro engine, in particular). Implementing a backend mostly boils down to translating between
representations of requests and responses and handling asynchronous computations.

### Handler function

`RawRest` object defines following type alias:

```scala
type HandleRequest = RestRequest => Async[RestResponse]
```

`RestRequest` is a simple, immutable representation of HTTP request. It contains HTTP method, path, URL
parameters, HTTP header values and a body (`HttpBody`). All data is already in serialized form so it can be
easily sent through network.

`RestResponse` is, similarly, a simple representation of HTTP response. `RestResponse` is made of HTTP status
code and HTTP body (`HttpBody`, which also contains media type).

`Async` is a type alias defined in `RawRest` object and serves as a low level representation of asynchronous,
repeatable computation.

```scala
type Callback[T] = Try[T] => Unit
type Async[T] = Callback[T] => Unit
```

Therefore, `Async[T]` resolves to `(Try[T] => Unit) => Unit` which means that it represents a consumer of a callback 
of possibly failed computation of value of type `T`. 

In other words, `HandleRequest` is a function which translates a `RestRequest` into an unexecuted, asynchronous
computation which yields a `RestResponse` when run.

### Implementing a server

An existing implementation of REST API trait can be easily turned into a `HandleRequest` 
function using `RawRest.asHandleRequest`.

Therefore, the only thing you need to do to expose your REST API trait as an actual web service it to turn
`HandleRequest` function into a server. This is usually just a matter of translating native HTTP request into
a `RestRequest`, passing them to `HandleRequest` function and translating resulting `RestResponse` to native 
HTTP response.

See [`RestServlet`](../rest/.jvm/src/main/scala/io/udash/rest/RestServlet.scala)
for an example implementation.

### Implementing a client

If you already have a `HandleRequest` function, you can easily turn it into an implementation of desired REST API trait
using `RawRest.fromHandleRequest`. This implementation is a macro-generated proxy which translates actual
method calls into invocations of provided `HandleRequest` function.

Therefore, the only thing you need to to in order to wrap a native HTTP client into a REST API trait instance is
to turn this native HTTP client into a `HandleRequest` function.

See [`DefaultRestClient`](../rest/src/main/scala/io/udash/rest/DefaultRestClient.scala) for
an example implementation.

## Generating OpenAPI 3.0 specifications

[OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md) is an open standard for describing
REST endpoints using JSON or YAML. It can be consumed by e.g. [Swagger UI](https://swagger.io/tools/swagger-ui/) in
order to generate nicely looking, human-readable documentation of a REST endpoint.

REST framework provides automatic generation of OpenAPI documents based on REST API traits.
In order to do this, `OpenApiMetadata` typeclass must be available for an API trait. If your trait's companion
extends `DefaultRestApiCompanion` or `DefaultRestServerApiCompanion` then `OpenApiMetadata` is automatically
materialized by a macro. You can then use it to generate OpenAPI specification document like this:

```scala
import io.udash.rest._

trait MyRestApi {
  ...
}
object MyRestApi extends DefaultRestApiCompanion[MyRestApi]

object PrintOpenApiJson {
  def main(args: Array[String]): Unit = {
    val openapi = MyRestApi.openapiMetadata.openapi(
      Info("Some REST API", "0.1", description = "Some example REST API"),
      servers = List(Server("http://localhost"))
    )
    println(JsonStringOutput.writePretty(openapi))
  }
}
```

### Operation IDs

For every HTTP REST method, an
[Operation Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject)
will be generated. `operationId` of that object will be by default set to HTTP method's `rpcName`.
`rpcName` itself defaults to method's regular name if not specified explicitly.

Additionally, if HTTP method is overloaded then all overloaded variants except for the first one will
have `rpcName` prepended with lowercased HTTP method followed by underscore (e.g. `post_`).

Finally, if HTTP method is a member of [prefix method](#prefix-methods)'s result, `operationId` will be further prepended with
the prefix method's `rpcName` followed by underscore.

All this behaviour may be customized with `@operationId` and `@operationIdPrefix` annotations.
It's important for operations to have sensible IDs because tools that generate client code in other programming
languages for REST API defined in OpenAPI documents often reuse operation IDs as method/function names that
map to REST requests.

Note that Operation object itself may be arbitrarily adjusted with other annotations -
see [adjusting operations](#adjusting-operations).

### OpenAPI implicits summary

Generation of OpenAPI documents is governed by multiple typeclasses. Below is a diagram which summarizes
their roles and dependencies between them.

![OpenAPI implicits](assets/images/views/rest/openapi.svg)

### `RestSchema` typeclass

In order to macro-materialize `OpenApiMetadata` for your REST API, you need to provide an instance of `RestSchema` typeclass
for every type used by your API (as a parameter or result type, i.e. when your method returns `Future[T]` then
`RestSchema[T]` will be needed). `RestSchema` contains a description of data type that is later translated into
[OpenAPI Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject).

Most of the primitive types, collection types, `Option`, `Opt`, etc. already have an appropriate `RestSchema` instance defined
(roughly the same set of simple types that have `GenCodec` automatically available). If `RestSchema` is not defined, there are
usually two ways to provide it:

#### Macro materialized ADT schemas

If your data type is an ADT (algebraic data type) - which means case class, object or sealed hierarchy - the easiest way
to provide schema is by making companion object of your data type extend `RestDataCompanion` - this will automatically
materialize a `RestStructure` instance for your data type. `RestSchema` is then built based on that `RestStructure`.
`RestDataCompanion` also automatically materializes a `GenCodec` instance.

```scala
case class User(id: String, @whenAbsent("anon") name: String, birthYear: Int)
object User extends RestDataCompanion[User] // gives GenCodec + RestStructure + RestSchema
```

[Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject) generated for
`User` class will look like this:

```json
{
  "type": "object",
  "properties": {
    "id": {
      "type": "string"
    },
    "name": {
      "type": "string",
      "default": "anon"
    },
    "birthYear": {
      "type": "integer",
      "format": "int32"
    }
  },
  "required": [
    "id",
    "birthYear"
  ]
}
```

It's also possible to macro materialize schema without using `RestDataCompanion`:

```scala
object User {
  implicit lazy val schema: RestSchema[User] = RestStructure.materialize[User].standaloneSchema
}
```

Schema derived for an ADT from macro materialized `RestStructure` will describe the JSON format used by
`GenCodec` macro materialized for that type. It will take into account all the annotations, e.g.
`@flatten`, `@name`, `@transparent`, `@whenAbsent` etc.

#### Registered schemas

By default, schemas macro materialized for case classes and sealed hierarchies will be _named_.
This means they will be registered under their name in
[Components Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#componentsObject).

By default, the name that will be used will be the simple (unqualified) name of the data type, e.g. "User".
This can be changed with [`@name`](http://avsystem.github.io/scala-commons/api/com/avsystem/commons/serialization/name.html) annotation.

When referring to registered schema (e.g. in
[Media Type Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject)),
a [Reference Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#referenceObject)
will be inserted, e.g. `{"$ref": "#/components/schemas/User"}`. This is good for schema reuse but may lead to name
conflicts if you have multiple data types with the same name but in different packages. Unfortunately, such conflicts
cannot be detected in compile time and will only be reported in runtime, when trying to generate OpenAPI document.

#### Adjusting macro materialized schemas

It's possible to adjust schemas macro materialized for ADTs using annotations. For example, you can use
`@description` annotation on data types and case class fields to inject description into materialized schemas, e.g.

```scala
import io.udash.rest._
import io.udash.rest.openapi._

@description("data type for users")
case class User(id: UserId, @description("user name") name: User, birthYear: Int)
object User extends RestDataCompanion[User]
```

You can also use more general `@adjustSchema` annotation which lets you define
completely arbitrary schema transformations.
See [Adjusting generated OpenAPI documents with annotations](#adjusting-generated-openapi-documents-with-annotations)
for more details on this mechanism.

#### Manually defined schemas

You may also define `RestSchema` completely by hand. This is usually done for primitive types or types with custom
serialization, different from macro materialized `GenCodec`. You can also insert references to externally defined
schemas.

```scala
class CustomStringType { ... }
object CustomStringType {
  implicit val restSchema: RestSchema[CustomType] =
    RestSchema.plain(Schema(`type` = DataType.String, description = "custom string type"))
}
```

When one `RestSchema` needs to refer to some other `RestSchema`, it must be resolved using `SchemaResolver`, e.g.

```scala
class CustomListType[T] { ... }
object CustomListType {
  implicit def restSchema[T: RestSchema]: RestSchema[CustomListType[T]] =
    RestSchema.create(resolver => Schema.arrayOf(resolver.resolve(RestSchema[T])))
}
```

This ensures that when the other schema is _named_ then the resolver will return a reference to registered schema
rather than inlined schema.

### `RestMediaTypes` typeclass

`RestMediaType` is an auxiliary typeclass which serves as a basis for `RestResponses` and `RestRequestBody` typeclasses.
It captures all the possible media types which may be used in a request or response body for given Scala type.
Media types are represented using OpenAPI 
[Media Type Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject).

By default, `RestMediaTypes` instance is derived from `RestSchema` instance and `application/json` is assumed as 
the only available media type.

You **should** define `RestMediaTypes` manually for every type which has custom serialization to `HttpBody` defined
(`AsRaw/AsReal[HttpBody, T]`). In general, you may want to define it manually every time you want to describe media
types other than `application/json` for your Scala type.

### `RestResponses` typeclass

`RestResponses` is an auxiliary typeclass which is needed for result type of every HTTP REST method
in your REST API trait. For example, if your method returns `Future[User]` then you need an instance
of `RestResponses[User]` (this transformation is modeled by yet another intermediate typeclass, `RestResultType`).
This typeclass governs generation of
[Responses Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responsesObject)

By default, if no specific `RestResponses` instance is provided, it is created based on `RestMediaTypes`.
The resulting [Responses](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responsesObject)
will contain exactly one
[Response](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responseObject)
for HTTP status code `200 OK` with 
[Media Types](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject)
inferred from `RestMediaTypes` instance. Also note that `RestMediaTypes` itself is by default derived from 
`RestSchema`

You **should** define `RestResponses` manually for every type which has custom serialization
to `RestResponse` defined (`AsRaw/AsReal[RestResponse, T]`). In general, you may want to define 
it manually every time you want to describe responses for status codes other than `200 OK`. 

Also remember that `Responses` object can be adjusted locally, for each method, using annotations - 
see [Adjusting operations](#adjusting-operations).

### `RestRequestBody` typeclass

`RestRequestBody` typeclass is an auxiliary typeclass analogous to `RestResponses`. It's necessary
for the `@Body` parameter of every `@CustomBody` method and governs generation of
[Request Body Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#requestBodyObject).
By default, if not defined explicitly, it is derived from `RestMediaTypes` instance - which by itself is derived
from `RestSchema` by default.

### Adjusting generated OpenAPI documents with annotations

The way OpenAPI documents are generated for your REST API can be influenced with annotations
applied on REST methods, parameters and data types. The most common example is the `@description` annotation
which may be applied on data types, case class fields, REST methods and parameters.
It causes the description to be injected into appropriate
[Schema](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject),
[Parameter](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#parameterObject) or
[Operation](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject)
objects.

However, `@description` is just an example of more general mechanism - schemas, parameters and operations
can be modified arbitrarily.

Also, remember that all annotations are processed with respect to the same
[annotation processing](https://github.com/AVSystem/scala-commons/blob/master/docs/Annotations.md) rules. It is recommended to familiarize oneself with these rules in
order to use them more effectively and with less boilerplate.

#### Adjusting schemas

In order to adjust schemas, one can define arbitrary annotations that extend `SchemaAdjuster`.
There is also a default implementation of `SchemaAdjuster`, the `@adjustSchema` annotation which
takes a lambda parameter which defines the schema transformation.

Annotations extending `SchemaAdjuster` can arbitrarily transform a
[Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject)
and can be applied on:

* data types with macro-generated `RestSchema`
* case class fields of data types with macro generated `RestSchema`
* `@Body` parameters of REST methods

Schema adjusters do **NOT** work on path/header/query/cookie parameters and REST methods
themselves. Instead use [parameter adjusters](#adjusting-parameters) and
[operation adjusters](#adjusting-operations) which can also modify schemas
used by `Parameter` and `Operation` objects.

Also, be aware that a `SchemaAdjuster` may be passed a schema reference instead of actual schema object.
This reference is then wrapped into a
[Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject)
object defined as `{"allOf": [{"$ref": <the-original-reference>}]}`.
Therefore, a schema adjuster may extend the referenced schema using
[Composition and Inheritance](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaComposition)
but it should not rely on the ability to inspect the referenced schema.

#### Adjusting parameters

Similar to `SchemaAdjuster` there is a `ParameterAdjuster` annotation trait. Its default implementation
is `@adjustParameter` which takes transformation lambda as its parameter.
Schema adjusters can arbitrarily transform
[Parameter Objects](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#parameterObject)
which are generated for path, header and query parameters of REST methods.

#### Adjusting operations

For adjusting [Operation Objects](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject)
there is `OperationAdjuster` annotation trait with default implementation `@adjustOperation`.
Operation adjuster can be applied on REST HTTP methods in order to transform Operation Objects
generated for them. This in particular means that operation adjusters can modify request body and responses
of an operation. When operation adjuster is applied on a [prefix method](#prefix-methods), it will apply to all
operations associated with result of this prefix method.

#### Adjusting path items

For adjusting [Path Item Objects](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathItemObject)
there is `PathItemAdjuster` annotation trait with default implementation `@adjustPathItem`.
Path item adjuster can be applied on REST HTTP methods in order to transform Path Item objects generated for them.
Because multiple REST HTTP methods may have the same path, adjusters are collected from all methods and ultimately all
are applied on the associated Path Item Object. When path item adjuster is applied on a [prefix method](#prefix-methods),
it will apply to all Path Item Objects associated with result of this prefix method.

### Limitations

* Current representation of OpenAPI document does not support
[specification extensions](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions).
