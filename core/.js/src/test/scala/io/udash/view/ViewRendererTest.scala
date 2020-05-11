package io.udash.view

import com.avsystem.commons._
import io.udash.testing.{TestFinalView, TestView, UdashFrontendTest}
import org.scalactic.source.Position
import org.scalajs.dom

class ViewRendererTest extends UdashFrontendTest {
  "ViewRenderer" should {
    "render clear views hierarchy" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Iterator.empty, rootView :: childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)
    }

    "render changed views without touching old ones" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Iterator.empty, rootView :: childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      //clear last child
      Seq(rootView, childViewA, childViewB, childViewC).foreach(_.lastChild = null)

      renderer.renderView(Iterator(rootView, childViewA), childViewC :: childViewB :: Nil)

      rootView.lastChild should be(null) // renderChild was not called
      childViewA.lastChild should be(childViewC)
      childViewB.lastChild should be(null)
      childViewC.lastChild should be(childViewB)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(2)
      childViewC.renderingCounter should be(2)
    }

    "check old views hierarchy and use only valid prefix" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Iterator.empty, rootView :: childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      //clear last child
      Seq(rootView, childViewA, childViewB, childViewC).foreach(_.lastChild = null)

      renderer.renderView(Iterator(rootView, childViewB, childViewA), childViewC :: Nil)

      rootView.lastChild should be(childViewC)
      childViewA.lastChild should be(null)
      childViewB.lastChild should be(null)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(2)
    }

    "remove old views from hierarchy" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Iterator.empty, rootView :: childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(Iterator(rootView, childViewA), Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)
    }

    def testReplace(endpoint: dom.Element)(implicit position: Position) = {
      val renderer = new ViewRenderer(endpoint)

      val rootView = new TestView
      val rootView2 = new TestView

      renderer.renderView(Iterator.empty, rootView :: Nil)

      endpoint.childNodes.length shouldBe 2
      endpoint.children.length shouldBe 1
      val first = endpoint.firstChild
      val last = endpoint.lastChild
      val content = endpoint.outerHTML

      renderer.renderView(Iterator.empty, rootView2 :: Nil)

      endpoint.childNodes.length shouldBe 2
      endpoint.children.length shouldBe 1
      val first2 = endpoint.firstChild
      val last2 = endpoint.lastChild
      val content2 = endpoint.outerHTML
      first2 should not be first
      last2 should not be last
      content2 should not be content

      renderer.renderView(Iterator(rootView2), rootView :: Nil)

      endpoint.childNodes.length shouldBe 2
      endpoint.children.length shouldBe 1
      endpoint.firstChild should not be first
      endpoint.lastChild should not be last
      endpoint.outerHTML should not be markup

      renderer.renderView(Iterator(rootView2), Nil)
      endpoint.childNodes.length shouldBe 2
      endpoint.children.length shouldBe 1
      endpoint.firstChild shouldBe first2
      endpoint.lastChild shouldBe last2
      endpoint.outerHTML shouldBe content2
    }

    "handle replacing the whole hierarchy" in testReplace(emptyComponent())

    "handle non-empty endpoint" in testReplace(emptyComponent().setup(_.appendChild(dom.document.createElement("span"))))

    "handle endpoint with non-element node" in testReplace(emptyComponent().setup(_.appendChild(dom.document.createTextNode("lorem ipsum"))))

    "not try to call renderChild on non-container view" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestFinalView

      renderer.renderView(Iterator.empty, rootView :: Nil)
      renderer.renderView(Iterator(rootView), childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(Iterator(rootView, childViewA, childViewB, childViewC), Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(Iterator(rootView, childViewA, childViewB), Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(Iterator(rootView, childViewA, childViewB), childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(2)
    }
  }
}
