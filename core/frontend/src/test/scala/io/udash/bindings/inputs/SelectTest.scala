package io.udash.bindings.inputs

import com.github.ghik.silencer.silent
import io.udash._
import io.udash.wrappers.jquery._
import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest

class SelectTest extends UdashFrontendTest {
  "Select" should {
    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = (Select(p, options, Select.defaultLabel)(): @silent).render

      select.childElementCount should be(5)
      select.value should be("B")

      for (o <- options) {
        p.set(o)
        select.value should be(o)
      }
    }

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = (Select(p, options, Select.defaultLabel)(): @silent).render

      select.childElementCount should be(5)

      for (i <- options.indices) {
        val o = options(i)
        select.value = o
        select.onchange(null)
        p.get should be(o)
      }
    }

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options.toProperty)(Select.defaultLabel).render

      select.childElementCount should be(5)
      select.value should be("1")

      for ((o, idx) <- options.zipWithIndex) {
        p.set(o)
        select.value should be(idx.toString)
      }
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options.toProperty)(Select.defaultLabel).render

      select.childElementCount should be(5)

      for ((o, idx) <- options.zipWithIndex) {
        select.value = idx.toString
        jQ(select).change()
        p.get should be(o)
      }
    }

    "synchronise visible options" in {
      val options = SeqProperty("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)
      p.listenersCount() should be(5)

      for ((o, idx) <- options.get.zipWithIndex) {
        p.set(o)
        selectElement.value should be(idx.toString)
      }

      options.remove(2, 3)
      selectElement.childElementCount should be(2)
      p.listenersCount() should be(2)
      p.get should be("A")
      selectElement.value should be("0")

      options.prepend("1", "2")
      selectElement.childElementCount should be(4)
      p.listenersCount() should be(4)
      p.get should be("A")
      selectElement.value should be("2")

      options.clear()
      selectElement.childElementCount should be(0)
      p.listenersCount() should be(0)
      p.get should be("A")
      selectElement.value should be("")

      options.append("x", "y", "z")
      selectElement.childElementCount should be(3)
      p.listenersCount() should be(3)
      p.get should be("x")
      selectElement.value should be("0")

      select.kill()
      p.listenersCount() should be(0)
    }
  }

  "Select with multiple on" should {
    "synchronise state with property changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = (Select(p, options, Select.defaultLabel)(): @silent).render

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

    "synchronise property with state changes (deprecated)" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = (Select(p, options, Select.defaultLabel)(): @silent).render

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

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)
      selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)

      for (o <- options) {
        p.set(Seq(o))
        selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 0)
        selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 1)
        selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 2)
        selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 3)
        selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(options.indexOf(o) == 4)
      }

      p.set(Seq("A", "B", "C", "D", "E"))
      selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)
      selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(true)

      p.clear()
      selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
      selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected should be(false)
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)

      for (o <- options) {
        selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 0
        selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 1
        selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 2
        selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 3
        selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = options.indexOf(o) == 4
        selectElement.onchange(null)
        p.get should be(Seq(o))
      }

      selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      selectElement.onchange(null)
      p.get.size should be(2)
      p.get should contain("B")
      p.get should contain("D")

      selectElement.childNodes(0).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      selectElement.childNodes(1).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      selectElement.childNodes(2).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      selectElement.childNodes(3).asInstanceOf[org.scalajs.dom.html.Option].selected = false
      selectElement.childNodes(4).asInstanceOf[org.scalajs.dom.html.Option].selected = true
      selectElement.onchange(null)
      p.get.size should be(3)
      p.get should contain("A")
      p.get should contain("C")
      p.get should contain("E")
    }

    "synchronise visible options" in {
      val options = SeqProperty("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)
      p.listenersCount() should be(5)

      for ((o, idx) <- options.get.zipWithIndex) {
        p.set(Seq(o))
        selectElement.value should be(idx.toString)
      }

      options.remove(2, 3)
      selectElement.childElementCount should be(2)
      p.listenersCount() should be(2)
      p.get should be(Seq.empty)

      p.set(Seq("A", "B"))
      options.prepend("1", "2")
      selectElement.childElementCount should be(4)
      p.listenersCount() should be(4)
      p.get should be(Seq("A", "B"))

      options.clear()
      selectElement.childElementCount should be(0)
      p.listenersCount() should be(0)
      p.get should be(Seq.empty)

      options.append("x", "y", "z")
      selectElement.childElementCount should be(3)
      p.listenersCount() should be(3)
      p.get should be(Seq.empty)

      select.kill()
      p.listenersCount() should be(0)
    }
  }
}
