package io.udash.bindings

import io.udash._
import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest

class SelectTest extends UdashFrontendTest {
  "Select" should {
    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options).render

      select.childElementCount should be(5)
      select.value should be("B")

      for (o <- options) {
        p.set(o)
        select.value should be(o)
      }
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options).render

      select.childElementCount should be(5)

      for (i <- options.indices) {
        val o = options(i)
        select.value = o
        select.onchange(null)
        p.get should be(o)
      }
    }
  }
  "Select with multiple on" should {
    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options).render

      select.childElementCount should be(5)
      select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)

      for (o <- options) {
        p.set(Seq(o))
        select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 0)
        select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 1)
        select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 2)
        select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 3)
        select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 4)
      }

      p.set(Seq("A", "B", "C", "D", "E"))
      select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)

      p.clear()
      select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options).render

      select.childElementCount should be(5)

      for (o <- options) {
        select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 0
        select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 1
        select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 2
        select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 3
        select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 4
        select.onchange(null)
        p.get should be(Seq(o))
      }

      select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      select.onchange(null)
      p.get.size should be(2)
      p.get should contain("B")
      p.get should contain("D")

      select.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      select.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      select.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      select.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      select.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      select.onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")
    }
  }
}
