package io.udash.bootstrap.dropdown

import io.udash._
import io.udash.bootstrap.dropdown.UdashDropdown.DropdownEvent.SelectionEvent
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.html.Button

class UdashDropdownTest extends UdashFrontendTest {
  import UdashDropdown._

  import scalatags.JsDom.all._

  private val elements: Seq[DefaultDropdownItem] =Seq(
    DefaultDropdownItem.Header("Header"),
    DefaultDropdownItem.Link("Link 1", Url("#")),
    DefaultDropdownItem.Link("Link 2", Url("#")),
    DefaultDropdownItem.Divider,
    DefaultDropdownItem.Disabled(DefaultDropdownItem.Link("Link 3", Url("#")))
  )

  "UdashDropdown component" should {
    "call listeners on opening and closing" in {
      import DropdownEvent._

      val dropdown = UdashDropdown.default(SeqProperty(elements))("Test")
      val el = dropdown.render
      jQ("body").append(el)

      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      dropdown.listen {
        case VisibilityChangeEvent(_, EventType.Show) => showCounter += 1
        case VisibilityChangeEvent(_, EventType.Shown) => shownCounter += 1
        case VisibilityChangeEvent(_, EventType.Hide) => hideCounter += 1
        case VisibilityChangeEvent(_, EventType.Hidden) => hiddenCounter += 1
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

    "call listeners on toggle call" in {
      import DropdownEvent._

      val dropdown = UdashDropdown.default(SeqProperty(elements))("Test")
      val el = dropdown.render
      jQ("body").append(el)


      var showCounter = 0
      var shownCounter = 0
      var hideCounter = 0
      var hiddenCounter = 0
      dropdown.listen {
        case VisibilityChangeEvent(_, EventType.Show) => showCounter += 1
        case VisibilityChangeEvent(_, EventType.Shown) => shownCounter += 1
        case VisibilityChangeEvent(_, EventType.Hide) => hideCounter += 1
        case VisibilityChangeEvent(_, EventType.Hidden) => hiddenCounter += 1
      }

      for (i <- 1 to 10) {
        dropdown.toggle()
        showCounter should be(i)
        shownCounter should be(i)
        hideCounter should be(i-1)
        hiddenCounter should be(i-1)
        dropdown.toggle()
        showCounter should be(i)
        shownCounter should be(i)
        hideCounter should be(i)
        hiddenCounter should be(i)
      }
    }

    "call listeners on element click" in {
      val els = SeqProperty(elements)
      val dropdown = UdashDropdown(els)(UdashDropdown.defaultItemFactory, "Test")
      val el = dropdown.render
      jQ("body").append(el)


      var elClickCounter = 0
      var selectedItem: DefaultDropdownItem = DefaultDropdownItem.Divider
      dropdown.listen { case ev: SelectionEvent[_, _] =>
        elClickCounter += 1
        selectedItem = ev.item
      }

      for (i <- 1 to 5) {
        el.childNodes(1).childNodes(i-1).asInstanceOf[Button].click()
        elClickCounter should be(i)
        els.get.contains(selectedItem) should be(true)
      }

      els.elemProperties(1).set(DefaultDropdownItem.Header("Test Header 123"))

      for (i <- 1 to 5) {
        el.childNodes(1).childNodes(i-1).asInstanceOf[Button].click()
        elClickCounter should be(i + 5)
        els.get.contains(selectedItem) should be(true)
      }
    }

    "update dropdown elements on property change" in {
      val els = SeqProperty(elements)
      val dropdown = UdashDropdown.default(els)("Test")
      val el = dropdown.render
      jQ("body").append(el)


      el.childNodes(1).childNodes.length should be(els.get.length)

      val tmp: DefaultDropdownItem.Link = DefaultDropdownItem.Link("New", Url("#"))
      els.append(tmp)
      el.childNodes(1).childNodes.length should be(els.get.length)
      els.remove(tmp)
      el.childNodes(1).childNodes.length should be(els.get.length)
    }

    "update dropdown element on a single property change" in {
      val els = SeqProperty(elements)
      val dropdown = UdashDropdown.default(els)("Test")
      val el = dropdown.render
      jQ("body").append(el)


      el.childNodes(1).childNodes(1).firstChild.nodeName should be("A")

      val tmp: DefaultDropdownItem = els.elemProperties(1).get
      els.elemProperties(1).set(DefaultDropdownItem.Divider)
      el.childNodes(1).childNodes(1).firstChild.nodeName should be("DIV")
      els.elemProperties(1).set(tmp)
      el.childNodes(1).childNodes(1).firstChild.nodeName should be("A")
    }
  }
}
