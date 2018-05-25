package io.udash.bindings.inputs

import io.udash._
import io.udash.bindings.inputs
import io.udash.testing.UdashFrontendTest

class CheckboxTest extends UdashFrontendTest {
  "Checkbox" should {
    "synchronise state with property changes" in {
      val p = Property[Boolean](true)
      val checkbox = inputs.Checkbox(p).render

      checkbox.checked should be(true)

      p.set(false)
      checkbox.checked should be(false)

      p.set(true)
      checkbox.checked should be(true)

      p.set(false)
      checkbox.checked should be(false)
    }

    "synchronise property with state changes" in {
      val p = Property[Boolean](true)
      val checkbox = inputs.Checkbox(p).render

      checkbox.checked should be(true)

      checkbox.checked = false
      checkbox.onchange(null)
      p.get should be(false)

      checkbox.checked = true
      checkbox.onchange(null)
      p.get should be(true)

      checkbox.checked = false
      checkbox.onchange(null)
      p.get should be(false)
    }
  }
}
