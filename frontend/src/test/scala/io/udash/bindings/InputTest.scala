package io.udash.bindings

import io.udash._
import io.udash.testing.UdashFrontendTest

class InputTest extends UdashFrontendTest {
  "Input" should {
    "synchronise state with property changes" in {
      val p = Property[String]("ABC")
      val input = TextInput(p).render

      input.value should be("ABC")

      p.set("CBA")
      input.value should be("CBA")

      p.set("")
      input.value should be("")

      p.set("123")
      input.value should be("123")

      p.set(null)
      input.value should be("")

      p.set("123")
      input.value should be("123")
    }

    "synchronise property with state changes" in {
      val p = Property[String]("ABC")
      val input = TextInput(p).render

      input.value = "ABCD"
      input.onchange(null)
      p.get should be("ABCD")

      input.value = "ABC"
      input.onchange(null)
      p.get should be("ABC")

      input.value = "AB"
      input.onchange(null)
      p.get should be("AB")

      input.value = "A"
      input.onchange(null)
      p.get should be("A")

      input.value = "123qweasd"
      input.onchange(null)
      p.get should be("123qweasd")
    }
  }
}
