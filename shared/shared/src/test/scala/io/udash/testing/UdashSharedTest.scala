package io.udash.testing

import io.udash.rpc.Utils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

trait UdashSharedTest extends WordSpec with Matchers with BeforeAndAfterAll with Utils
