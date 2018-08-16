package io.udash.bootstrap.jumbotron

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest

import scalatags.JsDom.all._

class UdashJumbotronTest extends UdashFrontendTest {

  "UdashJumbotron component" should {
    "clean up property listeners" in {
      val fluid = Property[Boolean](false)
      val jumbo = UdashJumbotron(fluid)(
        h4("Content")
      )
      val el = jumbo.render
      el.childNodes.length should be(1)
      el.textContent should be("Content")

      el.classList.contains(BootstrapStyles.Jumbotron.fluid.className) should be(false)

      fluid.set(true)
      el.classList.contains(BootstrapStyles.Jumbotron.fluid.className) should be(true)

      jumbo.kill()
      fluid.listenersCount() should be(0)
    }
  }
}
