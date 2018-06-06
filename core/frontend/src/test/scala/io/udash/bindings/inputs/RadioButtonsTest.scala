package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.testing.UdashFrontendTest
import org.scalactic.source.Position
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Input => JSInput}

class RadioButtonsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "RadioButtons" should {
    def checkSelected(select: Element, selected: Seq[Boolean])(implicit pos: Position): Unit = {
      selected.zipWithIndex.foreach {
        case (value, idx) => select.childNodes(idx).asInstanceOf[JSInput].checked should be(value)
      }
    }

    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = (RadioButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)
      checkSelected(buttons, List(false, true, false, false, false))

      for ((opt, idx) <- options.zipWithIndex) {
        p.set(opt)
        checkSelected(buttons, List(false, false, false, false, false).updated(idx, true))
      }
    }

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("X")

      val buttons = (RadioButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)

      for (i <- options.indices) {
        buttons.childNodes(i).asInstanceOf[JSInput].click()
        buttons.childNodes(i).asInstanceOf[JSInput].onchange(null)
        p.get should be(options(i))
      }

      for (i <- options.indices.reverse) {
        buttons.childNodes(i).asInstanceOf[JSInput].click()
        buttons.childNodes(i).asInstanceOf[JSInput].onchange(null)
        p.get should be(options(i))
      }
    }

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      p.get should be("B")
      checkSelected(buttons, List(false, true, false, false, false))

      for ((opt, idx) <- options.zipWithIndex) {
        p.set(opt)
        checkSelected(buttons, List(false, false, false, false, false).updated(idx, true))
      }
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("X")

      val buttons = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      p.get should be("A")

      for (i <- options.indices) {
        buttons.childNodes(i).asInstanceOf[JSInput].click()
        buttons.childNodes(i).asInstanceOf[JSInput].onchange(null)
        p.get should be(options(i))
      }

      for (i <- options.indices.reverse) {
        buttons.childNodes(i).asInstanceOf[JSInput].click()
        buttons.childNodes(i).asInstanceOf[JSInput].onchange(null)
        p.get should be(options(i))
      }
    }

    "synchronise visible options" in {
      val options = SeqProperty("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = RadioButtons(p, options)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      checkSelected(buttons, List(false, true, false, false, false))

      options.append("F")

      buttons.childElementCount should be(6)
      p.get should be("B")
      checkSelected(buttons, List(false, true, false, false, false, false))

      options.replace(0, 3, "X")

      buttons.childElementCount should be(4)
      p.get should be("X")
      checkSelected(buttons, List(true, false, false, false))
    }

    "synchronise with two inputs bound to a single property" in {
      val p = Property[Int](2)
      val options: Seq[Int] = 0 until 5
      val input = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator)
      val input2 = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator)

      val r = input.render
      val r2 = input2.render

      checkSelected(r, List(false, false, true, false, false))
      checkSelected(r2, List(false, false, true, false, false))

      p.set(3)
      checkSelected(r, List(false, false, false, true, false))
      checkSelected(r2, List(false, false, false, true, false))

      r.childNodes(0).asInstanceOf[JSInput].checked = true
      r.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get should be(0)
      checkSelected(r, List(true, false, false, false, false))
      checkSelected(r2, List(true, false, false, false, false))

      r2.childNodes(1).asInstanceOf[JSInput].checked = true
      r2.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get should be(1)
      checkSelected(r, List(false, true, false, false, false))
      checkSelected(r2, List(false, true, false, false, false))

      p.listenersCount() should be(10)

      input2.kill()
      p.listenersCount() should be(5)

      r.childNodes(4).asInstanceOf[JSInput].checked = true
      r.childNodes(4).asInstanceOf[JSInput].onchange(null)
      p.get should be(4)
      checkSelected(r, List(false, false, false, false, true))
      checkSelected(r2, List(false, true, false, false, false))

      input.kill()
      p.listenersCount() should be(0)

      p.set(2)
      checkSelected(r, List(false, false, false, false, true))
      checkSelected(r2, List(false, true, false, false, false))
    }
  }
}
