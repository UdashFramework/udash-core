package io.udash.rpc

trait RPCFrontend {
  type ServerConnector = io.udash.rpc.internals.ServerConnector
  type AtmosphereServerConnector = io.udash.rpc.internals.AtmosphereServerConnector
  type DefaultAtmosphereServerConnector = io.udash.rpc.internals.DefaultAtmosphereServerConnector
  type ExposesClientRPC[ClientRPCType] = io.udash.rpc.internals.ExposesClientRPC[ClientRPCType]
  type DefaultExposesClientRPC[ClientRPCType] = io.udash.rpc.internals.DefaultExposesClientRPC[ClientRPCType]
}
