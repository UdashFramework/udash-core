package io.udash.testing

import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Span}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

trait AsyncUdashSharedTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with Eventually {
  override implicit val patienceConfig = PatienceConfig(scaled(Span(5000, Millis)), scaled(Span(100, Millis)))
}
