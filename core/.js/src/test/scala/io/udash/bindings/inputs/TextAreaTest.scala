package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

import scala.concurrent.duration.DurationInt

class TextAreaTest extends AsyncUdashFrontendTest {
  "TextArea" should {
    "synchronise state with property changes (deprecated)" in {
      val p = Property[String]("ABC")
      val input = (TextArea(p, None): @silent).render

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

    "synchronise property with state changes (deprecated)" in {
      val p = Property[String]("ABC")
      val input = (TextArea(p, None): @silent).render

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

    "synchronise state with property changes" in {
      val p = Property[String]("ABC")
      val input = TextArea(p, 0 millis)()
      val inputEl = input.render

      inputEl.value should be("ABC")

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
      val input = TextArea(p, 0 millis)()
      val inputEl = input.render

      inputEl.value = "ABCD"
      inputEl.onpaste(null)
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
      val input = TextArea(p)()
      val inputEl = input.render

      inputEl.value = "ABCD"
      inputEl.onpaste(null)
      retrying {
        p.get should be("ABCD")
      } flatMap { case _ =>
        inputEl.value = "ABC"
        inputEl.onchange(null)
        retrying { p.get should be("ABC") }
      } flatMap { case _ =>
        inputEl.value = "AB"
        inputEl.oninput(null)
        retrying { p.get should be("AB") }
      } flatMap { case _ =>
        inputEl.value = "A"
        inputEl.onkeyup(null)
        retrying { p.get should be("A") }
      } flatMap { case _ =>
        inputEl.value = "123qweasd"
        inputEl.onchange(null)
        retrying { p.get should be("123qweasd") }
      } flatMap { case _ =>
        p.listenersCount() should be(1)
        input.kill()
        p.listenersCount() should be(0)
      }
    }

    "synchronise with two inputs bound to a single property" in {
      val p = Property[String]("ABC")
      val input = TextArea(p, 0 millis)()
      val input2 = TextArea(p, 0 millis)()

      val r = input.render
      val r2 = input2.render

      r.value should be("ABC")
      r2.value should be("ABC")

      p.set("test")
      r.value should be("test")
      r2.value should be("test")

      r.value = "qwe"
      r.onchange(null)
      p.get should be("qwe")
      r2.value should be("qwe")

      r2.value = "asd"
      r2.onchange(null)
      p.get should be("asd")
      r.value should be("asd")

      p.listenersCount() should be(2)

      input2.kill()
      p.listenersCount() should be(1)

      r.value = "qaz"
      r.onchange(null)
      p.get should be("qaz")
      r2.value should be("asd")

      input.kill()
      p.listenersCount() should be(0)

      p.set("trewq")
      r.value should be("qaz")
      r2.value should be("asd")
    }
  }
}
