package io.udash.bootstrap.collapse

import io.udash.bootstrap._
import io.udash.bootstrap.button.UdashButton
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._
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

      val q: JQuery = jQ(s"#${collapse.componentId}")
      def checkState(): (Boolean, Boolean) = {
        (q.hasClass("show"), q.hasClass("collapsing"))
      }

      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      collapse.listen {
        case CollapseEvent(_, CollapseEvent.EventType.Show) => showCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Shown) => shownCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Hide) => hideCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Hidden) => hiddenCounter += 1
      }

      collapse.hide()
      retrying {
        checkState() should be((false, false))
        (showCounter, shownCounter, hideCounter, hiddenCounter) should be((0, 0, 0, 0)) // it was hidden already
      } flatMap { case _ =>
        collapse.show()
        retrying {
          checkState() should be((true, false))
          (showCounter, shownCounter, hideCounter, hiddenCounter) should be((1, 1, 0, 0))
        } flatMap { case _ =>
          collapse.hide()
          retrying {
            checkState() should be((false, false))
            (showCounter, shownCounter, hideCounter, hiddenCounter) should be((1, 1, 1, 1))
          } flatMap { case _ =>
            collapse.toggle()
            retrying {
              checkState() should be((true, false))
              (showCounter, shownCounter, hideCounter, hiddenCounter) should be((2, 2, 1, 1))
            } flatMap { case _ =>
              collapse.toggle()
              retrying {
                checkState() should be((false, false))
                (showCounter, shownCounter, hideCounter, hiddenCounter) should be((2, 2, 2, 2))
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
      val button = UdashButton()(_ => Seq[Modifier]("Toggle", collapse.toggleButtonAttrs())).render
      jQ("body").append(button)
      jQ("body").append(element)

      val q: JQuery = jQ(s"#${collapse.componentId}")
      def checkState(): (Boolean, Boolean) = {
        (q.hasClass("show"), q.hasClass("collapsing"))
      }

      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      collapse.listen {
        case CollapseEvent(_, CollapseEvent.EventType.Show) => showCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Shown) => shownCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Hide) => hideCounter += 1
        case CollapseEvent(_, CollapseEvent.EventType.Hidden) => hiddenCounter += 1
      }

      button.click()
      retrying {
        checkState() should be((true, false))
        (showCounter, shownCounter, hideCounter, hiddenCounter) should be((1, 1, 0, 0))
      } flatMap { case _ =>
        button.click()
        retrying {
          checkState() should be((false, false))
          (showCounter, shownCounter, hideCounter, hiddenCounter) should be((1, 1, 1, 1))
        } flatMap { case _ =>
          button.click()
          retrying {
            checkState() should be((true, false))
            (showCounter, shownCounter, hideCounter, hiddenCounter) should be((2, 2, 1, 1))
          } flatMap { case _ =>
            button.click()
            retrying {
              checkState() should be((false, false))
              (showCounter, shownCounter, hideCounter, hiddenCounter) should be((2, 2, 2, 2))
            }
          }
        }
      }
    }
  }
}
