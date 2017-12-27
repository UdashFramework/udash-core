package io.udash.bindings

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

class InputTest extends AsyncUdashFrontendTest {
  "Input" should {
    "synchronise state with property changes" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, None).render

      input.value should be("ABC")

      p.set("CBA")
      p.set("123")
      p.set("321")
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
      val input = TextInput(p, None).render

      input.value = "ABCD"
      input.onpaste(null)
      input.value = "12345"
      input.onpaste(null)
      input.value = "5432"
      input.onpaste(null)
      input.onkeyup(null)
      input.onchange(null)
      input.oninput(null)
      input.value = "ABCD"
      input.onpaste(null)
      input.onkeyup(null)
      input.onchange(null)
      input.oninput(null)

      p.get should be("ABCD")
      input.value = "ABC"
      input.onchange(null)
      p.get should be("ABC")
      input.value = "AB"
      input.oninput(null)
      p.get should be("AB")
      input.value = "A"
      input.onkeyup(null)
      p.get should be("A")
      input.value = "123qweasd"
      input.onchange(null)
      p.get should be("123qweasd")
    }

    "synchronise property with state changes with debouncing" in {
      val p = Property[String]("ABC")
      val input = TextInput.debounced(p).render

      input.value = "ABCD"
      input.onpaste(null)
      input.value = "12345"
      input.onpaste(null)
      input.value = "5432"
      input.onpaste(null)
      input.onkeyup(null)
      input.onchange(null)
      input.oninput(null)
      input.value = "ABCD"
      input.onpaste(null)
      input.onkeyup(null)
      input.onchange(null)
      input.oninput(null)

      retrying { p.get should be("ABCD") } flatMap { case _ =>
        input.value = "ABC"
        input.onchange(null)
        retrying { p.get should be("ABC") }
      } flatMap { case _ =>
        input.value = "AB"
        input.oninput(null)
        retrying { p.get should be("AB") }
      } flatMap { case _ =>
        input.value = "A"
        input.onkeyup(null)
        retrying { p.get should be("A") }
      } flatMap { case _ =>
        input.value = "123qweasd"
        input.onchange(null)
        retrying { p.get should be("123qweasd") }
      }
    }
  }
}
