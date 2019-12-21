package io.udash
package rest.openapi

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.RestTestApi

import scala.io.Source
import org.scalatest.funsuite.AnyFunSuite

class OpenApiGenerationTest extends AnyFunSuite {
  test("openapi for RestTestApi") {
    val openapi = RestTestApi.openapiMetadata.openapi(
      Info("Test API", "0.1", description = "Some test REST API"),
      servers = List(Server("http://localhost"))
    )
    val expected = Source.fromInputStream(getClass.getResourceAsStream("/RestTestApi.json")).getLines().mkString("\n")
    assert(JsonStringOutput.writePretty(openapi) == expected)
  }
}
