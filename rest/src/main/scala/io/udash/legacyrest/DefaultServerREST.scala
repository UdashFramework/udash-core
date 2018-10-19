package io.udash.legacyrest

import io.udash.legacyrest.internal.{RESTConnector, UsesREST}

import scala.concurrent.ExecutionContext

/** Default REST usage mechanism using [[io.udash.legacyrest.DefaultRESTFramework]]. */
class DefaultServerREST[ServerRPCType : DefaultRESTFramework.AsRealRPC : DefaultRESTFramework.RPCMetadata : DefaultRESTFramework.ValidREST]
                       (override protected val connector: RESTConnector)(implicit ec: ExecutionContext)
  extends UsesREST[ServerRPCType] with RESTConverters {

  override val framework = DefaultRESTFramework

  override val remoteRpcAsReal: DefaultRESTFramework.AsRealRPC[ServerRPCType] = implicitly[DefaultRESTFramework.AsRealRPC[ServerRPCType]]
  override val rpcMetadata: framework.RPCMetadata[ServerRPCType] = implicitly[framework.RPCMetadata[ServerRPCType]]
}

object DefaultServerREST {
  /** Creates [[io.udash.legacyrest.DefaultServerREST]] with [[io.udash.legacyrest.DefaultRESTConnector]] for provided REST interfaces. */
  def apply[ServerRPCType : DefaultRESTFramework.AsRealRPC : DefaultRESTFramework.RPCMetadata : DefaultRESTFramework.ValidREST]
           (protocol: Protocol, host: String, port: Int, pathPrefix: String = "")(implicit ec: ExecutionContext): ServerRPCType = {
    val serverConnector = new DefaultRESTConnector(protocol, host, port, pathPrefix)
    val serverRPC: DefaultServerREST[ServerRPCType] = new DefaultServerREST[ServerRPCType](serverConnector)
    serverRPC.remoteRpc
  }
}