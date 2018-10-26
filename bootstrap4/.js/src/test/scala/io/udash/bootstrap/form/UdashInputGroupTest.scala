package io.udash.bootstrap.form

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import scalatags.JsDom.all._

class UdashInputGroupTest extends UdashCoreFrontendTest {

  "UdashInputGroup component" should {
    "clean up property listeners" in {
      val size = Property[Option[BootstrapStyles.Size]](Some(BootstrapStyles.Size.Large))
      val group = UdashInputGroup(size)(
        UdashInputGroup.prependText("Test"),
        UdashInputGroup.input(TextInput(Property(""))().render)
      )
      val el = group.render
      el.childNodes.length should be(2)

      el.classList should contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className)
      el.classList shouldNot contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className)

      size.set(None)
      el.classList shouldNot contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className)
      el.classList shouldNot contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className)

      size.set(Some(BootstrapStyles.Size.Small))
      el.classList shouldNot contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className)
      el.classList should contain(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className)

      group.kill()
      size.listenersCount() should be(0)
    }
  }
}
