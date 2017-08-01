package io.udash.rest.server

import io.udash.rest.DefaultRESTFramework
import io.udash.rpc.serialization.URLEncoder

class DefaultExposesREST[ServerRPCType : DefaultRESTFramework.ValidServerREST : DefaultRESTFramework.RPCMetadata](localRest: ServerRPCType)(
  implicit protected val localRpcAsRaw: DefaultRESTFramework.AsRawRPC[ServerRPCType]
) extends ExposesREST[ServerRPCType](localRest) {
  override val framework = DefaultRESTFramework

  protected val rpcMetadata: DefaultRESTFramework.RPCMetadata[ServerRPCType] =
    implicitly[DefaultRESTFramework.RPCMetadata[ServerRPCType]]

  override def headerArgumentToRaw(raw: String, isStringArg: Boolean): framework.RawValue = rawArg(raw, isStringArg)
  override def queryArgumentToRaw(raw: String, isStringArg: Boolean): framework.RawValue = rawArg(raw, isStringArg)
  override def urlPartToRaw(raw: String, isStringArg: Boolean): framework.RawValue = rawArg(URLEncoder.decode(raw), isStringArg)

  private def rawArg(raw: String, isStringArg: Boolean): framework.RawValue =
    if (isStringArg) framework.stringToRaw(s""""$raw"""")
    else framework.stringToRaw(raw)
}
