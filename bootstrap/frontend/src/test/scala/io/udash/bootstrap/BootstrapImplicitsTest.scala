package io.udash.bootstrap

import io.udash._
import io.udash.testing.UdashFrontendTest
import org.scalajs.dom

import scala.language.postfixOps

class BootstrapImplicitsTest extends UdashFrontendTest with BootstrapImplicits {
  "PropertyOps" should {
    "allow reactive style changes" in {
      val p = Property(false)
      val textArea = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe false
      p.set(true)
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe true
      p.set(false)
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe false
    }
  }

  "StyleOps" should {
    "apply style conditionally" in {
      val textArea = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(false)).render
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe false
      val textArea2 = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(true)).render
      textArea2.hasStyles(BootstrapStyles.Form.formControl) shouldBe true
    }

    "apply style conditionally from property" in {
      val p = Property(false)
      val textArea = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe false
      p.set(true)
      val textArea2 = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea2.hasStyles(BootstrapStyles.Form.formControl) shouldBe true
    }

    "apply style conditionally from property with `true` on init" in {
      val p = Property(true)
      val textArea = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea.hasStyles(BootstrapStyles.Form.formControl) shouldBe true
      p.set(false)
      val textArea2 = TextArea.debounced(Property(""), BootstrapStyles.Form.formControl.styleIf(p)).render
      textArea2.hasStyles(BootstrapStyles.Form.formControl) shouldBe false
    }
  }

  implicit class ElemOps(elem: dom.Element) {
    def hasStyles(styles: BootstrapStyles.BootstrapClass*): Boolean =
      styles
        .map(_.cls)
        .forall(elem.classList.contains)
  }
}