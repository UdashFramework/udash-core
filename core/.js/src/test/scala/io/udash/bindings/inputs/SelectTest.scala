package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.seq.SeqProperty
import io.udash.testing.UdashFrontendTest
import org.scalactic.source.Position
import org.scalajs.dom.html.{Option => JSOption, Select => JSSelect}
import scalatags.JsDom.all.StringFrag

class SelectTest extends UdashFrontendTest {
  "Select" should {
    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = Property[String]("B")

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel).render

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

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel).render

      select.childElementCount should be(5)

      for ((o, idx) <- options.zipWithIndex) {
        select.value = idx.toString
        select.onchange(null)
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

    "synchronise with two inputs bound to a single property" in {
      val p = Property[Int](2)
      val options: Seq[Int] = 0 until 5
      val input = Select(p, options.toSeqProperty)(v => StringFrag(v.toString))
      val input2 = Select(p, options.toSeqProperty)(v => StringFrag(v.toString))

      val r = input.render
      val r2 = input2.render

      r.value should be("2")
      r2.value should be("2")

      p.set(3)
      r.value should be("3")
      r2.value should be("3")

      r.value = "0"
      r.onchange(null)
      p.get should be(0)
      r2.value should be("0")

      r2.value = "1"
      r2.onchange(null)
      p.get should be(1)
      r.value should be("1")

      p.listenersCount() should be(10)

      input2.kill()
      p.listenersCount() should be(5)

      r.value = "4"
      r.onchange(null)
      p.get should be(4)
      r2.value should be("1")

      input.kill()
      p.listenersCount() should be(0)

      p.set(2)
      r.value should be("4")
      r2.value should be("1")
    }

    "handle optional case" in {
      val options = Seq(Some("A"), None, Some("B"))
      val p = Property[Option[Option[String]]](Some(None))

      val select = Select.optional(p, options.toSeqProperty,StringFrag("empty"))(x => StringFrag(x.getOrElse(""))).render

      select.childElementCount should be(4) // empty value should be included
      select.value should be("1")

      def trimHtml(s:String):String = s.split("\n").map(_.trim).mkString

      val expectedHtml = """<select>
                           |  <option value="">empty</option>
                           |  <option value="0">A</option>
                           |  <option value="1"></option>
                           |  <option value="2">B</option>
                           |</select>
                           |""".stripMargin

      trimHtml(select.outerHTML) should be(trimHtml(expectedHtml))

      p.set(None)
      select.value should be("")

      for ((o, idx) <- options.zipWithIndex) {
        p.set(Some(o))
        select.value should be(idx.toString)
      }
    }

  }

  "Select with multiple on" should {
    def checkSelected(select: JSSelect, selected: Seq[Boolean])(implicit pos: Position): Unit = {
      selected.zipWithIndex.foreach {
        case (value, idx) => select.childNodes(idx).asInstanceOf[JSOption].selected should be(value)
      }
    }

    def setSelected(select: JSSelect, selected: Seq[Boolean]): Unit = {
      selected.zipWithIndex.foreach {
        case (value, idx) => select.childNodes(idx).asInstanceOf[JSOption].selected = value
      }
    }

    "synchronise state with property changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)
      checkSelected(selectElement, List(false, true, false, false, false))

      for ((opt, idx) <- options.zipWithIndex) {
        p.set(Seq(opt))
        checkSelected(selectElement, List.fill(5)(false).updated(idx, true))
      }

      p.set(Seq("A", "B", "C", "D", "E"))
      checkSelected(selectElement, List(true, true, true, true, true))

      p.clear()
      checkSelected(selectElement, List(false, false, false, false, false))
    }

    "synchronise property with state changes" in {
      val options = Seq("A", "B", "C", "D", "E")
      val p = SeqProperty[String]("B")

      val select = Select(p, options.toSeqProperty)(Select.defaultLabel)
      val selectElement = select.render

      selectElement.childElementCount should be(5)

      for ((opt, idx) <- options.zipWithIndex) {
        setSelected(selectElement, List.fill(5)(false).updated(idx, true))
        selectElement.onchange(null)
        p.get should be(Seq(opt))
      }

      setSelected(selectElement, List(false, true, false, true, false))
      selectElement.onchange(null)
      p.get.size should be(2)
      p.get should contain("B")
      p.get should contain("D")

      setSelected(selectElement, List(true, false, true, false, true))
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

    "synchronise with two inputs bound to a single property" in {
      val p = SeqProperty[Int](2)
      val options: Seq[Int] = 0 until 5
      val input = Select(p, options.toSeqProperty)(v => StringFrag(v.toString))
      val input2 = Select(p, options.toSeqProperty)(v => StringFrag(v.toString))

      val r = input.render
      val r2 = input2.render

      checkSelected(r, List(false, false, true, false, false))
      checkSelected(r2, List(false, false, true, false, false))

      p.append(3)
      checkSelected(r, List(false, false, true, true, false))
      checkSelected(r2, List(false, false, true, true, false))

      r.childNodes(0).asInstanceOf[JSOption].selected = true
      r.onchange(null)
      p.get.size should be(3)
      p.get should contain(2)
      p.get should contain(3)
      p.get should contain(0)
      r2.childNodes(0).asInstanceOf[JSOption].selected should be(true)

      r2.childNodes(0).asInstanceOf[JSOption].selected = false
      r2.onchange(null)
      p.get.size should be(2)
      p.get should contain(2)
      p.get should contain(3)
      r.childNodes(0).asInstanceOf[JSOption].selected should be(false)

      p.listenersCount() should be(10)

      input2.kill()
      p.listenersCount() should be(5)

      r.childNodes(4).asInstanceOf[JSOption].selected = true
      r.onchange(null)
      p.get.size should be(3)
      p.get should contain(2)
      p.get should contain(3)
      p.get should contain(4)
      r2.childNodes(4).asInstanceOf[JSOption].selected should be(false)

      input.kill()
      p.listenersCount() should be(0)

      p.set(Seq.empty)
      r.childNodes(2).asInstanceOf[JSOption].selected should be(true)
      r2.childNodes(2).asInstanceOf[JSOption].selected should be(true)
    }
  }
}
