package io.udash.auth

class UnauthenticatedException extends RuntimeException(s"User has to be authenticated to access this content.")

class UnauthorizedException extends RuntimeException(s"Provided user context does not have access to this content.")
