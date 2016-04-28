package io.udash.view

import io.udash.testing.{TestView, UdashFrontendTest}

class ViewRendererTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

  "ViewRenderer" should {
    "render clear views hierarchy" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Nil, rootView :: childViewA :: childViewB :: childViewC :: Nil)

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

      renderer.renderView(Nil, rootView :: childViewA :: childViewB :: childViewC :: Nil)

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

      renderer.renderView(rootView :: childViewA :: Nil, childViewC :: childViewB :: Nil)

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

      renderer.renderView(Nil, rootView :: childViewA :: childViewB :: childViewC :: Nil)

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

      renderer.renderView(rootView :: childViewB :: childViewA :: Nil, childViewC :: Nil)

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

      renderer.renderView(Nil, rootView :: childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(rootView :: childViewA :: Nil, Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)
    }

    "handle adding more than one view to existing path and rerendering of the same path" in {
      val renderer = new ViewRenderer(emptyComponent())

      val rootView = new TestView
      val childViewA = new TestView
      val childViewB = new TestView
      val childViewC = new TestView

      renderer.renderView(Nil, rootView :: Nil)
      renderer.renderView(rootView :: Nil, childViewA :: childViewB :: childViewC :: Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)

      renderer.renderView(rootView :: childViewA :: childViewB :: childViewC :: Nil, Nil)

      rootView.lastChild should be(childViewA)
      childViewA.lastChild should be(childViewB)
      childViewB.lastChild should be(childViewC)
      childViewC.lastChild should be(null)

      rootView.renderingCounter should be(1)
      childViewA.renderingCounter should be(1)
      childViewB.renderingCounter should be(1)
      childViewC.renderingCounter should be(1)
    }
  }
}
