package io.udash.bootstrap.form

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.testing.UdashFrontendTest

import scalatags.JsDom.all._

class UdashInputGroupTest extends UdashFrontendTest {

  "UdashInputGroup component" should {
    "clean up property listeners" in {
      val size = Property[Option[BootstrapStyles.Size]](Some(BootstrapStyles.Size.Large))
      val group = UdashInputGroup(size)(
        UdashInputGroup.prependText("Test"),
        UdashInputGroup.input(TextInput(Property(""))().render)
      )
      val el = group.render
      el.childNodes.length should be(2)

      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className) should be(true)
      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className) should be(false)

      size.set(None)
      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className) should be(false)
      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className) should be(false)

      size.set(Some(BootstrapStyles.Size.Small))
      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Large).className) should be(false)
      el.classList.contains(BootstrapStyles.InputGroup.size(BootstrapStyles.Size.Small).className) should be(true)

      group.kill()
      size.listenersCount() should be(0)
    }
  }
}
