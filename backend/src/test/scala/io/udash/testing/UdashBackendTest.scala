package io.udash.testing

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.Eventually

trait UdashBackendTest extends UdashSharedTest with MockFactory with Eventually