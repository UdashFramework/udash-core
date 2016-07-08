package io.udash.rpc

import scala.language.higherKinds

class JVMSerializationIntegrationTest extends SerializationIntegrationTestBase {
  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultServerUdashRPCFramework, DefaultServerUdashRPCFramework)
  "DefaultUdashRPCFramework -> UPickleUdashRPCFramework default serialization" should tests(DefaultServerUdashRPCFramework, ServerUPickleUdashRPCFramework)
  "UPickleUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(ServerUPickleUdashRPCFramework, DefaultServerUdashRPCFramework)
}