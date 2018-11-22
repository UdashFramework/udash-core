package io.udash.testing

import io.udash.rpc.Utils
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Span}

trait AsyncUdashRpcBackendTest extends AsyncUdashSharedTest with Utils with Eventually {
  override implicit val patienceConfig = PatienceConfig(scaled(Span(5000, Millis)), scaled(Span(100, Millis)))
}

trait UdashRpcBackendTest extends UdashSharedTest with Utils with Eventually {
  override implicit val patienceConfig = PatienceConfig(scaled(Span(5000, Millis)), scaled(Span(100, Millis)))
}