package io.udash.rpc

trait RPCBackend {
  type DefaultAtmosphereFramework = io.udash.rpc.utils.DefaultAtmosphereFramework
  type FileUploadServlet = io.udash.rpc.utils.FileUploadServlet
}