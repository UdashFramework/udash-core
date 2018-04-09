package io.udash.rpc

class JVMSerializationIntegrationTest extends SerializationIntegrationTestBase {
  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(
    DefaultServerUdashRPCFramework, DefaultServerUdashRPCFramework
  )
  "CirceUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(
    CirceUdashRpcFramework, DefaultServerUdashRPCFramework
  )
  "DefaultUdashRPCFramework -> CirceUdashRPCFramework default serialization" should tests(
    DefaultServerUdashRPCFramework, CirceUdashRpcFramework
  )
}