package io.udash.rpc

trait RPCFrontend {
  type ServerConnector[RPCRequest]                     = io.udash.rpc.internals.ServerConnector[RPCRequest]
  type AtmosphereServerConnector[RPCRequest]           = io.udash.rpc.internals.AtmosphereServerConnector[RPCRequest]
  type DefaultAtmosphereServerConnector                = io.udash.rpc.internals.DefaultAtmosphereServerConnector
  type ExposesClientRPC[ClientRPCType]                 = io.udash.rpc.internals.ExposesClientRPC[ClientRPCType]
  type DefaultExposesClientRPC[ClientRPCType]          = io.udash.rpc.internals.DefaultExposesClientRPC[ClientRPCType]
}
