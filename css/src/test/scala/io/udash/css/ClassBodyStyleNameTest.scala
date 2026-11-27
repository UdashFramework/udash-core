package io.udash.css

import io.udash.testing.UdashSharedTest

class ClassBodyStyleNameTest extends UdashSharedTest {
  "Styles created as stylesheet body statements" should {
    "be named after the enclosing stylesheet" in {
      ClassBodyStylesheetExample.statementStyles.map(_.className) shouldBe Seq(
        "io-udash-css-ClassBodyStylesheetExample",
        "io-udash-css-ClassBodyStylesheetExample",
      )
    }
  }
}
