package io.udash.bootstrap

import io.udash.testing.UdashFrontendTest
import io.udash._

import scala.language.postfixOps

class BootstrapImplicitsTest extends UdashFrontendTest with BootstrapImplicits {
  "PropertyOps" should {
    "allow reactive style changes" in {
      val p = Property(false)
      val textArea = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea.classList.contains(BootstrapStyles.Form.formControl.htmlClass) shouldBe false
      p.set(true)
      textArea.classList.contains(BootstrapStyles.Form.formControl.htmlClass) shouldBe true
      p.set(false)
      textArea.classList.contains(BootstrapStyles.Form.formControl.htmlClass) shouldBe false
    }
  }
}