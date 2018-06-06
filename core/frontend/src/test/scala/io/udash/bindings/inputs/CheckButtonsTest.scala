package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest
import org.scalactic.source.Position
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Input => JSInput}

class CheckButtonsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "CheckButtons" should {
    def checkSelected(select: Element, selected: Seq[Boolean])(implicit pos: Position): Unit = {
      selected.zipWithIndex.foreach {
        case (value, idx) => select.childNodes(idx).asInstanceOf[JSInput].checked should be(value)
      }
    }

    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = (CheckButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)
      checkSelected(buttons, List(true, false, true, false, false))

      p.set(Seq("A", "D", "E"))
      checkSelected(buttons, List(true, false, false, true, true))

      p.set(Seq())
      checkSelected(buttons, List(false, false, false, false, false))

      p.set(Seq("A", "B", "C", "D", "E"))
      checkSelected(buttons, List(true, true, true, true, true))
    }

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = (CheckButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)

      buttons.childNodes(0).asInstanceOf[JSInput].checked = false
      buttons.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(1)
      p.get should contain("C")

      buttons.childNodes(4).asInstanceOf[JSInput].checked = true
      buttons.childNodes(4).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = true
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = false
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(0).asInstanceOf[JSInput].checked = true
      buttons.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = true
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(4)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(3).asInstanceOf[JSInput].checked = true
      buttons.childNodes(3).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(5)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("D")
      p.get should contain("E")
    }

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = CheckButtons(p, options.toProperty)(CheckButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      checkSelected(buttons, List(true, false, true, false, false))

      p.set(Seq("A", "D", "E"))
      checkSelected(buttons, List(true, false, false, true, true))

      p.set(Seq())
      checkSelected(buttons, List(false, false, false, false, false))

      p.set(Seq("A", "B", "C", "D", "E"))
      checkSelected(buttons, List(true, true, true, true, true))
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = CheckButtons(p, options.toProperty)(CheckButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)

      buttons.childNodes(0).asInstanceOf[JSInput].checked = false
      buttons.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(1)
      p.get should contain("C")

      buttons.childNodes(4).asInstanceOf[JSInput].checked = true
      buttons.childNodes(4).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = true
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = false
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(0).asInstanceOf[JSInput].checked = true
      buttons.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[JSInput].checked = true
      buttons.childNodes(1).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(4)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(3).asInstanceOf[JSInput].checked = true
      buttons.childNodes(3).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(5)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("D")
      p.get should contain("E")
    }

    "synchronise visible options" in {
      val options = SeqProperty("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val boxes = CheckButtons(p, options)(CheckButtons.inputsOnlyDecorator)
      val boxElements = boxes.render

      boxElements.childElementCount should be(5)
      p.listenersCount() should be(5)

      for ((o, idx) <- options.get.zipWithIndex) {
        p.set(Seq(o))
        boxElements.childNodes(idx).asInstanceOf[JSInput].checked should be(true)
      }

      p.set(options.get)
      options.remove(2, 3)
      boxElements.childElementCount should be(2)
      p.listenersCount() should be(2)
      p.get should be(Seq("A", "B"))

      options.prepend("1", "2")
      boxElements.childElementCount should be(4)
      p.listenersCount() should be(4)
      p.get should be(Seq("A", "B"))

      options.clear()
      boxElements.childElementCount should be(0)
      p.listenersCount() should be(0)
      p.get should be(Seq.empty)

      options.append("x", "y", "z")
      boxElements.childElementCount should be(3)
      p.listenersCount() should be(3)
      p.get should be(Seq.empty)

      boxes.kill()
      p.listenersCount() should be(0)
    }

    "synchronise with two inputs bound to a single property" in {
      val p = SeqProperty[Int](2)
      val options: Seq[Int] = 0 until 5
      val input = CheckButtons(p, options.toProperty)(CheckButtons.inputsOnlyDecorator)
      val input2 = CheckButtons(p, options.toProperty)(CheckButtons.inputsOnlyDecorator)

      val r = input.render
      val r2 = input2.render

      checkSelected(r, List(false, false, true, false, false))
      checkSelected(r2, List(false, false, true, false, false))

      p.append(3)
      checkSelected(r, List(false, false, true, true, false))
      checkSelected(r2, List(false, false, true, true, false))

      r.childNodes(0).asInstanceOf[JSInput].checked = true
      r.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain(2)
      p.get should contain(3)
      p.get should contain(0)
      r2.childNodes(0).asInstanceOf[JSInput].checked should be(true)

      r2.childNodes(0).asInstanceOf[JSInput].checked = false
      r2.childNodes(0).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(2)
      p.get should contain(2)
      p.get should contain(3)
      r.childNodes(0).asInstanceOf[JSInput].checked should be(false)

      p.listenersCount() should be(10)

      input2.kill()
      p.listenersCount() should be(5)

      r.childNodes(4).asInstanceOf[JSInput].checked = true
      r.childNodes(4).asInstanceOf[JSInput].onchange(null)
      p.get.size should be(3)
      p.get should contain(2)
      p.get should contain(3)
      p.get should contain(4)
      r2.childNodes(4).asInstanceOf[JSInput].checked should be(false)

      input.kill()
      p.listenersCount() should be(0)

      p.set(Seq.empty)
      r.childNodes(2).asInstanceOf[JSInput].checked should be(true)
      r2.childNodes(2).asInstanceOf[JSInput].checked should be(true)
    }
  }
}
