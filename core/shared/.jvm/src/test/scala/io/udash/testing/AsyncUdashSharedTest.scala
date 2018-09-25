package io.udash.testing

import org.scalactic.source.Position
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.{Assertion, Succeeded}

import scala.concurrent.{ExecutionContext, Future}

trait AsyncUdashSharedTest extends AsyncUdashSharedTestBase with Eventually {
  override implicit def executionContext: ExecutionContext = ExecutionContext.global

  override def retrying(code: => Any)(implicit patienceConfig: PatienceConfig, pos: Position): Future[Assertion] = {
    Future {
      eventually(Timeout(patienceConfig.timeout), Interval(patienceConfig.interval))(code)
      Succeeded
    }
  }
}
