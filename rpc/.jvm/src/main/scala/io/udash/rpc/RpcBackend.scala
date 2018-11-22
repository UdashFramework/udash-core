package io.udash.rpc

trait RpcBackend {
  type FileDownloadServlet = io.udash.rpc.utils.FileDownloadServlet
  type FileUploadServlet = io.udash.rpc.utils.FileUploadServlet
}