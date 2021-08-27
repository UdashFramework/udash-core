package io.udash.bindings

import com.avsystem.commons._
import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.properties.{HasModelPropertyCreator, seq}
import io.udash.testing.UdashFrontendTest
import org.scalajs.dom.Node

import java.util.concurrent.atomic.AtomicInteger

class TagsBindingTest extends UdashFrontendTest with Bindings { bindings: Bindings =>

  import scalatags.JsDom.all._

  object Model {
    class WithSubClass(val i: Int, val subType: SubClass)
    object WithSubClass extends HasModelPropertyCreator[WithSubClass]

    class SubClass(val i: Int)
    object SubClass extends HasModelPropertyCreator[SubClass]

    trait WithSubTrait {
      def i: Int
      def subType: SubTrait
    }
    object WithSubTrait extends HasModelPropertyCreator[WithSubTrait]

    trait SubTrait {
      def i: Int
    }
    object SubTrait extends HasModelPropertyCreator[SubTrait]
  }

  trait TodoElement {
    def name: String
    def completed: Boolean
  }
  object TodoElement extends HasModelPropertyCreator[TodoElement]

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
        var t: Int = 7
        override def toString: String =
          s"C($i)"
      }

      val p = Property(null: C)
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

      template2.innerHTML = ""
      template.appendChild(b)

      p.set("CBA")
      template.textContent should be("CBA")
      template2.textContent should be("")
    }

    "stop updates after `kill` call" in {
      val p = Property[String]("A")
      val binding = bind(p)
      val template = div(binding).render

      template.textContent should be("A")

      p.set("B")
      template.textContent should be("B")

      binding.kill()

      p.set("C")
      template.textContent should be("B")
    }
  }

  "showIf" should {
    "update content of DOM element" in {
      val p = Property[Boolean](true)
      val element = h1("Test")
      val template = div(
        span(),
        showIf(p)(element.render),
        span()
      ).render
      val template2 = div(showIf(p)(element.render)).render

      template.textContent should be("Test")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("Test")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("Test")

      p.set(false)
      template.textContent should be("")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("")

      p.set(true)
      template.textContent should be("Test")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("Test")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("Test")
    }

    "not swap position" in {
      val p = Property(true)
      val p2 = Property(false)

      val element = h1("Test").render
      val element2 = h1("ABC").render

      val template = div(
        "1",
        showIf(p)(element),
        span("2"),
        showIf(p2)(element2),
        div("3")
      ).render

      template.textContent should be("1Test23")

      p.set(false)
      template.textContent should be("123")

      p2.set(true)
      template.textContent should be("12ABC3")

      p.set(true)
      template.textContent should be("1Test2ABC3")

      p.set(false)
      template.textContent should be("12ABC3")

      p2.set(false)
      template.textContent should be("123")
    }

    "work after moving element in DOM" in {
      val p = Property(true)
      val element = h1("Test").render
      val b = span(showIf(p)(element)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("Test")
      template2.textContent should be("")

      p.set(false)
      template.textContent should be("")
      template2.textContent should be("")

      p.set(true)
      template.textContent should be("Test")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      template.textContent should be("")
      template2.textContent should be("Test")

      p.set(false)
      template.textContent should be("")
      template2.textContent should be("")

      template2.innerHTML = ""
      template.appendChild(b)

      p.set(true)
      template.textContent should be("Test")
      template2.textContent should be("")
    }

    "use custom elements replace method" in {
      def customReplace(res: Boolean) = (root: Node, oldEls: Seq[Node], newEls: Seq[Node]) => {
        oldEls.foreach(_.textContent = "OLD")
        res
      }
      val p = Property[Boolean](true)
      val element = h1("Test").render
      val template = div(
        span(),
        showIf(p, customReplace(true))(element),
        span()
      ).render
      val element2 = h1("Test").render
      val template2 = div(
        span(),
        showIf(p, customReplace(false))(element2),
        span()
      ).render

      template.textContent should be("Test")
      template2.textContent should be("")

      p.set(false)
      template.textContent should be("")
      element.textContent should be("OLD")
      template2.textContent should be("")
      element2.textContent should be("OLD")

      p.set(true)
      template.textContent should be("OLD")
      element.textContent should be("OLD")
      template2.textContent should be("")
      element2.textContent should be("OLD")
    }

    "render DocumentFragment elements" in {
      val p = Property(true)
      val template = div(
        showIf(p)(Seq("A", "B", "C").render)
      ).render

      template.textContent should be("ABC")

      p.toggle()
      template.textContent should be("")

      p.toggle()
      template.textContent should be("ABC")
    }
  }

  "showIfElse" should {
    "update content of DOM element" in {
      val p = Property[Boolean](true)
      val element = h1("Test")
      val elseElement = h1("Else")
      val template = div(
        span(),
        showIfElse(p)(element.render, elseElement.render),
        span()
      ).render
      val template2 = div(showIfElse(p)(element.render, elseElement.render)).render

      template.textContent should be("Test")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("Test")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("Test")

      p.set(false)
      template.textContent should be("Else")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("Else")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("Else")

      p.set(true)
      template.textContent should be("Test")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("Test")
      template.childNodes(2).textContent should be("")
      template2.textContent should be("Test")
    }

    "not swap position" in {
      val p = Property(true)
      val p2 = Property(false)

      val element = h1("Test").render
      val element2 = h1("ABC").render
      val elseElement = h1("Else").render

      val template = div(
        "1",
        showIfElse(p)(element, elseElement),
        span("2"),
        showIf(p2)(element2),
        div("3")
      ).render

      template.textContent should be("1Test23")

      p.set(false)
      template.textContent should be("1Else23")

      p2.set(true)
      template.textContent should be("1Else2ABC3")

      p.set(true)
      template.textContent should be("1Test2ABC3")

      p.set(false)
      template.textContent should be("1Else2ABC3")

      p2.set(false)
      template.textContent should be("1Else23")
    }

    "work after moving element in DOM" in {
      val p = Property(true)
      val element = h1("Test").render
      val elseElement = h1("Else").render
      val b = span(showIfElse(p)(element, elseElement)).render
      val template = div(b).render
      val template2 = emptyComponent()

      template.textContent should be("Test")
      template2.textContent should be("")

      p.set(false)
      template.textContent should be("Else")
      template2.textContent should be("")

      p.set(true)
      template.textContent should be("Test")
      template2.textContent should be("")

      template.removeChild(b)
      template2.appendChild(b)

      template.textContent should be("")
      template2.textContent should be("Test")

      p.set(false)
      template.textContent should be("")
      template2.textContent should be("Else")

      template2.innerHTML = ""
      template.appendChild(b)

      p.set(true)
      template.textContent should be("Test")
      template2.textContent should be("")
    }

    "use custom elements replace method" in {
      def customReplace(res: Boolean) = (root: Node, oldEls: Seq[Node], newEls: Seq[Node]) => {
        oldEls.foreach(_.textContent = "OLD")
        res
      }
      val p = Property[Boolean](true)
      val element = h1("Test").render
      val elementElse = h1("Else").render
      val template = div(
        span(),
        showIfElse(p, customReplace(true))(element, elementElse),
        span()
      ).render
      val element2 = h1("Test").render
      val elementElse2 = h1("Else").render
      val template2 = div(
        span(),
        showIfElse(p, customReplace(false))(element2, elementElse2),
        span()
      ).render

      template.textContent should be("Test")
      template2.textContent should be("")

      p.set(false)
      template.textContent should be("Else")
      element.textContent should be("OLD")
      elementElse.textContent should be("Else")
      template2.textContent should be("")
      element2.textContent should be("OLD")
      elementElse2.textContent should be("Else")

      p.set(true)
      template.textContent should be("OLD")
      element.textContent should be("OLD")
      elementElse.textContent should be("OLD")
      template2.textContent should be("")
      element2.textContent should be("OLD")
      elementElse2.textContent should be("OLD")
    }

    "render DocumentFragment elements" in {
      val p = Property(true)
      val template = div(
        showIfElse(p)(Seq("A", "B", "C").render, span("else").render)
      ).render

      template.textContent should be("ABC")

      p.toggle()
      template.textContent should be("else")

      p.toggle()
      template.textContent should be("ABC")
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

    "handle Seq of produced elements" in {
      val producer = (n: Int) => Seq.fill(n)(b(n.toString).render)

      val p1 = Property[Int](1)
      val p2 = Property[Int](2)
      val p3 = Property[Int](3)
      val template = div(
        span(),
        produce(p1)(producer),
        produce(p2)(producer),
        produce(p3)(producer),
        span()
      ).render

      template.textContent should be("122333")
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("2")
      template.childNodes(4).nodeName should be("B")
      template.childNodes(4).textContent should be("3")
      template.childNodes(5).nodeName should be("B")
      template.childNodes(5).textContent should be("3")
      template.childNodes(6).nodeName should be("B")
      template.childNodes(6).textContent should be("3")
      template.childNodes(7).textContent should be("")

      p1.set(4)
      template.textContent should be("444422333")
      p2.set(3)
      template.textContent should be("4444333333")
      p3.set(1)
      template.textContent should be("44443331")
      p3.set(5)
      template.textContent should be("444433355555")
      p2.set(1)
      template.textContent should be("4444155555")
      p1.set(1)
      template.textContent should be("1155555")
      p2.set(2)
      template.textContent should be("12255555")
      p3.set(3)
      template.textContent should be("122333")
      p2.set(0)
      template.textContent should be("1333")
      p3.set(0)
      template.textContent should be("1")
      p1.set(0)
      template.textContent should be("")
      p2.set(2)
      template.textContent should be("22")
      p3.set(3)
      template.textContent should be("22333")
      p1.set(1)
      template.textContent should be("122333")
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

    "handle empty case class based model properties" in {
      val p = ModelProperty(null: Model.WithSubClass)
      val sub = p.subProp(_.subType)
      val template = div(
        produce(p) { t =>
          div(t.i, t.subType.i).render
        },
        produce(sub) { t =>
          div(t.i).render
        }
      ).render

      template.textContent should be("")

      p.set(new Model.WithSubClass(5, new Model.SubClass(7)))
      template.textContent should be("577")
    }

    "handle empty trait based model properties" in {
      val p = ModelProperty(null: Model.WithSubTrait)
      val sub = p.subProp(_.subType)
      val template = div(
        produce(p) { t =>
          div(t.i, t.subType.i).render
        },
        produce(sub) { t =>
          div(t.i).render
        }
      ).render

      template.textContent should be("")

      p.set(new Model.WithSubTrait {
        override def i = 5
        override def subType = new Model.SubTrait {
          override def i = 7
        }
      })
      template.textContent should be("577")
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

      template2.innerHTML = ""
      template.appendChild(b)

      p.set("CBA")
      template.textContent should be("CBA")
      template2.textContent should be("")
    }

    "stop updates after `kill` call" in {
      val p = Property[String]("A")
      val binding = produce(p) { v => span(v * 3).render }
      val template = div(binding).render

      template.textContent should be("AAA")

      p.set("B")
      template.textContent should be("BBB")

      binding.kill()

      p.set("C")
      template.textContent should be("BBB")
    }

    "clean nested bindings" in {
      val p = Property[String]("A")
      val p2 = Property[String]("a")

      var externalCounter = 0
      var internalCounter = 0

      val template = div(
        produceWithNested(p) { case (v, nested) =>
          externalCounter += 1
          div(v, nested(produce(p2) { v2 =>
            internalCounter += 1
            span(v2).render
          })).render
        }
      ).render

      template.textContent should be("Aa")
      externalCounter should be(1)
      internalCounter should be(1)

      p.set("B")
      template.textContent should be("Ba")
      externalCounter should be(2)
      internalCounter should be(2)

      p.set("C")
      template.textContent should be("Ca")
      externalCounter should be(3)
      internalCounter should be(3)

      p2.set("b")
      template.textContent should be("Cb")
      externalCounter should be(3)
      internalCounter should be(4)
    }

    "use custom elements replace method" in {
      var oldCounter = 0

      def testCustomReplacement(
        produceAndReplace: ReadableProperty[Boolean] => Binding,
        produceManually: ReadableProperty[Boolean] => Binding
      ) = {
        val p = Property[Boolean](true)
        val template = div(
          span(),
          produceAndReplace(p),
          span()
        ).render
        val template2 = div(
          span(),
          produceManually(p),
          span()
        ).render

        template.textContent should be("true")
        template2.textContent should be("")
        oldCounter should be(0)

        p.set(false)
        template.textContent should be("false")
        template2.textContent should be("")
        oldCounter should be(2)

        p.set(true)
        template.textContent should be("true")
        template2.textContent should be("")
        oldCounter should be(4)
      }

      def customReplace(res: Boolean) = (_: Node, oldEls: Seq[Node], _: Seq[Node]) => {
        oldEls.foreach(_ => oldCounter += 1)
        res
      }

      testCustomReplacement(
        produce(_, customElementsReplace = customReplace(true), checkNull = false)(v => div(v.toString).render),
        produce(_, customElementsReplace = customReplace(false), checkNull = false)(v => div(v.toString).render)
      )

      oldCounter = 0

      testCustomReplacement(
        produceWithNested(_, customElementsReplace = customReplace(true), checkNull = false)((v, _) => div(v.toString).render),
        produceWithNested(_, customElementsReplace = customReplace(false), checkNull = false)((v, _) => div(v.toString).render)
      )
    }

    "handle standalone SeqFrag update" in {
      val p = SeqProperty.blank[String]
      val template = div(produce(p)(s => s.render)).render

      template.outerHTML shouldBe "<div></div>"

      p.set(Seq("A", "B", "C"))

      template.outerHTML shouldBe "<div>ABC</div>"
    }

    "handle SeqFrag update" in {
      val p = SeqProperty.blank[String]
      val template = div(produce(p)(s => Seq(
        "test".render,
        s.render
      ))).render
      template.outerHTML shouldBe "<div>test</div>"

      p.set(Seq("A", "B", "C"))
      template.outerHTML shouldBe "<div>testABC</div>"
    }

    "handle nested SeqFrag update" in {
      val p = SeqProperty.blank[String]
      val template = div(produce(p)(s => Seq(
        Seq(s.render).render
      ).render)).render
      template.outerHTML shouldBe "<div></div>"

      p.set(Seq("A", "B", "C"))
      template.outerHTML shouldBe "<div>ABC</div>"
    }

    "handle non-empty SeqFrag update" in {
      val p = SeqProperty("A", "B", "C")
      val template = div(produce(p)(s => s.render)).render

      template.outerHTML shouldBe "<div>ABC</div>"

      p.set(Seq("D", "E", "F"))

      template.outerHTML shouldBe "<div>DEF</div>"
    }
  }

  "produce for SeqProperty" should {
    "update content of DOM element" in {
      val p = seq.SeqProperty[Int](1, 2, 3)
      val template = div(
        span(),
        produce(p)((s: Seq[Int]) =>
          div(s.map(v =>
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          )).render
        ),
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

      p.set(Seq(2, 4, 6))
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
        produce(p)((s: Seq[Int]) =>
          div(s.map(v =>
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          )).render
        )
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

      p.set(Seq(2, 4, 6))
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
        produce(p)((s: Seq[Int]) =>
          div(s.map(v =>
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          )).render
        ),
        span("B"),
        produce(p2)((s: Seq[Int]) =>
          div(s.map(v =>
            if (v % 2 == 0) b(v.toString).render
            else i(v.toString).render
          )).render
        ),
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

      template2.innerHTML = ""
      template.appendChild(b)

      p.set(Seq("C", "B", "A"))
      template.textContent should be("CBA")
      template2.textContent should be("")
    }

    "stop updates after `kill` call" in {
      val p = SeqProperty[Int](1, 2, 3, 4, 5)
      val binding = produce(p) { v => span(v.mkString(",")).render }
      val template = div(binding).render

      template.textContent should be("1,2,3,4,5")

      p.append(7)
      template.textContent should be("1,2,3,4,5,7")

      binding.kill()

      p.remove(2)
      template.textContent should be("1,2,3,4,5,7")
    }

    "clean nested bindings" in {
      val p = SeqProperty[Int](1, 2, 3)
      val p2 = SeqProperty[Int](3, 4, 5)

      var externalCounter = 0
      var internalCounter = 0

      val template = div(
        produceWithNested(p) { case (v, nested) =>
          externalCounter += 1
          div(v.mkString(","), nested(produce(p2) { v2 =>
            internalCounter += 1
            span(v2.mkString("|")).render
          })).render
        }
      ).render

      template.textContent should be("1,2,33|4|5")
      externalCounter should be(1)
      internalCounter should be(1)

      p.append(7)
      template.textContent should be("1,2,3,73|4|5")
      externalCounter should be(2)
      internalCounter should be(2)

      p.remove(2)
      template.textContent should be("1,3,73|4|5")
      externalCounter should be(3)
      internalCounter should be(3)

      p2.append(8)
      template.textContent should be("1,3,73|4|5|8")
      externalCounter should be(3)
      internalCounter should be(4)
    }

    "use custom elements replace method" in {
      var oldCounter = 0

      def testCustomReplacement(
        produceAndReplace: ReadableSeqProperty[String] => Binding,
        produceManually: ReadableSeqProperty[String] => Binding
      ) = {
        val p = SeqProperty[String]("a", "b", "c")
        val template = div(
          span(),
          produceAndReplace(p),
          span()
        ).render
        val template2 = div(
          span(),
          produceManually(p),
          span()
        ).render

        template.textContent should be("a,b,c")
        template2.textContent should be("")
        oldCounter should be(0)

        p.set(Seq("x", "y"))
        template.textContent should be("x,y")
        template2.textContent should be("")
        oldCounter should be(2)

        p.append("z")
        template.textContent should be("x,y,z")
        template2.textContent should be("")
        oldCounter should be(4)
      }

      def customReplace(res: Boolean) = (_: Node, oldEls: Seq[Node], _: Seq[Node]) => {
        oldEls.foreach(_ => oldCounter += 1)
        res
      }

      testCustomReplacement(
        produce(_, customElementsReplace = customReplace(true))(v => div(v.mkString(",")).render),
        produce(_, customElementsReplace = customReplace(false))(v => div(v.mkString(",")).render)
      )

      oldCounter = 0

      testCustomReplacement(
        produceWithNested(_, customElementsReplace = customReplace(true))((v, _) => div(v.mkString(",")).render),
        produceWithNested(_, customElementsReplace = customReplace(false))((v, _) => div(v.mkString(",")).render)
      )
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

      template.childNodes.length should be(3 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("I")
      template.childNodes(3).textContent should be("3")
      template.childNodes(4).textContent should be("")

      p.set(Seq())
      template.childNodes.length should be(1 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("")

      p.append(1)
      template.childNodes.length should be(1 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).textContent should be("")

      p.append(2)
      template.childNodes.length should be(2 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).textContent should be("")

      p.set(Seq(2, 4, 6))
      template.childNodes.length should be(3 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).textContent should be("")

      p.append(8)
      template.childNodes.length should be(4 + 2)
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

      p.set(Seq(2, 4, 6))
      template.childNodes.length should be(3 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("B")
      template.childNodes(1).textContent should be("2")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("4")
      template.childNodes(3).nodeName should be("B")
      template.childNodes(3).textContent should be("6")
      template.childNodes(4).textContent should be("")

      p.append(1, 3, 5, 8)
      template.childNodes.length should be(7 + 2)
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
      template.childNodes.length should be(1 + 2) // placeholder
      template.childNodes(1).textContent should be("")

      p.prepend(1, 3)
      template.childNodes(0).textContent should be("")
      template.childNodes.length should be(2 + 2)
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("I")
      template.childNodes(2).textContent should be("3")
      template.childNodes(3).textContent should be("")

      p.insert(1, 2)
      template.childNodes(0).textContent should be("")
      template.childNodes.length should be(3 + 2)
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("I")
      template.childNodes(3).textContent should be("3")
      template.childNodes(4).textContent should be("")
    }

    "handle init with empty SeqProperty" in {
      val p = seq.SeqProperty.blank[Int]
      val template = div(
        span(),
        repeat(p)((p: Property[Int]) => {
          val v = p.get
          if (v % 2 == 0) b(v.toString).render
          else i(v.toString).render
        }),
        span()
      ).render

      template.childNodes.length should be(3) // spans + placeholder
      p.set(Seq(1,2,3))

      template.childNodes.length should be(3 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).nodeName should be("I")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).nodeName should be("B")
      template.childNodes(2).textContent should be("2")
      template.childNodes(3).nodeName should be("I")
      template.childNodes(3).textContent should be("3")
      template.childNodes(4).textContent should be("")

      p.set(Seq())
      template.childNodes.length should be(1 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("")

      p.append(1)
      template.childNodes.length should be(1 + 2)
      template.childNodes(0).textContent should be("")
      template.childNodes(1).textContent should be("1")
      template.childNodes(2).textContent should be("")
    }

    "handle Seq of produced elements" in {
      val p1 = SeqProperty[Int](1, 2, 3)
      val p2 = SeqProperty[Int](4, 5, 6)
      val p3 = SeqProperty[Int](7, 8, 9)

      val builder = (p: Property[Int]) => Seq.fill(p.get)(b(p.get.toString).render)
      val template = div(
        repeat(p1)(builder),
        repeat(p2)(builder),
        repeat(p3)(builder)
      ).render

      def expectedChildrenCount: Int =
        Seq(p1.get, p2.get, p3.get).flatten.sum

      template.textContent should be("122333444455555666666777777788888888999999999")
      template.childElementCount should be(expectedChildrenCount)

      p2.remove(5)
      template.textContent should be("1223334444666666777777788888888999999999")
      template.childElementCount should be(expectedChildrenCount)

      p1.replace(0, 1, 3)
      template.textContent should be("333223334444666666777777788888888999999999")
      template.childElementCount should be(expectedChildrenCount)

      p3.clear()
      template.textContent should be("333223334444666666")
      template.childElementCount should be(expectedChildrenCount)

      p3.append(0, 0, 0)
      template.textContent should be("333223334444666666")
      template.childElementCount should be(expectedChildrenCount)

      p2.clear()
      template.textContent should be("33322333")
      template.childElementCount should be(expectedChildrenCount)

      p1.clear()
      template.textContent should be("")
      template.childElementCount should be(expectedChildrenCount)

      p2.append(4, 0, 5)
      template.textContent should be("444455555")
      template.childElementCount should be(expectedChildrenCount)

      p3.append(3, 0, 2, 1, 0)
      template.textContent should be("444455555333221")
      template.childElementCount should be(expectedChildrenCount)

      p1.append(1, 2, 3)
      template.textContent should be("122333444455555333221")
      template.childElementCount should be(expectedChildrenCount)

      p1.prepend(0)
      template.textContent should be("122333444455555333221")
      template.childElementCount should be(expectedChildrenCount)

      p1.set(p1.get.filter(_ != 0))
      template.textContent should be("122333444455555333221")
      p2.set(p2.get.filter(_ != 0))
      template.textContent should be("122333444455555333221")
      p3.set(p3.get.filter(_ != 0))
      template.textContent should be("122333444455555333221")
      template.childElementCount should be(expectedChildrenCount)
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

      p.set(Seq(2, 4, 6))
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

      p.append(1, 3, 5, 8)
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

      p.prepend(1, 3)
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
      CallbackSequencer().sequence {
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
      sealed abstract class NumbersFilter(val matcher: Int => Boolean)
      case object OddsFilter extends NumbersFilter(i => i % 2 == 1)
      case object EvensFilter extends NumbersFilter(i => i % 2 == 0)

      val filter = Property[NumbersFilter](OddsFilter)
      val numbers = SeqProperty(1, 2, 3, 4, 5)

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
      val ints = doubles.bitransformElements(_.toInt)(_.toDouble)
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
      sealed abstract class NumbersFilter(val matcher: Int => Boolean)
      case object OddsFilter extends NumbersFilter(i => i % 2 == 1)
      case object EvensFilter extends NumbersFilter(i => i % 2 == 0)

      val filter = Property[NumbersFilter](OddsFilter)
      val numbers = SeqProperty(1, 2, 3, 4, 5)

      val dom = div(
        produce(filter)(f => ul(
          repeat(numbers.filter(f.matcher))(i => li(i.get).render)
        ).render)
      ).render

      dom.textContent should be("135")

      CallbackSequencer().sequence {
        filter.set(EvensFilter)
        filter.set(OddsFilter)
        filter.set(EvensFilter)
        numbers.append(6, 7, 8)
        numbers.append(6, 7, 8)
      }

      dom.textContent should be("246868")

      CallbackSequencer().sequence {
        numbers.set(Seq(1, 3, 5))
        filter.set(OddsFilter)
        numbers.append(6, 7, 8)
      }
      dom.textContent should be("1357")

      filter.set(OddsFilter)
      numbers.set(Seq(1, 3, 5))
      dom.textContent should be("135")
      CallbackSequencer().sequence {
        numbers.elemProperties.foreach(p => p.set(p.get + 1))
      }
      dom.textContent should be("")
    }

    "work with filtered SeqProperty of models" in {
      sealed abstract class TodosFilter(val matcher: TodoElement => Boolean)
      case object AllTodosFilter extends TodosFilter(_ => true)
      case object ActiveTodosFilter extends TodosFilter(todo => !todo.completed)
      case object CompletedTodosFilter extends TodosFilter(todo => todo.completed)

      case class Todo(override val name: String,
        override val completed: Boolean) extends TodoElement

      val filter = Property[TodosFilter](AllTodosFilter)
      val todos = SeqProperty.blank[TodoElement]

      val done = todos.filter(CompletedTodosFilter.matcher)
      val patches = MArrayBuffer.empty[Patch[_]]
      done.listenStructure(p => patches += p)

      todos.set(Seq(
        Todo("A", false),
        Todo("B", false),
        Todo("C", false),
        Todo("D", false),
        Todo("E", false)
      ))

      patches.size should be(0)
      patches.clear()

      val repeats = MArrayBuffer[AtomicInteger]()

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

      template2.innerHTML = ""
      template.appendChild(b)

      p.set(Seq("C", "B", "A"))
      template.textContent should be("CBA")
      template2.textContent should be("")
    }

    "stop updates after `kill` call" in {
      val p = SeqProperty[Int](1, 2, 3, 4, 5)
      val binding = repeat(p) { el =>
        span(el.get).render
      }
      val template = div(binding).render

      template.textContent should be("12345")

      p.append(7)
      template.textContent should be("123457")

      binding.kill()

      p.remove(2)
      template.textContent should be("123457")
    }

    "clean nested bindings" in {
      var counter = 0
      var internalCounter = 0
      val p = SeqProperty[Int](1, 2, 3, 4, 5)
      val binding = repeatWithNested(p) { (el, nested) =>
        counter += 1
        span(nested(produce(el) { v =>
          internalCounter += 1
          i(v).render
        })).render
      }
      val template = div(binding).render

      template.textContent should be("12345")
      counter should be(5)
      internalCounter should be(5)

      p.append(7)
      template.textContent should be("123457")
      counter should be(6)
      internalCounter should be(6)

      val second = p.elemProperties(1)
      p.remove(2)
      template.textContent should be("13457")
      counter should be(6)
      internalCounter should be(6)

      second.set(9)
      template.textContent should be("13457")
      counter should be(6)
      internalCounter should be(6)
    }

    "use custom elements replace method" in {
      var oldCounter = 0
      var newCounter = 0
      def customReplace(res: Boolean) = (root: Node, oldEls: Seq[Node], newEls: Seq[Node]) => {
        oldEls.foreach(_ => oldCounter += 1)
        newEls.foreach(_ => newCounter += 1)
        res
      }
      def customInsert(res: Boolean) = (root: Node, before: Node, newEls: Seq[Node]) => {
        newEls.foreach(_ => newCounter += 1)
        res
      }
      val p = SeqProperty[String]("a", "b", "c")
      val template = div(
        span(),
        repeat(p, customElementsReplace = customReplace(true), customElementsInsert = customInsert(true))(v => div(v.get).render),
        span()
      ).render
      val template2 = div(
        span(),
        repeat(p, customElementsReplace = customReplace(false), customElementsInsert = customInsert(false))(v => div(v.get).render),
        span()
      ).render

      template.textContent should be("abc")
      template2.textContent should be("")
      oldCounter should be(0)
      newCounter should be(6)

      p.set(Seq("x", "y"))
      template.textContent should be("xy")
      template2.textContent should be("")
      oldCounter should be(6)
      newCounter should be(10)

      p.append("z")
      template.textContent should be("xyz")
      template2.textContent should be("")
      oldCounter should be(6)
      newCounter should be(12)
    }

    "handle nested document frag" in {
      val sp = SeqProperty(1, 2, 3)
      val template = div(
        repeat(sp)(p => Seq(p.get.toString).render),
      ).render

      template.textContent should be("123")

      sp.remove(2)

      template.textContent should be("13")
    }
  }

  "repeatWithIndex" should {
    "provide property with element index" in {
      val p = SeqProperty("a", "b", "c", "d")

      val el = div(
        repeatWithIndex(p) { case (item, idx, nested) =>
          span(nested(bind(idx)), nested(bind(item))).render
        }
      ).render

      el.textContent should be("0a1b2c3d")

      p.append("e")
      el.textContent should be("0a1b2c3d4e")

      p.remove("b")
      el.textContent should be("0a1c2d3e")

      p.insert(1, "B")
      el.textContent should be("0a1B2c3d4e")

      p.clear()
      el.textContent should be("")

      p.set(Seq("x", "y", "z"))
      el.textContent should be("0x1y2z")

      p.replace(1, 2, "a", "B")
      el.textContent should be("0x1a2B")

      p.set(Seq("q", "w", "e", "r"))
      el.textContent should be("0q1w2e3r")

      p.set(Seq("z", "x"))
      el.textContent should be("0z1x")

      p.set(Seq("q", "w", "e", "r"))
      el.textContent should be("0q1w2e3r")

      CallbackSequencer().sequence {
        p.set(Seq())
        p.set(Seq("c", "d"))
      }
      el.textContent should be("0c1d")

      CallbackSequencer().sequence {
        p.set(Seq("a", "b"))
        p.set(Seq("x", "y"))
      }
      el.textContent should be("0x1y")
    }

    "set initial index values to corresponding elements' position in underlying SeqProperty" in {
      val p = SeqProperty("a", "b", "c", "d")
      var counter = 0

      val el = div(
        repeatWithIndex(p)((item, idx, nested) =>
          span(nested(bind(
            item.combine(idx) { (itemValue, indexValue) =>
              counter += 1
              s"$indexValue$itemValue"
            }
          ))).render
        )
      ).render

      el.textContent should be("0a1b2c3d")
      counter shouldBe 4
    }
  }

  "AttrOps" should {
    "allow reactive attribute bind" in {
      val p = Property("idValue")
      val b = id.bind(p)
      val textArea = TextArea(Property(""))(b).render
      textArea.getAttribute("id") shouldBe "idValue"
      p.set("idValue2")
      textArea.getAttribute("id") shouldBe "idValue2"
      p.set(null)
      textArea.hasAttribute("disabled") shouldBe false
      p.set("idValue3")
      textArea.getAttribute("id") shouldBe "idValue3"
      p.listenersCount() should be(1)
      b.kill()
      p.listenersCount() should be(0)
    }

    "allow reactive attribute bind with condition" in {
      val p = Property("idValue")
      val c = Property(true)
      val b = id.bindIf(p, c)
      val textArea = TextArea(Property(""))(b).render
      textArea.getAttribute("id") shouldBe "idValue"
      c.set(false)
      textArea.getAttribute("id") shouldBe null
      p.set("idValue2")
      textArea.getAttribute("id") shouldBe null
      c.set(true)
      textArea.getAttribute("id") shouldBe "idValue2"
      p.set(null)
      textArea.hasAttribute("disabled") shouldBe false
      p.set("idValue3")
      textArea.getAttribute("id") shouldBe "idValue3"
      p.listenersCount() should be(1)
      c.listenersCount() should be(1)
      b.kill()
      p.listenersCount() should be(0)
      c.listenersCount() should be(0)
    }
  }

  "AttrPairOps" should {
    "allow reactive attribute apply" in {
      val p = Property(false)
      val binding = (disabled := "disabled").attrIf(p)
      val textArea = TextArea(Property(""))(binding).render
      textArea.hasAttribute("disabled") shouldBe false
      p.set(true)
      textArea.hasAttribute("disabled") shouldBe true
      p.set(false)
      textArea.hasAttribute("disabled") shouldBe false
      binding.kill()
      p.set(true)
      textArea.hasAttribute("disabled") shouldBe false

      p.set(false)
      val binding2 = (disabled := "disabled").attrIfNot(p)
      val textArea2 = TextArea(Property(""))(binding2).render
      textArea2.hasAttribute("disabled") shouldBe true
      p.set(true)
      textArea2.hasAttribute("disabled") shouldBe false
      p.set(false)
      textArea2.hasAttribute("disabled") shouldBe true
      binding2.kill()
      p.set(true)
      textArea2.hasAttribute("disabled") shouldBe true
    }
  }

  "PropertyOps" should {
    "allow reactive attr changes" in {
      val p = Property(false)
      val binding = p.reactiveApply((el, v) => el.setAttribute("test", v.toString))
      val textArea = TextArea(Property(""))(binding).render
      textArea.getAttribute("test").toBoolean shouldBe false
      p.set(true)
      textArea.getAttribute("test").toBoolean shouldBe true
      p.set(false)
      textArea.getAttribute("test").toBoolean shouldBe false
      binding.kill()
      p.set(true)
      textArea.getAttribute("test").toBoolean shouldBe false
    }
  }

  "Opt bindings" should {
    "implicitly convert Opt to Modifier" in {
      val stringOpt: Opt[Modifier] = Opt("test")
      val testDiv = div(stringOpt).render
      testDiv.innerHTML should ===("test")
    }
    "implicitly convert Opt.Empty to Modifier" in {
      val stringOpt: Opt[String] = Opt.Empty
      val testDiv = div(stringOpt).render
      testDiv.innerHTML shouldBe empty
    }
  }

  "InlineStyleOps" should {
    "allow reactive inlined style bind" in {
      val styleProperty = Property("red")
      val pixelStyleProperty = Property("10px")
      val bindStyle = backgroundColor.bind(styleProperty)
      val bindPixelStyle = width.bind(pixelStyleProperty)
      val testDiv = div(bindStyle, bindPixelStyle).render
      testDiv.style.getPropertyValue("background-color") should ===("red")
      testDiv.style.getPropertyValue("width") should ===("10px")
      styleProperty.set("black")
      pixelStyleProperty.set("100px")
      testDiv.style.getPropertyValue("background-color") should ===("black")
      testDiv.style.getPropertyValue("width") should ===("100px")
      styleProperty.set(null)
      pixelStyleProperty.set(null)
      testDiv.style.getPropertyValue("background-color") should ===("")
      testDiv.style.getPropertyValue("width") should ===("")
      styleProperty.set("blue")
      pixelStyleProperty.set("2rem")
      testDiv.style.getPropertyValue("background-color") should ===("blue")
      testDiv.style.getPropertyValue("width") should ===("2rem")
      styleProperty.listenersCount() should be(1)
      pixelStyleProperty.listenersCount() should be(1)
      bindStyle.kill()
      styleProperty.listenersCount() should be(0)
      pixelStyleProperty.listenersCount() should be(1)
      bindPixelStyle.kill()
      styleProperty.listenersCount() should be(0)
      pixelStyleProperty.listenersCount() should be(0)
    }

    "allow reactive inlined style bind with condition" in {
      val styleProperty = Property("red")
      val conditionProperty = Property(true)
      val bindIfStyle = backgroundColor.bindIf(styleProperty, conditionProperty)
      val testDiv = div(bindIfStyle).render
      testDiv.style.getPropertyValue("background-color") should ===("red")
      conditionProperty.set(false)
      testDiv.style.getPropertyValue("background-color") should ===("")
      styleProperty.set("black")
      testDiv.style.getPropertyValue("background-color") should ===("")
      conditionProperty.set(true)
      testDiv.style.getPropertyValue("background-color") should ===("black")
      styleProperty.set(null)
      testDiv.style.getPropertyValue("background-color") should ===("")
      styleProperty.set("blue")
      testDiv.style.getPropertyValue("background-color") should ===("blue")
      styleProperty.listenersCount() should be(1)
      conditionProperty.listenersCount() should be(1)
      bindIfStyle.kill()
      styleProperty.listenersCount() should be(0)
      conditionProperty.listenersCount() should be(0)
    }
  }

  "Sequenced binding updates" should {
    "avoid multiple updates in produceWithNested on single properties" in {
      val p = Property(1)
      val s = Property(2)

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        produceWithNested(p) { (v1, nested) =>
          div(
            nested(produce(s) { v2 =>
              fired += v1 -> v2
              div().render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(3)
        s.set(4)
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (1, 2),
        (3, 4)
      )
    }

    //todo https://github.com/UdashFramework/udash-core/issues/290
    "avoid multiple updates in produceWithNested on transformed properties" ignore {
      val p = Property(2)
      val s = Property(3)

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        produceWithNested(p.transform(_ + 1)) { case (v, nested) =>
          div(
            nested(produce(s) { v2 =>
              fired += v -> v2
              div().render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(20)
        s.set(30)
      }

      CallbackSequencer().sequence {
        s.set(300)
        p.set(200)
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (3, 3),
        (21, 30),
        (21, 300),
        (201, 300)
      )
    }

    "avoid multiple updates in produceWithNested on combined properties" ignore {
      val p = Property(2)
      val c = Property(1)
      val s = Property(3)

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        produceWithNested(p.combine(c)(_ + _)) { case (v, nested) =>
          div(
            nested(produce(s) { v2 =>
              fired += v -> v2
              div().render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(20)
        s.set(30)
      }

      CallbackSequencer().sequence {
        s.set(300)
        p.set(200)
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (3, 3),
        (21, 30),
        (21, 300),
        (201, 300)
      )
    }

    "avoid multiple updates in produceWithNested on SeqProperties" in {
      val p = Property(1)
      val s = SeqProperty(1, 2, 3)

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        produceWithNested(p) { case (v1, nested) =>
          div(
            nested(repeatWithNested(s) { (p2, nested) =>
              div(nested(produce(p2) { v2 =>
                fired += v1 -> v2
                div((v1 -> v2).toString()).render
              })).render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(2)
        s.set(Seq(4, 5, 6))
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (1, 1),
        (1, 2),
        (1, 3),
        (2, 4),
        (2, 5),
        (2, 6)
      )
    }

    "avoid multiple updates in produceWithNested on SeqProperties from single value" in {
      val p = Property(1)
      val source = Property(1)
      val s = source.transformToSeq(i => Seq(i, i + 1, i + 2))

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        produceWithNested(p) { case (v1, nested) =>
          div(
            nested(repeatWithNested(s) { (vs, nested) =>
              div(
                nested(produce(vs) { v2 =>
                  fired += v1 -> v2
                  (v1 -> v2).toString().render
                })
              ).render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(2)
        source.set(4)
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (1, 1),
        (1, 2),
        (1, 3),
        (2, 4),
        (2, 5),
        (2, 6)
      )
    }

    "avoid multiple updates in produceWithNested on zipped SeqProperties" in {
      val p = SeqProperty(1, 2, 3)
      val s = SeqProperty(1, 2, 3)

      val fired = MListBuffer.empty[(Int, Int)]

      div(
        repeatWithNested(p) { case (p1, nested) =>
          div(
            nested(repeatWithNested(s) { (p2, nested) =>
              div(
                nested(produce(p1.combine(p2)(_ -> _)) { case (v1, v2) =>
                  fired += v1 -> v2
                  (v1 -> v2).toString().render
                })
              ).render
            })
          ).render
        }
      ).render

      CallbackSequencer().sequence {
        p.set(Seq(4, 5, 6))
        s.set(Seq(4, 5, 6))
      }

      fired.result() should contain theSameElementsInOrderAs Seq(
        (1, 1),
        (1, 2),
        (1, 3),
        (2, 1),
        (2, 2),
        (2, 3),
        (3, 1),
        (3, 2),
        (3, 3),
        (4, 4),
        (4, 5),
        (4, 6),
        (5, 4),
        (5, 5),
        (5, 6),
        (6, 4),
        (6, 5),
        (6, 6)
      )
    }
  }
}
