package io.udash.rest.raw

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.SomeServerApiImpl
import io.udash.rest.openapi.Info
import monix.execution.Scheduler
import monix.reactive.Observable
import org.scalactic.source.Position
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

class ServerImplApiTest extends AnyFunSuite with ScalaFutures {
  implicit def scheduler: Scheduler = Scheduler.global

  private val apiImpl = new SomeServerApiImpl
  private val serverHandle = SomeServerApiImpl.asHandleRequest(apiImpl)
  private val serverHandleStreaming = SomeServerApiImpl.asHandleRequestWithStreaming(apiImpl)

  def assertRawExchange(request: RestRequest, response: RestResponse)(implicit pos: Position): Unit = {
    val future = serverHandle(request).runToFuture
    assert(future.futureValue == response)
  }

  def assertStreamingExchange(
    request: RestRequest,
    expectedCode: Int,
    expectedContentType: Option[String] = None,
    verifyBody: Observable[_] => Boolean = _ => true
  )(implicit pos: Position): Unit = {
    val future = serverHandleStreaming(request).runToFuture
    val response = future.futureValue

    assert(response.isInstanceOf[StreamedRestResponse], "Response should be a StreamedRestResponse")

    val streamedResp = response.asInstanceOf[StreamedRestResponse]
    assert(streamedResp.code == expectedCode, s"Expected status code $expectedCode but got ${streamedResp.code}")

    expectedContentType.foreach { contentType =>
      val actualContentType = streamedResp.body match {
        case nonEmpty: StreamedBody.NonEmpty => nonEmpty.contentType
        case _ => ""
      }
      assert(actualContentType.startsWith(contentType),
        s"Expected content type starting with $contentType but got $actualContentType")
    }

    streamedResp.body match {
      case StreamedBody.Empty =>
        assert(expectedContentType.isEmpty, "Expected empty body but content type was specified")

      case jsonList: StreamedBody.JsonList =>
        assert(verifyBody(jsonList.elements), "JSON body verification failed")

      case binary: StreamedBody.RawBinary =>
        assert(verifyBody(binary.content), "Binary body verification failed")

      case single: StreamedBody.Single =>
        // For single body, we don't need to verify the observable as it's just a wrapper
        assert(true)
    }
  }

