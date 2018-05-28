package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.testing.UdashFrontendTest

class RadioButtonsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "RadioButtons" should {
    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = (RadioButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      for (o <- options) {
        p.set(o)
        buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 0)
        buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 1)
        buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 2)
        buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 3)
        buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 4)
      }
    }

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("X")

      val buttons = (RadioButtons(p, options, s => div(s.map(t => t._1))): @silent).render

      buttons.childElementCount should be(5)

      for (i <- options.indices) {
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].click()
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
        p.get should be(options(i))
      }

      for (i <- options.indices.reverse) {
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].click()
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
        p.get should be(options(i))
      }
    }

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      p.get should be("B")
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      for (o <- options) {
        p.set(o)
        buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 0)
        buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 1)
        buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 2)
        buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 3)
        buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(options.indexOf(o) == 4)
      }
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("X")

      val buttons = RadioButtons(p, options.toProperty)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      p.get should be("A")

      for (i <- options.indices) {
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].click()
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
        p.get should be(options(i))
      }

      for (i <- options.indices.reverse) {
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].click()
        buttons.childNodes(i).asInstanceOf[org.scalajs.dom.html.Input].onchange(null)
        p.get should be(options(i))
      }
    }

    "synchronise visible options" in {
      val options = SeqProperty("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val buttons = RadioButtons(p, options)(RadioButtons.inputsOnlyDecorator).render

      buttons.childElementCount should be(5)
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      options.append("F")

      buttons.childElementCount should be(6)
      p.get should be("B")
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(4).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(5).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)

      options.replace(0, 3, "X")

      buttons.childElementCount should be(4)
      p.get should be("X")
      buttons.childNodes(0).asInstanceOf[org.scalajs.dom.html.Input].checked should be(true)
      buttons.childNodes(1).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(2).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
      buttons.childNodes(3).asInstanceOf[org.scalajs.dom.html.Input].checked should be(false)
    }
  }
}
