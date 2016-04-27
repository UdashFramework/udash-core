package io.udash.testing

import io.udash.rpc.Utils
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.Eventually

trait UdashBackendTest extends UdashRPCSharedTest with MockFactory with Eventually