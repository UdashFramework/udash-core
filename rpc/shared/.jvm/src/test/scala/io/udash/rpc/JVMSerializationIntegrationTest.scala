package io.udash.rpc

class JVMSerializationIntegrationTest extends SerializationIntegrationTestBase {
  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultServerUdashRPCFramework, DefaultServerUdashRPCFramework)
  "DefaultUdashRPCFramework -> UPickleUdashRPCFramework default serialization" should tests(DefaultServerUdashRPCFramework, ServerUPickleUdashRPCFramework)
  "UPickleUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(ServerUPickleUdashRPCFramework, DefaultServerUdashRPCFramework)
}