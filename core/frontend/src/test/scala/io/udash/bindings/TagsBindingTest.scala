package io.udash.bindings

import java.util.concurrent.atomic.AtomicInteger

import io.udash._
import io.udash.properties.{ImmutableValue, seq}
import io.udash.testing.UdashFrontendTest
import org.scalajs.dom.Element

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import io.udash.wrappers.jquery._

class TagsBindingTest extends UdashFrontendTest with Bindings { bindings: Bindings =>
  import scalatags.JsDom.all._

  "bind" should {
    "update content of DOM element" in {
      val p = Property[String]("A")
      val template = div(
        span(),
        bind(p),
        span()
      ).render
      val template2 = div(bind(p)).render

      template.textContent should be("A")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("A")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("A")

      p.set("B")
      template.textContent should be("B")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("B")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("B")

      p.set("ABC")
      template.textContent should be("ABC")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("ABC")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("ABC")
    }

    "handle null value providing empty span element" in {
      class C(val i: Int) {
        override def toString: String =
          s"C($i)"
      }
      implicit val allowCTpe: ImmutableValue[C] = null

      val p = Property[C]
      val template = div(bind(p)).render
      val template2 = div(bind(p)).render

      template.textContent should be("")
      template2.textContent should be("")

      p.set(new C(2))
      template.textContent should be("C(2)")
      template2.textContent should be("C(2)")

      p.set(null)
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")
      template2.textContent should be("")

      p.set(new C(123))
      template.textContent should be("C(123)")
      template2.textContent should be("C(123)")

      p.set(null)
      template.textContent should be("")
      template2.textContent should be("")
    }

    "not swap position" in {
      val p = Property[String]("A")
      val p2 = Property[String]("B")

      val template = div(
        "1",
        bind(p),
        span("2"),
        bind(p2),
        div("3")
      ).render

      template.textContent should be("1A2B3")

      p.set("C")
      template.textContent should be("1C2B3")

      p2.set("D")
      template.textContent should be("1C2D3")

      p.set(null)
      template.textContent should be("12D3")

      p.set("X")
      template.textContent should be("1X2D3")
    }

    "work after moving element in DOM" in {
      val p = Property[String]("A")
      val b = span(bind(p)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("A")
      template2.textContent should be("")

      p.set("B")
      template.textContent should be("B")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      p.set("ABC")
      template.textContent should be("")
      template2.textContent should be("ABC")

      jQ(template2).children().remove()
      jQ(template).append(b)

      p.set("CBA")
      template.textContent should be("CBA")
      template2.textContent should be("")
    }
  }

  "produce" should {
    "update content of DOM element" in {
      val p = Property[String]("ABC")
      val template = div(
        span(),
        produce(p)((s: String) => {
          b(s).render
        }),
        span()
      ).render

      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("ABC")
      template.childNodes(2).textContent should be("")

      p.set("CBA")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("CBA")
      template.childNodes(2).textContent should be("")
    }

    "handle null value providing empty content" in {
      val p = Property[String]("ABC")
      val template = div(
        produce(p)((s: String) => {
          b(s).render
        })
      ).render

      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("ABC")

      p.set("CBA")
      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("CBA")

      p.set(null)
      template.childElementCount should be(0)

      p.set("")
      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("")
    }

    "allow custom null handling" in {
      val p = Property[String]("ABC")
      val template = div(
        produce(p, checkNull = false)((s: String) => {
          if (s != null) b(s).render
          else i("empty").render
        })
      ).render

      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("ABC")

      p.set("CBA")
      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("CBA")

      p.set(null)
      template.childNodes.apply(0).nodeName should be("I")
      template.childNodes.apply(0).textContent should be("empty")

      p.set("")
      template.childNodes.apply(0).nodeName should be("B")
      template.childNodes.apply(0).textContent should be("")
    }

    "not swap position" in {
      val p = Property[String]("A")
      val p2 = Property[String]("B")
      val template = div(
        "1",
        produce(p)((s: String) => {
          b(s).render
        }),
        span("2"),
        produce(p2)((s: String) => {
          i(s).render
        }),
        div("3")
      ).render

      template.textContent should be("1A2B3")

      p.set("C")
      template.textContent should be("1C2B3")

      p2.set("D")
      template.textContent should be("1C2D3")

      p2.set("E")
      template.textContent should be("1C2E3")

      p.set(null)
      template.textContent should be("12E3")

      p.set("#")
      template.textContent should be("1#2E3")
    }

    "work after moving element in DOM" in {
      val p = Property[String]("A")
      val b = span(produce(p)(v => span(v).render)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("A")
      template2.textContent should be("")

      p.set("B")
      template.textContent should be("B")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      p.set("ABC")
      template.textContent should be("")
      template2.textContent should be("ABC")

      jQ(template2).children().remove()
      jQ(template).append(b)

      p.set("CBA")
      template.textContent should be("CBA")
      template2.textContent should be("")
    }
  }

  "produce for SeqProperty" should {
    "update content of DOM element" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        span(),
        produce(p)((s: Seq[Int]) => {
          div(s.map(v => {
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          })).render
        }),
        span()
      ).render

      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("DIV")
      template.childNodes(1).childNodes.length should be(3)
      template.childNodes(1).childNodes(0).nodeName should be("I")
      template.childNodes(1).childNodes(0).textContent should be("1")
      template.childNodes(1).childNodes(1).nodeName should be("B")
      template.childNodes(1).childNodes(1).textContent should be("2")
      template.childNodes(1).childNodes(2).nodeName should be("I")
      template.childNodes(1).childNodes(2).textContent should be("3")
      template.childNodes(2).textContent should be("")

      p.set(Seq(2,4,6))
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("DIV")
      template.childNodes(1).childNodes.length should be(3)
      template.childNodes(1).childNodes(0).nodeName should be("B")
      template.childNodes(1).childNodes(0).textContent should be("2")
      template.childNodes(1).childNodes(1).nodeName should be("B")
      template.childNodes(1).childNodes(1).textContent should be("4")
      template.childNodes(1).childNodes(2).nodeName should be("B")
      template.childNodes(1).childNodes(2).textContent should be("6")
      template.childNodes(2).textContent should be("")

      p.set(Seq())
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("DIV")
      template.childNodes(1).childNodes.length should be(0)
      template.childNodes(2).textContent should be("")
    }

    "handle null value providing empty Seq to callback" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        produce(p)((s: Seq[Int]) => {
          div(s.map(v => {
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          })).render
        })
      ).render

      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(3)
      template.childNodes(0).childNodes(0).nodeName should be("I")
      template.childNodes(0).childNodes(0).textContent should be("1")
      template.childNodes(0).childNodes(1).nodeName should be("B")
      template.childNodes(0).childNodes(1).textContent should be("2")
      template.childNodes(0).childNodes(2).nodeName should be("I")
      template.childNodes(0).childNodes(2).textContent should be("3")

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(Seq(2,4,6))
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(3)
      template.childNodes(0).childNodes(0).nodeName should be("B")
      template.childNodes(0).childNodes(0).textContent should be("2")
      template.childNodes(0).childNodes(1).nodeName should be("B")
      template.childNodes(0).childNodes(1).textContent should be("4")
      template.childNodes(0).childNodes(2).nodeName should be("B")
      template.childNodes(0).childNodes(2).textContent should be("6")

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(Seq())
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)
    }

    "not swap position" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val p2 = seq.SeqProperty[Int](3, 2, 1)
      val template = div(
        "A",
        produce(p)((s: Seq[Int]) => {
          div(s.map(v => {
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          })).render
        }),
        span("B"),
        produce(p2)((s: Seq[Int]) => {
          div(s.map(v => {
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          })).render
        }),
        div("C")
      ).render

      template.textContent should be("A123B321C")

      p.set(Seq(4, 3))
      template.textContent should be("A43B321C")

      p2.set(Seq(3))
      template.textContent should be("A43B3C")

      p.set(null)
      template.textContent should be("AB3C")

      p.set(Seq(1, 2, 5))
      template.textContent should be("A125B3C")
    }

    "work after moving element in DOM" in {
      val p = seq.SeqProperty[String]("A")
      val b = span(produce(p)((v: Seq[String]) => span(v.mkString).render)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("A")
      template2.textContent should be("")

      p.set(Seq("B"))
      template.textContent should be("B")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      p.set(Seq("A", "B", "C"))
      template.textContent should be("")
      template2.textContent should be("ABC")

      jQ(template2).children().remove()
      jQ(template).append(b)

      p.set(Seq("C", "B", "A"))
      template.textContent should be("CBA")
      template2.textContent should be("")
    }
  }

  "Patching produce for SeqProperty" should {
    "init and update content of DOM element" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        span(),
        produce(p,
          (seq: Seq[Property[Int]]) => div(seq.map(p => span(s"${p.get} ")): _*).render,
          (patch: Patch[Property[Int]], elem: Element) => {
            val el = jQ(elem)
            val insertBefore = el.children().at(patch.idx)
            if (el.children().length > patch.idx) patch.added.foreach(p => jQ(span(s"${p.get} ").render).insertBefore(insertBefore))
            else patch.added.foreach(p => el.append(span(s"${p.get} ").render))
            patch.removed.foreach(p => el.children().at(patch.idx + patch.added.size).remove())
          }
        ),
        span()
      ).render

      template.childNodes(0).textContent should be("")
      val firstNode = template.childNodes(1)
      firstNode.nodeName should be("DIV")
      firstNode.childNodes.length should be(3)
      template.childNodes(2).textContent should be("")

      p.set(Seq(2,4,6,8,10))
      template.childNodes(0).textContent should be("")
      val secondNode = template.childNodes(1)
      firstNode.hashCode() should be(secondNode.hashCode())
      secondNode.nodeName should be("DIV")
      secondNode.childNodes.length should be(5)
      template.childNodes(2).textContent should be("")

      p.prepend(1,3,5,7,9)
      template.childNodes(0).textContent should be("")
      val thirdNode = template.childNodes(1)
      secondNode.hashCode() should be(thirdNode.hashCode())
      thirdNode.nodeName should be("DIV")
      thirdNode.childNodes.length should be(10)
      template.childNodes(2).textContent should be("")

      p.set(Seq())
      template.childNodes(0).textContent should be("")
      val fourthNode = template.childNodes(1)
      thirdNode.hashCode() should be(fourthNode.hashCode())
      fourthNode.nodeName should be("DIV")
      fourthNode.childNodes.length should be(0)
      template.childNodes(2).textContent should be("")
    }

    "handle null value providing empty Seq to callback" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        produce(p,
          (seq: Seq[Property[Int]]) => div(seq.map(p => span(s"${p.get} ")): _*).render,
          (patch: Patch[Property[Int]], elem: Element) => {
            val el = jQ(elem)
            val insertBefore = el.children().at(patch.idx)
            if (el.children().length > patch.idx) patch.added.foreach(p => jQ(span(s"${p.get} ").render).insertBefore(insertBefore))
            else patch.added.foreach(p => el.append(span(s"${p.get} ").render))
            patch.removed.foreach(p => el.children().at(patch.idx + patch.added.size).remove())
          }
        )
      ).render

      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(3)

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(Seq(2,4,6,8,10))
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(5)

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.prepend(1,3,5,7,9)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(5)

      p.append(2,4,6,8,10)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(10)

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(Seq())
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)

      p.set(null)
      template.childNodes(0).nodeName should be("DIV")
      template.childNodes(0).childNodes.length should be(0)
    }

    "not swap position" in {
      def prod(p: seq.SeqProperty[Int, Property[Int]]) = {
        produce(p,
          (seq: Seq[Property[Int]]) => div(seq.map(p => span(p.get)): _*).render,
          (patch: Patch[Property[Int]], elem: Element) => {
            val el = jQ(elem)
            val insertBefore = el.children().at(patch.idx)
            if (el.children().length > patch.idx) patch.added.foreach(p => jQ(span(p.get).render).insertBefore(insertBefore))
            else patch.added.foreach(p => el.append(span(p.get).render))
            patch.removed.foreach(p => el.children().at(patch.idx + patch.added.size).remove())
          }
        )
      }

      val p = seq.SeqProperty[Int](1, 2, 3)
      val p2 = seq.SeqProperty[Int](3, 2, 1)
      val template = div(
        "A",
        prod(p),
        span("B"),
        prod(p2),
        div("C")
      ).render

      template.textContent should be("A123B321C")

      p.set(Seq(4, 3))
      template.textContent should be("A43B321C")

      p2.set(Seq(3))
      template.textContent should be("A43B3C")

      p.set(null)
      template.textContent should be("AB3C")

      p.set(Seq(1, 2, 5))
      template.textContent should be("A125B3C")
    }
  }

  "repeat" should {
    "update content of DOM element" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        span(),
        repeat(p)((p: Property[Int]) => {
          val v = p.get
          if (v % 2 == 0) b(v.toString).render
          else i(v.toString).render
        }),
        span()
      ).render

      template.childNodes.length should be(3+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("I")
      template.childNodes(3).textContent should be("3")
      template.childNodes(4).textContent should be("")

      p.set(Seq())
      template.childNodes.length should be(1+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("")

      p.append(1)
      template.childNodes.length should be(1+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).textContent should be("")

      p.append(2)
      template.childNodes.length should be(2+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).textContent should be("")

      p.set(Seq(2,4,6))
      template.childNodes.length should be(3+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).textContent should be("")

      p.append(8)
      template.childNodes.length should be(4+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).nodeName should be("B")
      template.childNodes(4).textContent should be("8")
      template.childNodes(5).textContent should be("")

      p.set(Seq(2,4,6))
      template.childNodes.length should be(3+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).textContent should be("")

      p.append(1,3,5,8)
      template.childNodes.length should be(7+2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).nodeName should be("I")
      template.childNodes(4).textContent should be("1")
      template.childNodes(5).nodeName should be("I")
      template.childNodes(5).textContent should be("3")
      template.childNodes(6).nodeName should be("I")
      template.childNodes(6).textContent should be("5")
      template.childNodes(7).nodeName should be("B")
      template.childNodes(7).textContent should be("8")
      template.childNodes(8).textContent should be("")

      p.set(Seq())
      template.childNodes(0).textContent should be("")
      template.childNodes.length should be(1+2) // placeholder
      template.childNodes(1).textContent should be("")

      p.prepend(1,3)
      template.childNodes(0).textContent should be("")
      template.childNodes.length should be(2+2)
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("I")
      template.childNodes(2).textContent should be("3")
      template.childNodes(3).textContent should be("")

      p.insert(1, 2)
      template.childNodes(0).textContent should be("")
      template.childNodes.length should be(3+2)
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("I")
      template.childNodes(3).textContent should be("3")
      template.childNodes(4).textContent should be("")
    }

    "handle null value providing empty text placeholder" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        repeat(p)((p: Property[Int]) => {
          val v = p.get
          if (v % 2 == 0) b(v.toString).render
          else i(v.toString).render
        })
      ).render

      template.childNodes.length should be(3)
      template.childNodes(0).nodeName should be("I")
      template.childNodes(0).textContent should be("1")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("I")
      template.childNodes(2).textContent should be("3")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.append(1)
      template.childNodes.length should be(1)
      template.textContent should be("1")

      p.append(2)
      template.childNodes.length should be(2)
      template.textContent should be("12")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.set(Seq(2,4,6))
      template.childNodes.length should be(3)
      template.childNodes(0).nodeName should be("B")
      template.childNodes(0).textContent should be("2")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("4")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("6")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.append(1,3,5,8)
      template.childNodes.length should be(4)
      template.childNodes(0).nodeName should be("I")
      template.childNodes(0).textContent should be("1")
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("3")
      template.childNodes(2).nodeName should be("I")
      template.childNodes(2).textContent should be("5")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("8")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.set(Seq())
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")

      p.prepend(1,3)
      template.childNodes.length should be(2)
      template.childNodes(0).nodeName should be("I")
      template.childNodes(0).textContent should be("1")
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("3")

      p.set(null)
      template.childNodes.length should be(1) // placeholder
      template.textContent should be("")
      template.childNodes(0).nodeName should be("#text")
    }

    "not swap position" in {
      def rep(p: seq.SeqProperty[Int, Property[Int]]) = {
        repeat(p)((p: Property[Int]) => {
          val v = p.get
          if (v % 2 == 0) b(v.toString).render
          else i(v.toString).render
        })
      }

      val p = seq.SeqProperty[Int](1, 2, 3)
      val p2 = seq.SeqProperty[Int](3, 2, 1)
      val template = div(
        "A",
        rep(p),
        span("B"),
        rep(p2),
        div("C")
      ).render
      val template2 = div(
        rep(p),
        rep(p2)
      ).render


      template.textContent should be("A123B321C")
      template2.textContent should be("123321")

      p.set(Seq(4, 3))
      template.textContent should be("A43B321C")
      template2.textContent should be("43321")

      p2.set(Seq(3))
      template.textContent should be("A43B3C")
      template2.textContent should be("433")

      p.set(null)
      template.textContent should be("AB3C")
      template2.textContent should be("3")

      p.set(Seq(1, 2, 5))
      template.textContent should be("A125B3C")
      template2.textContent should be("1253")

      p.remove(5)
      template.textContent should be("A12B3C")
      template2.textContent should be("123")

      p.insert(1, 7)
      template.textContent should be("A172B3C")
      template2.textContent should be("1723")

      p.prepend(9)
      template.textContent should be("A9172B3C")
      template2.textContent should be("91723")

      p.remove(9)
      template.textContent should be("A172B3C")
      template2.textContent should be("1723")

      p.replace(0, 2, 6, 6, 6)
      template.textContent should be("A6662B3C")
      template2.textContent should be("66623")
    }

    "not swap position with CallbackSequencer" in {
      def rep(p: seq.SeqProperty[Int, Property[Int]]) = {
        repeat(p)((p: Property[Int]) => {
          val v = p.get
          if (v % 2 == 0) b(v.toString).render
          else i(v.toString).render
        })
      }

      val p = seq.SeqProperty[Int](1, 2, 3)
      val p2 = seq.SeqProperty[Int](3, 2, 1)
      val template = div(
        "A",
        rep(p),
        span("B"),
        rep(p2),
        div("C")
      ).render
      val template2 = div(
        rep(p),
        rep(p2)
      ).render


      template.textContent should be("A123B321C")
      template2.textContent should be("123321")

      p.set(Seq(1, 2, 5))
      CallbackSequencer.sequence {
        p.remove(5)
        p.insert(1, 7)
        p.prepend(9)
        p.remove(9)
        p.replace(0, 2, 6, 6, 6)
      }

      template.textContent should be("A6662B321C")
      template2.textContent should be("6662321")
    }

    "work with filtered SeqProperty" in {
      sealed abstract class NumbersFilter(val matcher: (Int) => Boolean)
      case object OddsFilter       extends NumbersFilter(i => i % 2 == 1)
      case object EvensFilter      extends NumbersFilter(i => i % 2 == 0)

      val filter = Property[NumbersFilter]
      val numbers = seq.SeqProperty[Int]

      filter.set(OddsFilter)
      numbers.set(Seq(1, 2, 3, 4, 5))

      val dom = div(
        produce(filter)(f => ul(
          repeat(numbers.filter(f.matcher))(i => li(i.get).render)
        ).render)
      ).render

      dom.textContent should be("135")

      filter.set(EvensFilter)
      dom.textContent should be("24")

      filter.set(OddsFilter)
      dom.textContent should be("135")

      filter.set(EvensFilter)
      dom.textContent should be("24")

      numbers.append(6, 7, 8)
      dom.textContent should be("2468")

      numbers.append(6, 7, 8)
      dom.textContent should be("246868")

      numbers.set(Seq(1, 3, 5))
      dom.textContent should be("")

      filter.set(OddsFilter)
      dom.textContent should be("135")

      numbers.append(6, 7, 8)
      dom.textContent should be("1357")
    }

    "work with filtered transformed SeqProperty" in {
      val doubles = seq.SeqProperty[Double](1.5, 2.3, 3.7)
      val ints = doubles.transform((d: Double) => d.toInt, (i: Int) => i.toDouble)
      val evens = ints.filter(_ % 2 == 0)

      val dom = div(
        "Doubles: ", repeat(doubles)(p => span(p.get, ", ").render), ";",
        "Ints: ", repeat(ints)(p => span(p.get, ", ").render), ";",
        "Evens: ", repeat(evens)(p => span(p.get, ", ").render)
      ).render

      dom.textContent should be("Doubles: 1.5, 2.3, 3.7, ;Ints: 1, 2, 3, ;Evens: 2, ")

      doubles.prepend(8.5)
      dom.textContent should be("Doubles: 8.5, 1.5, 2.3, 3.7, ;Ints: 8, 1, 2, 3, ;Evens: 8, 2, ")

      ints.append(12)
      dom.textContent should be("Doubles: 8.5, 1.5, 2.3, 3.7, 12, ;Ints: 8, 1, 2, 3, 12, ;Evens: 8, 2, 12, ")

      doubles.replace(1, 3, 4.5)
      dom.textContent should be("Doubles: 8.5, 4.5, 12, ;Ints: 8, 4, 12, ;Evens: 8, 4, 12, ")

      doubles.remove(8.5)
      dom.textContent should be("Doubles: 4.5, 12, ;Ints: 4, 12, ;Evens: 4, 12, ")

      ints.remove(12)
      dom.textContent should be("Doubles: 4.5, ;Ints: 4, ;Evens: 4, ")

      doubles.remove(4.5)
      dom.textContent should be("Doubles: ;Ints: ;Evens: ")

      ints.append(6)
      dom.textContent should be("Doubles: 6, ;Ints: 6, ;Evens: 6, ")
    }

    "work with filtered SeqProperty and CallbackSequencer" in {
      sealed abstract class NumbersFilter(val matcher: (Int) => Boolean)
      case object OddsFilter       extends NumbersFilter(i => i % 2 == 1)
      case object EvensFilter      extends NumbersFilter(i => i % 2 == 0)

      val filter = Property[NumbersFilter]
      val numbers = seq.SeqProperty[Int]

      filter.set(OddsFilter)
      numbers.set(Seq(1, 2, 3, 4, 5))

      val dom = div(
        produce(filter)(f => ul(
          repeat(numbers.filter(f.matcher))(i => li(i.get).render)
        ).render)
      ).render

      dom.textContent should be("135")

      CallbackSequencer.sequence {
        filter.set(EvensFilter)
        filter.set(OddsFilter)
        filter.set(EvensFilter)
        numbers.append(6, 7, 8)
        numbers.append(6, 7, 8)
      }

      dom.textContent should be("246868")

      CallbackSequencer.sequence {
        numbers.set(Seq(1, 3, 5))
        filter.set(OddsFilter)
        numbers.append(6, 7, 8)
      }
      dom.textContent should be("1357")

      filter.set(OddsFilter)
      numbers.set(Seq(1, 3, 5))
      dom.textContent should be("135")
      CallbackSequencer.sequence {
        numbers.elemProperties.foreach(p => p.set(p.get + 1))
      }
      dom.textContent should be("")
    }

    "work with filtered SeqProperty of models" in {
      sealed abstract class TodosFilter(val matcher: (TodoElement) => Boolean)
      case object AllTodosFilter       extends TodosFilter(_ => true)
      case object ActiveTodosFilter    extends TodosFilter(todo => !todo.completed)
      case object CompletedTodosFilter extends TodosFilter(todo => todo.completed)

      trait TodoElement {
        def name: String
        def completed: Boolean
      }

      case class Todo(override val name: String,
                      override val completed: Boolean) extends TodoElement

      val filter = Property[TodosFilter]
      val todos = seq.SeqProperty[TodoElement]

      val done = todos.filter(CompletedTodosFilter.matcher)
      val patches = scala.collection.mutable.ArrayBuffer.empty[Patch[_]]
      done.listenStructure(p => patches += p)


      filter.set(AllTodosFilter)
      todos.set(Seq(
        Todo("A", false),
        Todo("B", false),
        Todo("C", false),
        Todo("D", false),
        Todo("E", false)
      ))

      patches.size should be(0)
      patches.clear()

      var repeats = mutable.ArrayBuffer[AtomicInteger]()

      val dom = div(
        produce(filter)(f => {
          val i = new AtomicInteger(0)
          repeats.append(i)
          ul(
            repeat(todos.filter(f.matcher))(todo => {
              i.incrementAndGet()
              li(todo.get.name).render
            })
          ).render
        })
      ).render

      dom.textContent should be("ABCDE")

      filter.set(ActiveTodosFilter)
      dom.textContent should be("ABCDE")

      filter.set(CompletedTodosFilter)
      dom.textContent should be("")

      val chars = Seq("A", "B", "C", "D", "E")
      for (i <- 1 to 50) {
        val char = chars(i % 5)
        todos.elemProperties(i % 5).asModel.subProp(_.completed).set(true)
        dom.textContent should be(char)
        patches.size should be(1)
        patches.clear()
        repeats.last.get should be(1)

        filter.set(ActiveTodosFilter)
        dom.textContent should be("ABCDE".replace(char, ""))
        repeats.last.get should be(4)

        filter.set(CompletedTodosFilter)
        dom.textContent should be(char)
        repeats.last.get should be(1)

        todos.elemProperties(i % 5).asModel.subProp(_.completed).set(false)
        dom.textContent should be("")
        patches.size should be(1)
        patches.clear()
        repeats.last.get should be(1)

        filter.set(ActiveTodosFilter)
        dom.textContent should be("ABCDE")
        repeats.last.get should be(5)

        filter.set(CompletedTodosFilter)
        dom.textContent should be("")
        repeats.last.get should be(0)
      }

      filter.set(ActiveTodosFilter)
      todos.elemProperties.foreach(_.asModel.subProp(_.completed).set(false))
      repeats.last.set(0)
      for (i <- 1 to 50) {
        val char = chars(i % 5)
        todos.elemProperties(i % 5).asModel.subProp(_.completed).set(true)
        dom.textContent should be("ABCDE".replace(char, ""))
        patches.size should be(1)
        patches.clear()

        todos.elemProperties(i % 5).asModel.subProp(_.completed).set(false)
        dom.textContent should be("ABCDE")
        patches.size should be(1)
        patches.clear()
      }

      repeats.last.get should be(50)
    }

    "work after moving element in DOM" in {
      val p = seq.SeqProperty[String]("A")
      val b = span(repeat(p)((v: Property[String]) => span(v.get).render)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("A")
      template2.textContent should be("")

      p.set(Seq("B"))
      template.textContent should be("B")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      p.set(Seq("A", "B", "C"))
      template.textContent should be("")
      template2.textContent should be("ABC")

      jQ(template2).children().remove()
      jQ(template).append(b)

      p.set(Seq("C", "B", "A"))
      template.textContent should be("CBA")
      template2.textContent should be("")
    }
  }

  "bindValidation" should {
    "render init view on validation start" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int)(implicit ec: ExecutionContext): Future[ValidationResult] = {
          val result = Promise[ValidationResult]
          result.future
        }
      })

      val template = div(
        bindValidation(p,
          _ => i("Validating...").render,
          _ => b("done").render,
          _ => b("error").render
        )
      ).render

      template.textContent should be("Validating...")
    }

    "render result" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int)(implicit ec: ExecutionContext): Future[ValidationResult] = Future.successful(Valid)
      })

      val template = div(
        span(),
        bindValidation(p,
          _ => i("Validating...").render,
          _ => b("done").render,
          _ => b("error").render
        ),
        span()
      ).render

      template.textContent should be("done")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("done")
      template.childNodes(2).textContent should be("")
    }

    "render error if Future failed" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int)(implicit ec: ExecutionContext): Future[ValidationResult] = Future.failed(new NullPointerException)
      })

      val template = div(
        bindValidation(p,
          _ => i("Validating...").render,
          _ => b("done").render,
          _ => b("error").render
        )
      ).render

      template.textContent should be("error")
    }

    "not swap position" in {
      val p = Property[Int](5)
      val p2 = Property[Int](3)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int)(implicit ec: ExecutionContext): Future[ValidationResult] = Future.failed(new NullPointerException)
      })

      val template = div(
        "1",
        bindValidation(p,
          _ => i("Validating...").render,
          _ => b("done").render,
          _ => b("error").render
        ),
        span("2"),
        bindValidation(p,
          _ => i("Validating...").render,
          _ => b("done").render,
          _ => b("Error").render
        ),
        div("3")
      ).render

      template.textContent should be("1error2Error3")

      p.set(-8)
      template.textContent should be("1error2Error3")

      p2.set(2)
      template.textContent should be("1error2Error3")

      p.set(-5)
      template.textContent should be("1error2Error3")
    }
  }

  "bindAttribute" should {
    "update element on property change" in {
      val p = Property[Int](5)

      val template = div(
        id := "someId",
        bindAttribute(p)((i: Int, el: Element) => {
          el.setAttribute("class", s"c$i")
        })
      ).render

      template.getAttribute("class") should be("c5")

      p.set(-8)
      template.getAttribute("class") should be("c-8")

      p.set(0)
      template.getAttribute("class") should be("c0")
    }
  }
}
