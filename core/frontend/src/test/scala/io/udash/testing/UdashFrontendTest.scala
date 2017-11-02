package io.udash.testing

trait FrontendTestUtils {
  import scalatags.JsDom.all.div
  def emptyComponent() = div().render
}

trait UdashFrontendTest extends UdashSharedTest with FrontendTestUtils

trait AsyncUdashFrontendTest extends AsyncUdashSharedTest with FrontendTestUtils