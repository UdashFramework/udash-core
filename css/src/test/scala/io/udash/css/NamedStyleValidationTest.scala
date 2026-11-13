package io.udash.css

import io.udash.testing.{CompilationErrorAssertions, UdashSharedTest}

class NamedStyleValidationTest extends UdashSharedTest with CompilationErrorAssertions {
  "namedStyle" should {
    "reject a literal class name containing whitespace" in {
      typeErrorFor(
        """object S extends io.udash.css.CssBase { import dsl._; namedStyle("fa fa-adjust", display.block) }"""
      ) should include("""Invalid CSS class name "fa fa-adjust": it must be non-empty and must not contain whitespace""")
    }

    "reject an empty literal class name" in {
      typeErrorFor(
        """object S extends io.udash.css.CssBase { import dsl._; namedStyle("", display.block) }"""
      ) should include("""Invalid CSS class name "": it must be non-empty and must not contain whitespace""")
    }
  }

  "namedKeyframes" should {
    "reject a literal name containing whitespace" in {
      typeErrorFor(
        """object S extends io.udash.css.CssBase { import dsl._; namedKeyframes("fade in", 0d -> keyframe(opacity(0))) }"""
      ) should include("""Invalid CSS class name "fade in": it must be non-empty and must not contain whitespace""")
    }
  }
}
