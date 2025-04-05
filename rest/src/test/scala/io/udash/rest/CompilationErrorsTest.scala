package io.udash
package rest

import io.udash.testing.CompilationErrorAssertions

import scala.concurrent.Future
import org.scalatest.funsuite.AnyFunSuite

class CompilationErrorsTest extends AnyFunSuite with CompilationErrorAssertions {
  private def norm(error: String): String =
    error.replaceFirst("^Macro at <macro>:\\d+:\\d+ failed: ", "")

  trait SubApi
  object SubApi extends DefaultRestApiCompanion[SubApi]

  trait MissingSerializerForParam {
    def meth(par: Any): Future[Unit]
  }

  // TODO streaming add streaming tests

  test("missing serializer for parameter") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[MissingSerializerForParam]"))
    assert(error ==
      """problem with parameter par of method meth:
        |Cannot deserialize Any from JsonValue, because:
        |No GenCodec found for Any""".stripMargin)
  }

  trait MissingSerializerForResult {
    def meth(par: String): Future[Any]
  }

  test("missing serializer for result") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[MissingSerializerForResult]"))
    assert(error ==
      """cannot translate between trait MissingSerializerForResult and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into a prefix method:
        |   scala.concurrent.Future[Any] is not a valid server REST API trait, does its companion extend DefaultRestApiCompanion, DefaultRestServerApiCompanion or other companion base?
        | * it cannot be translated into an HTTP method:
        |   scala.concurrent.Future[Any] is not a valid result type because:
        |   Cannot serialize Any into RestResponse, because:
        |   Cannot serialize Any into HttpBody, because:
        |   Cannot serialize Any into JsonValue, because:
        |   No GenCodec found for Any
        | * it cannot be translated into an HTTP stream method:
        |   scala.concurrent.Future[Any] is not a valid result type because:
        |   Cannot serialize Any into StreamedRestResponse, because:
        |   Cannot serialize Any into io.udash.rest.raw.StreamedBody, appropriate AsRaw instance not found""".stripMargin)
  }

  trait BadResultType {
    def meth(par: String): Unit
  }

  test("bad result type") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[BadResultType]"))
    assert(error ==
      """cannot translate between trait BadResultType and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into a prefix method:
        |   Unit is not a valid server REST API trait, does its companion extend DefaultRestApiCompanion, DefaultRestServerApiCompanion or other companion base?
        | * it cannot be translated into an HTTP method:
        |   Unit is not a valid result type of HTTP REST method - it must be a Future
        | * it cannot be translated into an HTTP stream method:
        |   Unit is not a valid result type of HTTP REST method - it must be a Future""".stripMargin)
  }

  trait UnexpectedPrefixBodyParam {
    def meth(@Body par: String): SubApi
  }

  test("unexpected body param in prefix") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[UnexpectedPrefixBodyParam]"))
    assert(error ==
      """cannot translate between trait UnexpectedPrefixBodyParam and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into a prefix method:
        |   prefix methods cannot take @Body parameters
        | * it cannot be translated into an HTTP method:
        |   CompilationErrorsTest.this.SubApi is not a valid result type of HTTP REST method - it must be a Future
        | * it cannot be translated into an HTTP stream method:
        |   CompilationErrorsTest.this.SubApi is not a valid result type of HTTP REST method - it must be a Future""".stripMargin)
  }

  trait UnexpectedGETBodyParam {
    @GET def meth(@Body par: String): Future[Unit]
  }

  test("unexpected body param in GET") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[UnexpectedGETBodyParam]"))
    assert(error ==
      """cannot translate between trait UnexpectedGETBodyParam and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into an HTTP GET method:
        |   GET methods cannot take @Body parameters
        | * it cannot be translated into an HTTP GET stream method:
        |   scala.concurrent.Future[Unit] is not a valid result type because:
        |   Cannot serialize Unit into StreamedRestResponse, because:
        |   Cannot serialize Unit into io.udash.rest.raw.StreamedBody, appropriate AsRaw instance not found""".stripMargin)
  }

  trait MissingBodyParam {
    @CustomBody def meth(): Future[Unit]
  }

  test("missing body param in custom body method") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[MissingBodyParam]"))
    assert(error ==
      """cannot translate between trait MissingBodyParam and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into an HTTP method with custom body:
        |   expected exactly one @Body parameter but none was found
        | * it cannot be translated into an HTTP stream method with custom body:
        |   scala.concurrent.Future[Unit] is not a valid result type because:
        |   Cannot serialize Unit into StreamedRestResponse, because:
        |   Cannot serialize Unit into io.udash.rest.raw.StreamedBody, appropriate AsRaw instance not found""".stripMargin)
  }

  trait MultipleBodyParams {
    @CustomBody def meth(@Body foo: String, @Body bar: String): Future[Unit]
  }

  test("multiple body params in custom body method") {
    val error = norm(typeErrorFor("object Api extends DefaultRestApiCompanion[MultipleBodyParams]"))
    assert(error ==
      """cannot translate between trait MultipleBodyParams and trait RawRest:
        |problem with method meth:
        | * it cannot be translated into an HTTP method with custom body:
        |   expected exactly one @Body parameter but more than one was found
        | * it cannot be translated into an HTTP stream method with custom body:
        |   scala.concurrent.Future[Unit] is not a valid result type because:
        |   Cannot serialize Unit into StreamedRestResponse, because:
        |   Cannot serialize Unit into io.udash.rest.raw.StreamedBody, appropriate AsRaw instance not found""".stripMargin)
  }
}
