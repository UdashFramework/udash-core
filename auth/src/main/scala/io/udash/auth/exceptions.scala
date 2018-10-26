package io.udash.auth

import com.avsystem.commons.serialization.HasGenCodec

case class UnauthenticatedException() extends RuntimeException(s"User has to be authenticated to access this content.")
object UnauthenticatedException extends HasGenCodec[UnauthenticatedException]

case class UnauthorizedException() extends RuntimeException(s"Provided user context does not have access to this content.")
object UnauthorizedException extends HasGenCodec[UnauthorizedException]
