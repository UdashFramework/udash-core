package io.udash
package rest

import com.avsystem.commons.rpc.InvalidRpcCall

class RestException(msg: String, cause: Throwable = null) extends InvalidRpcCall(msg, cause)

class InvalidRestApiException(msg: String) extends RestException(msg)
