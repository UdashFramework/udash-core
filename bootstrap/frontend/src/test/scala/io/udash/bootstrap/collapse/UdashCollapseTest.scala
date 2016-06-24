package io.udash.bootstrap.collapse

import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.button.UdashButton
import io.udash.testing.{AsyncUdashFrontendTest, UdashFrontendTest}
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scalatags.JsDom.all._

class UdashCollapseTest extends AsyncUdashFrontendTest {
  import UdashCollapse._

  "UdashCollapse component" should {
    "show/hide on method call and emit events" in {
      val contentId = "collapse-test-content"
      val collapse = UdashCollapse()(
        div(id := contentId)("Something")
      )

      val element = collapse.render
      jQ("body").append(element)

      val q: JQuery = jQ(s"#${collapse.collapseId}")
      def checkState(): (Boolean, Boolean) = {
        (q.hasClass("in"), q.hasClass("collapsing"))
      }

      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      collapse.listen {
        case CollapseShowEvent(_) => showCounter += 1
        case CollapseShownEvent(_) => shownCounter += 1
        case CollapseHideEvent(_) => hideCounter += 1
        case CollapseHiddenEvent(_) => hiddenCounter += 1
      }

      collapse.hide()
      eventually {
        checkState() should be((false, false))
        (showCounter, shownCounter, hideCounter, hiddenCounter) should be(0, 0, 0, 0) // it was hidden already
      } flatMap { case _ =>
        collapse.show()
        eventually {
          checkState() should be((true, false))
          (showCounter, shownCounter, hideCounter, hiddenCounter) should be(1, 1, 0, 0)
        } flatMap { case _ =>
          collapse.hide()
          eventually {
            checkState() should be((false, false))
            (showCounter, shownCounter, hideCounter, hiddenCounter) should be(1, 1, 1, 1)
          } flatMap { case _ =>
            collapse.toggle()
            eventually {
              checkState() should be((true, false))
              (showCounter, shownCounter, hideCounter, hiddenCounter) should be(2, 2, 1, 1)
            } flatMap { case _ =>
              collapse.toggle()
              eventually {
                checkState() should be((false, false))
                (showCounter, shownCounter, hideCounter, hiddenCounter) should be(2, 2, 2, 2)
              }
            }
          }
        }
      }
    }

    "show/hide on button click and emit events" in {
      val contentId = "collapse-test-content"
      val collapse = UdashCollapse()(
        div(id := contentId)("Something")
      )

      val element = collapse.render
      val button = UdashButton()("Toggle", collapse.toggleButtonAttrs()).render
      jQ("body").append(button)
      jQ("body").append(element)

      val q: JQuery = jQ(s"#${collapse.collapseId}")
      def checkState(): (Boolean, Boolean) = {
        (q.hasClass("in"), q.hasClass("collapsing"))
      }

      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      collapse.listen {
        case CollapseShowEvent(_) => showCounter += 1
        case CollapseShownEvent(_) => shownCounter += 1
        case CollapseHideEvent(_) => hideCounter += 1
        case CollapseHiddenEvent(_) => hiddenCounter += 1
      }

      button.click()
      eventually {
        checkState() should be((true, false))
        (showCounter, shownCounter, hideCounter, hiddenCounter) should be(1, 1, 0, 0)
      } flatMap { case _ =>
        button.click()
        eventually {
          checkState() should be((false, false))
          (showCounter, shownCounter, hideCounter, hiddenCounter) should be(1, 1, 1, 1)
        } flatMap { case _ =>
          button.click()
          eventually {
            checkState() should be((true, false))
            (showCounter, shownCounter, hideCounter, hiddenCounter) should be(2, 2, 1, 1)
          } flatMap { case _ =>
            button.click()
            eventually {
              checkState() should be((false, false))
              (showCounter, shownCounter, hideCounter, hiddenCounter) should be(2, 2, 2, 2)
            }
          }
        }
      }
    }
  }
}
