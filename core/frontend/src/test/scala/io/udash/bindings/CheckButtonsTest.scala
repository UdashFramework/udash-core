package io.udash.bindings

import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest

class CheckButtonsTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "CheckButtons" should {
    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("A", "C")

      val buttons = CheckButtons(p, options, s => div(s.map(t => t._1))).render

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

      val buttons = CheckButtons(p, options, s => div(s.map(t => t._1))).render

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
  }
}
