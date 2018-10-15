package io.udash.testing

import org.scalajs.dom
import org.scalajs.dom.Element

trait FrontendTestUtils {
  def emptyComponent(): Element = dom.document.createElement("div")
}

trait UdashFrontendTest extends UdashSharedTest with FrontendTestUtils

trait AsyncUdashFrontendTest extends AsyncUdashSharedTest with FrontendTestUtils