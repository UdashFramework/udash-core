package io.udash.bootstrap.dropdown

import io.udash._
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.Button

class UdashDropdownTest extends UdashFrontendTest {
  import UdashDropdown._

  import scalatags.JsDom.all._

  "UdashDropdown component" should {
    val elements: SeqProperty[DefaultDropdownItem] = SeqProperty(Seq(
      DropdownHeader("Header"),
      DropdownLink("Link 1", Url("#")),
      DropdownLink("Link 2", Url("#")),
      DropdownDivider,
      DropdownDisabled(DropdownLink("Link 3", Url("#")))
    ))
    val dropdown = UdashDropdown(elements)("Test")
    val el = dropdown.render
    jQ("body").append(el)

    "call listeners on opening and closing" in {
      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      dropdown.listen {
        case DropdownShowEvent(_) => showCounter += 1
        case DropdownShownEvent(_) => shownCounter += 1
        case DropdownHideEvent(_) => hideCounter += 1
        case DropdownHiddenEvent(_) => hiddenCounter += 1
      }

      for (i <- 1 to 10) {
        el.childNodes(0).asInstanceOf[Button].click()
        showCounter should be(i)
        shownCounter should be(i)
        hideCounter should be(i-1)
        hiddenCounter should be(i-1)
        el.childNodes(0).asInstanceOf[Button].click()
        showCounter should be(i)
        shownCounter should be(i)
        hideCounter should be(i)
        hiddenCounter should be(i)
      }
    }

    "call listeners on element click" in {
      var elClickCounter = 0
      dropdown.listen { case _ => elClickCounter += 1 }

      for (i <- 1 to 5) {
        el.childNodes(1).childNodes(i-1).asInstanceOf[Button].click()
        elClickCounter should be(i)
      }
    }

    "update dropdown elements on property change" in {
      el.childNodes(1).childNodes.length should be(elements.get.length)

      val tmp: DropdownLink = DropdownLink("New", Url("#"))
      elements.append(tmp)
      el.childNodes(1).childNodes.length should be(elements.get.length)
      elements.remove(tmp)
      el.childNodes(1).childNodes.length should be(elements.get.length)
    }
  }
}
