package io.udash
package rest

import com.avsystem.commons._
import org.scalatest.FunSuite

class RestValidationTest extends FunSuite with DefaultRestImplicits {

  trait Api2 {
    def self: Api2
  }
  object Api2 {
    implicit val metadata: RestMetadata[Api2] = RestMetadata.materialize[Api2]
  }

  test("recursive API") {
    val failure = intercept[InvalidRestApiException](Api2.metadata.ensureValid())
    assert(failure.getMessage == "call chain self->self is recursive, recursively defined server APIs are forbidden")
  }

  trait Api1 {
    @GET("p") def g1: Future[String]
    @GET("p") def g2: Future[String]
    @GET("") def g3(@Path("p") arg: String): Future[String]

    @POST("p") def p1: Future[String]
  }

  test("simple ambiguous paths") {
    val failure = intercept[InvalidRestApiException] {
      RestMetadata.materialize[Api1].ensureValid()
    }
    assert(failure.getMessage ==
      """REST API has ambiguous paths:
        |GET /p may result from multiple calls:
        |  g1
        |  g2""".stripMargin
    )
  }

  trait PrefixApi1 {
    def prefix(@Header("X-Lol") lol: String): SuffixApi1
  }
  trait SuffixApi1 {
    def post(@Header("X-Lol") lol: String): Future[String]
  }
  object SuffixApi1 {
    implicit val metadata: RestMetadata[SuffixApi1] = RestMetadata.materialize
  }

  test("conflicting header params") {
    val failure = intercept[InvalidRestApiException] {
      RestMetadata.materialize[PrefixApi1].ensureValid()
    }
    assert(failure.getMessage ==
      "Header parameter X-Lol of post collides with header parameter of the same name in prefix prefix")
  }

  trait PrefixApi2 {
    def prefix(@Query lol: String): SuffixApi2
  }
  trait SuffixApi2 {
    def post(@Query lol: String): Future[String]
  }
  object SuffixApi2 {
    implicit val metadata: RestMetadata[SuffixApi2] = RestMetadata.materialize
  }

  test("conflicting query params") {
    val failure = intercept[InvalidRestApiException] {
      RestMetadata.materialize[PrefixApi2].ensureValid()
    }
    assert(failure.getMessage ==
      "Query parameter lol of post collides with query parameter of the same name in prefix prefix")
  }
}
