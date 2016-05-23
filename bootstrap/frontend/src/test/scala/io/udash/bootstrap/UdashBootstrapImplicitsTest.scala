package io.udash.bootstrap

import io.udash.properties.SeqProperty
import io.udash.testing.UdashFrontendTest

class UdashBootstrapImplicitsTest extends UdashFrontendTest with UdashBootstrapImplicits {
  "UdashBootstrapImplicits" should {
    "let this compile" in {
      val radioOptions = SeqProperty[String](Seq("Radio 1", "Radio 2", "Radio 3"))
      val radioGroup = FormInput.radioGroup(
        radioOptions.transform((s: String) => FormInput.radio(s, "modal-title", "First radio"))
      )
    }
  }
}
