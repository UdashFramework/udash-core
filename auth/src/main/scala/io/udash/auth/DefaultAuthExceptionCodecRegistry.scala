package io.udash.auth

import com.avsystem.commons.serialization.GenCodec
import io.udash.rpc.serialization.DefaultExceptionCodecRegistry

class DefaultAuthExceptionCodecRegistry extends DefaultExceptionCodecRegistry {
  register(GenCodec.create(_ => new UnauthenticatedException(), exceptionWriter))
  register(GenCodec.create(_ => new UnauthorizedException(), exceptionWriter))
}
