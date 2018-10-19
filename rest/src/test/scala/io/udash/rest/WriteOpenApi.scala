package io.udash
package rest

import java.io.FileWriter

import com.avsystem.commons.serialization.json.{JsonOptions, JsonStringOutput}
import io.udash.rest.openapi.{Info, Server}

object WriteOpenApi {
  def main(args: Array[String]): Unit = {
    val openapi = RestTestApi.openapiMetadata.openapi(
      Info("Test API", "0.1", description = "Some test REST API"),
      servers = List(Server("http://localhost"))
    )
    val fw = new FileWriter("/home/ghik/api.js")
    fw.write("apiSpec = ")
    fw.write(JsonStringOutput.write(openapi, JsonOptions.Pretty))
    fw.close()
  }
}
