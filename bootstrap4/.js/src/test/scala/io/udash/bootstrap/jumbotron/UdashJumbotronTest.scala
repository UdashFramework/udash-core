package io.udash.bootstrap.jumbotron

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import scalatags.JsDom.all._

class UdashJumbotronTest extends UdashCoreFrontendTest {

  "UdashJumbotron component" should {
    "clean up property listeners" in {
      val fluid = Property[Boolean](false)
      val jumbo = UdashJumbotron(fluid)(
        h4("Content")
      )
      val el = jumbo.render
      el.childNodes.length should be(1)
      el.textContent should be("Content")

      el.classList shouldNot contain(BootstrapStyles.Jumbotron.fluid.className)

      fluid.set(true)
      el.classList should contain(BootstrapStyles.Jumbotron.fluid.className)

      jumbo.kill()
      fluid.listenersCount() should be(0)
    }
  }
}
