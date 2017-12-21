package io.udash.bindings

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

class QueuedNodeModifierTest extends AsyncUdashFrontendTest with Bindings { bindings: Bindings =>
  import scalatags.JsDom.all._

  "queuedNode" should {
    "render placeholder and replace it with provided node" in {
      val template = div(
        "start",
        queuedNode {
          span("heavy thing").render
        },
        "end"
      ).render

      template.childNodes.length should be(3)
      template.childNodes(0).textContent should be("start")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("end")

      retrying {
        template.childNodes(0).textContent should be("start")
        template.childNodes(1).textContent should be("heavy thing")
        template.childNodes(2).textContent should be("end")
      }
    }

    "render node with timeout > 0" in {
      val template = div(
        "start",
        queuedNode({
          span("heavy thing 2").render
        }, (patienceConfig.interval.millisPart * 3).toInt),
        "end"
      ).render

      template.childNodes.length should be(3)
      template.childNodes(0).textContent should be("start")
      template.childNodes(1).textContent should be("")
      template.childNodes(2).textContent should be("end")

      var emptyTests = 0
      retrying {
        if (template.childNodes(1).textContent.isEmpty) emptyTests += 1
        template.childNodes(0).textContent should be("start")
        template.childNodes(1).textContent should be("heavy thing 2")
        template.childNodes(2).textContent should be("end")
        emptyTests > 1 should be(true)
      }
    }
  }
}
