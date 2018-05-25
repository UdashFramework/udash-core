package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

import scala.concurrent.duration.DurationLong

class InputTest extends AsyncUdashFrontendTest {
  "Input" should {
    "synchronise state with property changes (deprecated)" in {
      val p = Property[String]("ABC")
      val input = (TextInput(p, None): @silent).render

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

    "synchronise property with state changes (deprecated)" in {
      val p = Property[String]("ABC")
      val input = (TextInput(p, None): @silent).render

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

    "synchronise state with property changes" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, 0 millis)()
      val inputEl = input.render

      inputEl.value should be("ABC")

      p.set("CBA")
      p.set("123")
      p.set("321")
      p.set("CBA")
      inputEl.value should be("CBA")

      p.set("")
      inputEl.value should be("")

      p.set("123")
      inputEl.value should be("123")

      p.set(null)
      inputEl.value should be("")

      p.set("123")
      inputEl.value should be("123")

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }

    "synchronise property with state changes" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, 0 millis)()
      val inputEl = input.render

      inputEl.value = "ABCD"
      inputEl.onpaste(null)
      inputEl.value = "12345"
      inputEl.onpaste(null)
      inputEl.value = "5432"
      inputEl.onpaste(null)
      inputEl.onkeyup(null)
      inputEl.onchange(null)
      inputEl.oninput(null)
      inputEl.value = "ABCD"
      inputEl.onpaste(null)
      inputEl.onkeyup(null)
      inputEl.onchange(null)
      inputEl.oninput(null)

      p.get should be("ABCD")
      inputEl.value = "ABC"
      inputEl.onchange(null)
      p.get should be("ABC")
      inputEl.value = "AB"
      inputEl.oninput(null)
      p.get should be("AB")
      inputEl.value = "A"
      inputEl.onkeyup(null)
      p.get should be("A")
      inputEl.value = "123qweasd"
      inputEl.onchange(null)
      p.get should be("123qweasd")

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }

    "synchronise property with state changes with debouncing" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, 0 millis)()
      val inputEl = input.render

      inputEl.value = "ABCD"
      inputEl.onpaste(null)
      inputEl.value = "12345"
      inputEl.onpaste(null)
      inputEl.value = "5432"
      inputEl.onpaste(null)
      inputEl.onkeyup(null)
      inputEl.onchange(null)
      inputEl.oninput(null)
      inputEl.value = "ABCD"
      inputEl.onpaste(null)
      inputEl.onkeyup(null)
      inputEl.onchange(null)
      inputEl.oninput(null)

      retrying { p.get should be("ABCD") } flatMap { _ =>
        inputEl.value = "ABC"
        inputEl.onchange(null)
        retrying { p.get should be("ABC") }
      } flatMap { _ =>
        inputEl.value = "AB"
        inputEl.oninput(null)
        retrying { p.get should be("AB") }
      } flatMap { _ =>
        inputEl.value = "A"
        inputEl.onkeyup(null)
        retrying { p.get should be("A") }
      } flatMap { _ =>
        inputEl.value = "123qweasd"
        inputEl.onchange(null)
        retrying { p.get should be("123qweasd") }
      } flatMap { _ =>
        p.listenersCount() should be(1)
        input.kill()
        p.listenersCount() should be(0)
      }
    }
  }
}
