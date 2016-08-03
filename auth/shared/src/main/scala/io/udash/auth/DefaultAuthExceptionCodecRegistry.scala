package io.udash.auth

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.serialization.DefaultExceptionCodecRegistry

class DefaultAuthExceptionCodecRegistry extends DefaultExceptionCodecRegistry {
  register(GenCodec.create((input) => new UnauthenticatedException(), exceptionWriter))
  register(GenCodec.create((input) => new UnauthorizedException(), exceptionWriter))
}
