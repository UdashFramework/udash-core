package io.udash.rpc

trait RPCFrontend {
  type ServerConnector                                  = io.udash.rpc.internals.ServerConnector
  type AtmosphereServerConnector                        = io.udash.rpc.internals.AtmosphereServerConnector
  type ExposesClientRPC[ClientRPCType <: ClientRPC]     = io.udash.rpc.internals.ExposesClientRPC[ClientRPCType]
}
