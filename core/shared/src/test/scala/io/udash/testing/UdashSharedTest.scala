package io.udash.testing

import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext

trait UdashSharedTest extends WordSpec with Matchers with BeforeAndAfterAll
trait AsyncUdashSharedTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll
