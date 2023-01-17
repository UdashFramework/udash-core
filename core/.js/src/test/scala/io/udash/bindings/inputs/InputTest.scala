package io.udash.bindings.inputs

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest
import org.scalactic.source.Position
import org.scalajs.dom.{ClipboardEvent, Event, KeyboardEvent, html}
import org.scalatest.Assertion
import org.scalatest.exceptions.TestFailedException
import org.scalatest.time.{Millis, Span}
import scalatags.JsDom.all._

import scala.concurrent.duration.DurationLong
import scala.util.{Failure, Success}

class InputTest extends AsyncUdashFrontendTest {

  private implicit class InputElementTestOps(input: html.Input) {
    def changeValue(value: String): Unit = {
      input.value = value
      input.onchange(new Event("change"))

    }
  }

  "Input" should {
    "should ignore Attributes: `tpe`, `value`, `onkeyup`, `onchange`, `onpaste`, `oninput` from inputModifiers" in {
      val p = Property[String]("")

      val input = TextInput(p, 0 millis)(inputModifiers = Seq[Modifier](
        tpe :+= null,
        scalatags.JsDom.all.value :+= null,
        onkeyup :+= { (_: Event) => throw new TestFailedException(Option.empty, Option.empty, 0) },
        onchange :+= { (_: Event) => throw new TestFailedException(Option.empty, Option.empty, 0) },
        onpaste :+= { (_: Event) => throw new TestFailedException(Option.empty, Option.empty, 0) },
        oninput :+= { (_: Event) => throw new TestFailedException(Option.empty, Option.empty, 0) },
        attr("sth") := "sth2"
      ))

      val inputEl = input.render
      inputEl.value = "ABCD"
      inputEl.onchange(new Event("change"))
      p.get should be("ABCD")

      inputEl.value = "DCBA"
      inputEl.onkeyup(new KeyboardEvent("keyup"))
      p.get should be("DCBA")

      inputEl.value = "ABCD"
      inputEl.oninput(new Event("input"))
      p.get should be("ABCD")

      inputEl.value = "DCBA"
      inputEl.onpaste(new ClipboardEvent("paste"))
      p.get should be("DCBA")

      inputEl.getAttribute("sth") should be("sth2")
    }

    "update state on KeyUp, Change, Paste and Input events" in {
      val p = Property[String]("")
      val input = TextInput(p, 0 millis)()
      val inputEl = input.render

      inputEl.value = "ABCD"
      inputEl.onchange(new Event("change"))
      p.get should be("ABCD")

      inputEl.value = "DCBA"
      inputEl.onkeyup(new KeyboardEvent("keyup"))
      p.get should be("DCBA")

      inputEl.value = "ABCD"
      inputEl.oninput(new Event("input"))
      p.get should be("ABCD")

      inputEl.value = "DCBA"
      inputEl.onpaste(new ClipboardEvent("paste"))
      p.get should be("DCBA")
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

      inputEl.changeValue("ABCD")
      inputEl.changeValue("12345")
      inputEl.changeValue("54321")
      inputEl.changeValue("ABCD")

      p.get should be("ABCD")
      inputEl.changeValue("ABC")
      p.get should be("ABC")
      inputEl.changeValue("AB")
      p.get should be("AB")
      inputEl.changeValue("A")
      p.get should be("A")
      inputEl.changeValue("123qweasd")
      p.get should be("123qweasd")

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }

    "synchronise property with state changes with debouncing" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, 0 millis)()
      val inputEl = input.render

      inputEl.changeValue("ABCD")
      inputEl.changeValue("12345")
      inputEl.changeValue("54321")
      inputEl.changeValue("ABCD")


      retrying {
        p.get should be("ABCD")
      } flatMap { _ =>
        inputEl.changeValue("ABC")
        retrying {
          p.get should be("ABC")
        }
      } flatMap { _ =>
        inputEl.changeValue("AB")
        retrying {
          p.get should be("AB")
        }
      } flatMap { _ =>
        inputEl.changeValue("A")
        retrying {
          p.get should be("A")
        }
      } flatMap { _ =>
        inputEl.changeValue("123qweasd")
        retrying {
          p.get should be("123qweasd")
        }
      } flatMap { _ =>
        p.listenersCount() should be(1)
        input.kill()
        p.listenersCount() should be(0)
      }
    }

    "synchronise with two inputs bound to a single property" in {
      val p = Property[String]("ABC")
      val input = TextInput(p, 0 millis)()
      val input2 = TextInput(p, 0 millis)()

      val r = input.render
      val r2 = input2.render

      r.value should be("ABC")
      r2.value should be("ABC")

      p.set("test")
      r.value should be("test")
      r2.value should be("test")

      r.changeValue("qwe")
      p.get should be("qwe")
      r2.value should be("qwe")

      r2.changeValue("asd")
      p.get should be("asd")
      r.value should be("asd")

      p.listenersCount() should be(2)

      input2.kill()
      p.listenersCount() should be(1)

      r.changeValue("qaz")
      p.get should be("qaz")
      r2.value should be("asd")

      input.kill()
      p.listenersCount() should be(0)

      p.set("trewq")
      r.value should be("qaz")
      r2.value should be("asd")
    }
  }

  "run callback on state changes" in {
    val p = Property[String]("ABC")
    var result = ""
    val input = TextInput(p, 0 millis, result = _)()
    val inputEl = input.render

    inputEl.changeValue("ABCD")
    result should be("ABCD")

    inputEl.changeValue("ABC")
    result should be("ABC")

    inputEl.changeValue("AB")
    result should be("AB")

    inputEl.changeValue("A")
    result should be("A")

    inputEl.changeValue("123qweasd")
    result should be("123qweasd")

    p.listenersCount() should be(1)
    input.kill()
    p.listenersCount() should be(0)
  }

  "run callback on state changes with debouncing" in {
    val p = Property[String]("ABC")
    var result = ""
    val input = TextInput(p, 20 millis, result = _)()
    val inputEl = input.render

    inputEl.changeValue("ABCD")

    retrying {
      result should be("ABCD")
    } flatMap { _ =>
      inputEl.changeValue("ABC")
      retrying {
        result should be("ABC")
      }
    } flatMap { _ =>
      inputEl.changeValue("AB")
      retrying {
        result should be("AB")
      }
    } flatMap { _ =>
      inputEl.changeValue("A")
      retrying {
        result should be("A")
      }
    } flatMap { _ =>
      inputEl.changeValue("123qweasd")
      retrying {
        result should be("123qweasd")
      }
    }
  }

  "not run callback when value is the same with debouncing" in {
    val p = Property[String]("ABC")
    var callbackValues = Seq.empty[String]
    val input = TextInput(p, 1 seconds, newValue => callbackValues :+= newValue)()
    val inputEl = input.render

    callbackValues should be(empty)

    inputEl.changeValue("CBA")
    inputEl.changeValue("ABC")

    retrying(
      callbackValues.shouldNot(be(empty))
    )(PatienceConfig(scaled(Span(1000, Millis)), scaled(Span(500, Millis))), Position.here).transform {
      case Failure(_: RetryingTimeout | _: TestFailedException) => Success(succeed)
      case fail: Failure[Assertion] => fail
      case Success(_) => Failure(fail(s"Callback shouldn't be executed with this debounce setup but it was executed ${callbackValues.length}" +
        s" times with following values: ${callbackValues.mkString(",")}"
      ))
    }

  }
}
