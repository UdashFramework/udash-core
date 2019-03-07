package io.udash
package rest.openapi

import io.udash.rest.RestTestApi
import com.avsystem.commons.serialization.json.JsonStringOutput
import org.scalatest.FunSuite

class OpenApiGenerationTest extends FunSuite {
  test("openapi for RestTestApi") {
    val openapi = RestTestApi.openapiMetadata.openapi(
      Info("Test API", "0.1", description = "Some test REST API"),
      servers = List(Server("http://localhost"))
    )
    assert(JsonStringOutput.writePretty(openapi) ==
      """{
        |  "openapi": "3.0.1",
        |  "info": {
        |    "title": "Test API",
        |    "version": "0.1",
        |    "description": "Some test REST API"
        |  },
        |  "paths": {
        |    "/": {
        |      "put": {
        |        "operationId": "singleBodyPut",
        |        "requestBody": {
        |          "description": "Serious body",
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "description": "REST entity description",
        |                "allOf": [
        |                  {
        |                    "$ref": "#/components/schemas/RestEntity"
        |                  }
        |                ]
        |              }
        |            }
        |          },
        |          "required": true
        |        },
        |        "responses": {
        |          "200": {
        |            "description": "Serious response",
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
        |    "/complexParams": {
        |      "put": {
        |        "operationId": "put_complexParams",
        |        "requestBody": {
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "flatBaseEntity": {
        |                    "$ref": "#/components/schemas/FlatBaseEntity"
        |                  },
        |                  "baseEntity": {
        |                    "nullable": true,
        |                    "allOf": [
        |                      {
        |                        "$ref": "#/components/schemas/BaseEntity"
        |                      }
        |                    ],
        |                    "default": null
        |                  }
        |                },
        |                "required": [
        |                  "flatBaseEntity"
        |                ]
        |              }
        |            }
        |          },
        |          "required": true
        |        },
        |        "responses": {
        |          "204": {
        |            "description": "Success"
        |          }
        |        }
        |      },
        |      "post": {
        |        "operationId": "complexParams",
        |        "requestBody": {
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "baseEntity": {
        |                    "$ref": "#/components/schemas/BaseEntity"
        |                  },
        |                  "flatBaseEntity": {
        |                    "nullable": true,
        |                    "allOf": [
        |                      {
        |                        "$ref": "#/components/schemas/FlatBaseEntity"
        |                      }
        |                    ],
        |                    "default": null
        |                  }
        |                },
        |                "required": [
        |                  "baseEntity"
        |                ]
        |              }
        |            }
        |          },
        |          "required": true
        |        },
        |        "responses": {
        |          "204": {
        |            "description": "Success"
        |          }
        |        }
        |      }
        |    },
        |    "/customResponse": {
        |      "post": {
        |        "operationId": "customResponse",
        |        "parameters": [
        |          {
        |            "name": "value",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          }
        |        ],
        |        "responses": {
        |          "200": {
        |            "description": "Custom response",
        |            "headers": {
        |              "X-Value": {
        |                "schema": {
        |                  "type": "string"
        |                }
        |              }
        |            },
        |            "content": {
        |              "text/plain": {
        |                "schema": {
        |                  "type": "string"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/failingGet": {
        |      "get": {
        |        "operationId": "failingGet",
        |        "responses": {
        |          "204": {
        |            "description": "Success"
        |          }
        |        }
        |      }
        |    },
        |    "/formPost": {
        |      "post": {
        |        "operationId": "formPost",
        |        "parameters": [
        |          {
        |            "name": "q1",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          }
        |        ],
        |        "requestBody": {
        |          "content": {
        |            "application/x-www-form-urlencoded": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "p1": {
        |                    "type": "string"
        |                  },
        |                  "p2": {
        |                    "type": "integer",
        |                    "format": "int32",
        |                    "default": 42
        |                  }
        |                },
        |                "required": [
        |                  "p1"
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
        |    "/moreFailingGet": {
        |      "get": {
        |        "operationId": "moreFailingGet",
        |        "responses": {
        |          "204": {
        |            "description": "Success"
        |          }
        |        }
        |      }
        |    },
        |    "/multi/param/{p1}/p1/{p2}": {
        |      "description": "path with a followed by b",
        |      "get": {
        |        "description": "A really complex GET operation",
        |        "operationId": "complexGet",
        |        "parameters": [
        |          {
        |            "name": "p1",
        |            "in": "path",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "p2",
        |            "in": "path",
        |            "description": "Very serious path parameter",
        |            "required": true,
        |            "schema": {
        |              "type": "string",
        |              "title": "Stri"
        |            }
        |          },
        |          {
        |            "name": "X-H1",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "X-H2",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          },
        |          {
        |            "name": "q1",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "q=2",
        |            "in": "query",
        |            "schema": {
        |              "type": "string",
        |              "default": "q2def"
        |            }
        |          }
        |        ],
        |        "responses": {
        |          "200": {
        |            "description": "Success",
        |            "content": {
        |              "application/json": {
        |                "schema": {
        |                  "$ref": "#/components/schemas/RestEntity"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      },
        |      "post": {
        |        "operationId": "multiParamPost",
        |        "parameters": [
        |          {
        |            "name": "p1",
        |            "in": "path",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "p2",
        |            "in": "path",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          },
        |          {
        |            "name": "X-H1",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "X-H2",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          },
        |          {
        |            "name": "q1",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "q=2",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          }
        |        ],
        |        "requestBody": {
        |          "content": {
        |            "application/json": {
        |              "schema": {
        |                "type": "object",
        |                "properties": {
        |                  "b1": {
        |                    "type": "integer",
        |                    "format": "int32"
        |                  },
        |                  "b\"2": {
        |                    "type": "string",
        |                    "description": "weird body field"
        |                  }
        |                },
        |                "required": [
        |                  "b1",
        |                  "b\"2"
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
        |                  "$ref": "#/components/schemas/RestEntity"
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    },
        |    "/prefix/{p0}/subget/{p1}": {
        |      "summary": "summary for prefix paths",
        |      "get": {
        |        "operationId": "prefix_subget",
        |        "parameters": [
        |          {
        |            "name": "p0",
        |            "in": "path",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          },
        |          {
        |            "name": "X-H0",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            }
        |          },
        |          {
        |            "name": "q0",
        |            "in": "query",
        |            "required": true,
        |            "schema": {
        |              "type": "string"
        |            },
        |            "example": "q0example"
        |          },
        |          {
        |            "name": "p1",
        |            "in": "path",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "X-H1",
        |            "in": "header",
        |            "required": true,
        |            "schema": {
        |              "type": "integer",
        |              "format": "int32"
        |            }
        |          },
        |          {
        |            "name": "q1",
        |            "in": "query",
        |            "required": true,
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
        |    },
        |    "/trivialGet": {
        |      "get": {
        |        "operationId": "trivialGet",
        |        "responses": {
        |          "204": {
        |            "description": "Success"
        |          }
        |        }
        |      }
        |    }
        |  },
        |  "servers": [
        |    {
        |      "url": "http://localhost"
        |    }
        |  ],
        |  "components": {
        |    "schemas": {
        |      "BaseEntity": {
        |        "oneOf": [
        |          {
        |            "type": "object",
        |            "properties": {
        |              "RestEntity": {
        |                "$ref": "#/components/schemas/RestEntity"
        |              }
        |            },
        |            "required": [
        |              "RestEntity"
        |            ]
        |          },
        |          {
        |            "type": "object",
        |            "properties": {
        |              "RestOtherEntity": {
        |                "$ref": "#/components/schemas/RestOtherEntity"
        |              }
        |            },
        |            "required": [
        |              "RestOtherEntity"
        |            ]
        |          },
        |          {
        |            "type": "object",
        |            "properties": {
        |              "SingletonEntity": {
        |                "$ref": "#/components/schemas/SingletonEntity"
        |              }
        |            },
        |            "required": [
        |              "SingletonEntity"
        |            ]
        |          }
        |        ]
        |      },
        |      "FlatBaseEntity": {
        |        "description": "Flat sealed entity with some serious cases",
        |        "allOf": [
        |          {
        |            "type": "object",
        |            "properties": {
        |              "_case": {
        |                "type": "string",
        |                "enum": [
        |                  "RestEntity",
        |                  "RestOtherEntity",
        |                  "SingletonEntity"
        |                ],
        |                "default": "RestEntity"
        |              }
        |            }
        |          },
        |          {
        |            "oneOf": [
        |              {
        |                "$ref": "#/components/schemas/RestEntity"
        |              },
        |              {
        |                "$ref": "#/components/schemas/RestOtherEntity"
        |              },
        |              {
        |                "$ref": "#/components/schemas/SingletonEntity"
        |              }
        |            ],
        |            "discriminator": {
        |              "propertyName": "_case"
        |            }
        |          }
        |        ]
        |      },
        |      "RestEntity": {
        |        "type": "object",
        |        "description": "REST entity",
        |        "properties": {
        |          "id": {
        |            "type": "string",
        |            "description": "entity id"
        |          },
        |          "name": {
        |            "type": "string",
        |            "default": "anonymous"
        |          },
        |          "subentity": {
        |            "description": "recursive optional subentity",
        |            "nullable": true,
        |            "allOf": [
        |              {
        |                "$ref": "#/components/schemas/RestEntity"
        |              }
        |            ],
        |            "default": null
        |          }
        |        },
        |        "required": [
        |          "id"
        |        ]
        |      },
        |      "RestOtherEntity": {
        |        "type": "object",
        |        "properties": {
        |          "fuu": {
        |            "type": "boolean"
        |          },
        |          "kek": {
        |            "type": "array",
        |            "items": {
        |              "type": "string"
        |            }
        |          }
        |        },
        |        "required": [
        |          "fuu",
        |          "kek"
        |        ]
        |      },
        |      "SingletonEntity": {
        |        "type": "object"
        |      }
        |    }
        |  }
        |}""".stripMargin
    )
  }
}
