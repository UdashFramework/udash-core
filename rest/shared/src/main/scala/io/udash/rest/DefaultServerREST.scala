package io.udash.rest

import io.udash.rest.internal.{RESTConnector, UsesREST}

import scala.concurrent.ExecutionContext

/** Default REST usage mechanism using [[io.udash.rest.DefaultRESTFramework]]. */
class DefaultServerREST[ServerRPCType : DefaultRESTFramework.AsRealRPC : DefaultRESTFramework.RPCMetadata : DefaultRESTFramework.ValidREST]
                       (override protected val connector: RESTConnector)(implicit ec: ExecutionContext)
  extends UsesREST[ServerRPCType] with RESTConverters {

  override val framework = DefaultRESTFramework

  override val remoteRpcAsReal: DefaultRESTFramework.AsRealRPC[ServerRPCType] = implicitly[DefaultRESTFramework.AsRealRPC[ServerRPCType]]
  override val rpcMetadata: framework.RPCMetadata[ServerRPCType] = implicitly[framework.RPCMetadata[ServerRPCType]]
}

object DefaultServerREST {
  /** Creates [[io.udash.rest.DefaultServerREST]] with [[io.udash.rest.DefaultRESTConnector]] for provided REST interfaces. */
  def apply[ServerRPCType : DefaultRESTFramework.AsRealRPC : DefaultRESTFramework.RPCMetadata : DefaultRESTFramework.ValidREST]
           (protocol: Protocol, host: String, port: Int, pathPrefix: String = "")(implicit ec: ExecutionContext): ServerRPCType = {
    val serverConnector = new DefaultRESTConnector(protocol, host, port, pathPrefix)
    val serverRPC: DefaultServerREST[ServerRPCType] = new DefaultServerREST[ServerRPCType](serverConnector)
    serverRPC.remoteRpc
  }
}