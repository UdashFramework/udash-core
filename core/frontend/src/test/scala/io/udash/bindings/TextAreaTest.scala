package io.udash.bindings

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

class TextAreaTest extends AsyncUdashFrontendTest {
  "TextArea" should {
    "synchronise state with property changes" in {
      val p = Property[String]("ABC")
      val input = TextArea(p, None).render

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
      val input = TextArea(p, None).render

      input.value = "ABCD"
      input.onpaste(null)
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
      val input = TextArea.debounced(p).render

      input.value = "ABCD"
      input.onpaste(null)
      retrying {
        p.get should be("ABCD")
      } flatMap { case _ =>
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
      }flatMap { case _ =>
        input.value = "123qweasd"
        input.onchange(null)
        retrying { p.get should be("123qweasd") }
      }
    }
  }
}
