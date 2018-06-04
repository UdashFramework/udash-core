package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest

class CheckButtonsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "CheckButtons" should {
    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = (CheckButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      p.set(Seq("A", "D", "E"))
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)

      p.set(Seq())
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      p.set(Seq("A", "B", "C", "D", "E"))
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
    }

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = (CheckButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)

      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked = false
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(1)
      p.get should contain("C")

      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(3)
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = false
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(4)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
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
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      p.set(Seq("A", "D", "E"))
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)

      p.set(Seq())
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      p.set(Seq("A", "B", "C", "D", "E"))
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = CheckButtons(p, options.toProperty)(CheckButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)

      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked = false
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(1)
      p.get should contain("C")

      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(3)
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = false
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(2)
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
      p.get.size should be(4)
      p.get should contain("A")
      p.get should contain("B")
      p.get should contain("C")
      p.get should contain("E")

      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked = true
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
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
        boxElements.childNodes(idx).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
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
  }
}