  test("simple GET call") {
    val params = RestParameters(
      path = PlainValue.decodePath("/thingy"),
      query = Mapping.create("param" -> PlainValue("42"))
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("\"41\"")))
    assertRawExchange(request, response)
  }

  test("subapi POST call") {
    val params = RestParameters(
      path = PlainValue.decodePath("/subapi/yeet"),
    )
    val body = HttpBody.createJsonBody(Mapping.create("data" -> JsonValue(JsonStringOutput.write("foo"))))
    val request = RestRequest(HttpMethod.POST, params, body)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("\"yeet foo\"")))
    assertRawExchange(request, response)
  }

  test("openapi") {
    val openapi = SomeServerApiImpl.openapiMetadata.openapi(Info("Test API", "0.1"))
    val json = JsonStringOutput.writePretty(openapi)

    assert(json ==
      """{
        |  "openapi": "3.0.2",
        |  "info": {
        |    "title": "Test API",
        |    "version": "0.1"
        |  },
        |  "paths": {
        |    "/streamBinary": {
        |      "get": {
        |        "operationId": "streamBinary",
        |        "parameters": [
        |          {
        |            "name": "chunkSize",
        |            "in": "query",
        |            "required": true,
        |            "explode": false,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          }
        |        ],
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/octet-stream": {
        |                "schema": {
        |                  "type": "string",
        |                  "format": "binary"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/streamEcho": {
        |      "post": {
        |        "operationId": "streamEcho",
        |        "requestBody": {
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "values": {
        |                    "type": "array",
        |                    "items": {
        |                      "type": "integer",
        |                      "format": "int32"
        |                    }
        |                  }
        |                },
        |                "required": [
        |                  "values"
        |                ]
        |              }
        |            }
        |          },
        |          "required": true
        |        },
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/json": {
        |                "schema": {
        |                  "type": "array",
        |                  "items": {
        |                    "type": "integer",
        |                    "format": "int32"
        |                  }
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/streamEmpty": {
        |      "get": {
        |        "operationId": "streamEmpty",
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/octet-stream": {
        |                "schema": {
        |                  "type": "string",
        |                  "format": "binary"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/streamingNumbers": {
        |      "get": {
        |        "operationId": "streamingNumbers",
        |        "parameters": [
        |          {
        |            "name": "count",
        |            "in": "query",
        |            "required": true,
        |            "explode": false,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          }
        |        ],
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/json": {
        |                "schema": {
        |                  "type": "array",
        |                  "items": {
        |                    "type": "integer",
        |                    "format": "int32"
        |                  }
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/subapi/yeet": {
        |      "post": {
        |        "operationId": "subapi_yeet",
        |        "requestBody": {
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "data": {
        |                    "type": "string"
        |                  }
        |                },
        |                "required": [
        |                  "data"
        |                ]
        |              }
        |            }
        |          },
        |          "required": true
        |        },
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/json": {
        |                "schema": {
        |                  "type": "string"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/thingy": {
        |      "get": {
        |        "operationId": "thingy",
        |        "parameters": [
        |          {
        |            "name": "param",
        |            "in": "query",
        |            "required": true,
        |            "explode": false,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          }
        |        ],
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/json": {
        |                "schema": {
        |                  "type": "string"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    }
        |  },
        |  "components": {}
        |}""".stripMargin)
  }

  test("streaming GET with JSON response") {
    val params = RestParameters(
      path = PlainValue.decodePath("/streamingNumbers"),
      query = Mapping.create("count" -> PlainValue("5"))
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)

    assertStreamingExchange(
      request = request,
      expectedCode = 200,
      expectedContentType = Some(HttpBody.JsonType),
      verifyBody = obs => {
        val valuesFuture = obs.asInstanceOf[Observable[JsonValue]]
          .map(json => json.value.trim.toInt)
          .toListL.runToFuture
        valuesFuture.futureValue == List(1, 2, 3, 4, 5)
      }
    )
  }

  test("streaming POST with JSON body and streamed response") {
    val params = RestParameters(
      path = PlainValue.decodePath("/streamEcho"),
    )
    val body = HttpBody.createJsonBody(Mapping.create("values" -> JsonValue("[1,2,3,4,5]")))
    val request = RestRequest(HttpMethod.POST, params, body)

    assertStreamingExchange(
      request = request,
      expectedCode = 200,
      expectedContentType = Some(HttpBody.JsonType),
      verifyBody = obs => {
        val valuesFuture = obs.asInstanceOf[Observable[JsonValue]]
          .map(json => json.value.trim.toInt)
          .toListL.runToFuture
        valuesFuture.futureValue == List(1, 2, 3, 4, 5)
      }
    )
  }

  test("streaming binary data") {
    val params = RestParameters(
      path = PlainValue.decodePath("/streamBinary"),
      query = Mapping.create("chunkSize" -> PlainValue("3"))
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)

    assertStreamingExchange(
      request = request,
      expectedCode = 200,
      expectedContentType = Some(HttpBody.OctetStreamType),
      verifyBody = obs => {
        val chunksFuture = obs.asInstanceOf[Observable[Array[Byte]]]
          .map(bytes => new String(bytes))
          .toListL.runToFuture

        chunksFuture.futureValue.mkString("") == "HelloWorld" * 100
      }
    )
  }

  test("empty streaming response") {
    val params = RestParameters(
      path = PlainValue.decodePath("/streamEmpty")
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)

    assertStreamingExchange(
      request = request,
      expectedCode = 200
    )
  }

  test("streaming GET call to non-streaming endpoint") {
    val params = RestParameters(
      path = PlainValue.decodePath("/streamingNumbers"),
      query = Mapping.create("count" -> PlainValue("5"))
    )
    val request = RestRequest(HttpMethod.GET, params, HttpBody.Empty)
    val response = RestResponse(200, IMapping.empty, HttpBody.json(JsonValue("[1,2,3,4,5]")))
    assertRawExchange(request, response)
  }
}
