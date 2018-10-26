package io.udash.bootstrap.button

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import io.udash.wrappers.jquery._

class UdashButtonGroupTest extends UdashCoreFrontendTest {

  "UdashButtonGroup component" should {
    "render static buttons group" in {
      val buttons = UdashButtonGroup()(
        UdashButton()("Button 1").render,
        UdashButton()("Button 2").render,
        UdashButton()("Button 3").render
      )

      val el = buttons.render
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
    }

    "render reactive buttons group" in {
      val labels = SeqProperty(Seq("Button 1", "Button 2", "Button 3"))
      val buttons = UdashButtonGroup.reactive(labels) { (label, nested) =>
        val btn = UdashButton()(n => n(bind(label)))
        nested(btn)
        btn.render
      }

      val el = buttons.render
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")

      labels.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")

      buttons.kill()
      ensureNoListeners(labels)
    }

    "render checkbox buttons group" in {
      val buttons = SeqProperty("Button 1", "Button 2", "Button 3")
      val selected = SeqProperty.blank[String]
      val group = UdashButtonGroup.checkboxes(selected, buttons)()

      val el = group.render
      val children: JQuery = jQ(el).children()
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass(BootstrapStyles.active.className) should be(false)
      children.at(1).hasClass(BootstrapStyles.active.className) should be(false)
      children.at(2).hasClass(BootstrapStyles.active.className) should be(false)

      selected.append("Button 1")
      selected.append("Button 2")

      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass(BootstrapStyles.active.className) should be(true)
      children.at(1).hasClass(BootstrapStyles.active.className) should be(true)
      children.at(2).hasClass(BootstrapStyles.active.className) should be(false)

      buttons.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      val children4: JQuery = jQ(el).children()
      children4.at(0).hasClass(BootstrapStyles.active.className) should be(true)
      children4.at(1).hasClass(BootstrapStyles.active.className) should be(true)
      children4.at(2).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(3).hasClass(BootstrapStyles.active.className) should be(false)

      selected.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      children4.at(0).hasClass(BootstrapStyles.active.className) should be(true)
      children4.at(1).hasClass(BootstrapStyles.active.className) should be(true)
      children4.at(2).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(3).hasClass(BootstrapStyles.active.className) should be(true)

      group.kill()
      ensureNoListeners(buttons)
      ensureNoListeners(selected)
    }

    "render radio buttons group" in {
      val buttons = SeqProperty("Button 1", "Button 2", "Button 3")
      val selected = Property("")
      val group = UdashButtonGroup.radio(selected, buttons)()

      val el = group.render
      val children: JQuery = jQ(el).children()
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass(BootstrapStyles.active.className) should be(false)
      children.at(1).hasClass(BootstrapStyles.active.className) should be(false)
      children.at(2).hasClass(BootstrapStyles.active.className) should be(false)

      selected.set("Button 1")

      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass(BootstrapStyles.active.className) should be(true)
      children.at(1).hasClass(BootstrapStyles.active.className) should be(false)
      children.at(2).hasClass(BootstrapStyles.active.className) should be(false)

      buttons.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      val children4: JQuery = jQ(el).children()
      children4.at(0).hasClass(BootstrapStyles.active.className) should be(true)
      children4.at(1).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(2).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(3).hasClass(BootstrapStyles.active.className) should be(false)

      selected.set("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      children4.at(0).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(1).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(2).hasClass(BootstrapStyles.active.className) should be(false)
      children4.at(3).hasClass(BootstrapStyles.active.className) should be(true)

      group.kill()
      ensureNoListeners(buttons)
      selected.listenersCount() should be(0)
    }
  }
}
