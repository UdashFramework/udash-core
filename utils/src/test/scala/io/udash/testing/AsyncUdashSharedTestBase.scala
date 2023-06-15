package io.udash.testing

import org.scalactic.source.Position
import org.scalatest.{Assertion, BeforeAndAfterAll}
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.{Millis, Span}

import scala.concurrent.Future
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

trait AsyncUdashSharedTestBase extends AsyncWordSpec with Matchers with BeforeAndAfterAll with PatienceConfiguration {
  case class RetryingTimeout() extends Exception

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(scaled(Span(5000, Millis)), scaled(Span(100, Millis)))

  def retrying(code: => Any)(implicit patienceConfig: PatienceConfig, pos: Position): Future[Assertion]
}
