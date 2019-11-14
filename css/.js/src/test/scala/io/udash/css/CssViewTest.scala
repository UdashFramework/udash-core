package io.udash.css

import io.udash._
import io.udash.testing.UdashFrontendTest

class CssViewTest extends UdashFrontendTest {

  import StylesheetExample._
  import scalatags.JsDom.all._

  "CssView" should {
    "provide tools for rendering scalatags with Udash CSS elements" in {
      val el = div(test1, test2, indent(2))("Test").render

      el.textContent should be("Test")
      el.classList.length should be(3)
      el.classList should contain("io-udash-css-StylesheetExample-test1")
      el.classList should contain("io-udash-css-StylesheetExample-test2")
      el.classList should contain("StylesheetExample-indent-2")

      test1.isInstanceOf[CssStyleName] should be(true)
      test2.isInstanceOf[CssStyleName] should be(true)
      indent(2).isInstanceOf[CssStyleName] should be(true)
    }

    "reactive manage results of style factory" in {
      val f1 = (x: String) => if (x.toBoolean) CssStyleName("f1-true") else CssStyleName("f1-false")
      val f2 = (x: Int) => CssStyleName(s"f2-$x")

      val p1 = Property("false")
      val p2 = Property(Option(10))

      val el = div(
        f1.reactiveApply(p1),
        f2.reactiveOptionApply(p2)
      ).render

      el.classList.length should be(2)
      el.classList should contain("f1-false")
      el.classList should contain("f2-10")

      p1.set(null)

      el.classList.length should be(1)
      el.classList should contain("f2-10")

      p1.set("true")

      el.classList.length should be(2)
      el.classList should contain("f1-true")
      el.classList should contain("f2-10")

      p2.set(Some(3))

      el.classList.length should be(2)
      el.classList should contain("f1-true")
      el.classList should contain("f2-3")

      p2.set(None)

      el.classList.length should be(1)
      el.classList should contain("f1-true")

      p2.set(Some(5))

      el.classList.length should be(2)
      el.classList should contain("f1-true")
      el.classList should contain("f2-5")

      p1.set("false")

      el.classList.length should be(2)
      el.classList should contain("f1-false")
      el.classList should contain("f2-5")
    }
  }

  "CssPrefixedStyleName" should {
    "properly add both primary and prefix class to elements" in {
      val el = div(prefixedTest1, prefixedTest2)("Test").render

      el.textContent should be("Test")
      el.classList.length should be(3)
      el.classList should contain("pref")
      el.classList should contain("pref-suff1")
      el.classList should contain("pref-suff2")
    }

    "remove only primary class when other style with the same prefix is present" in {
      val el = div(prefixedTest1, prefixedTest2)("Test").render

      prefixedTest1.removeFrom(el)

      el.classList.length should be(2)
      el.classList should contain("pref")
      el.classList should contain("pref-suff2")
    }

    "remove both primary and prefix class when there is no other style with the same prefix" in {
      val el = div(prefixedTest1)("Test").render

      prefixedTest1.removeFrom(el)

      el.classList.length should be(0)
    }
  }
}
