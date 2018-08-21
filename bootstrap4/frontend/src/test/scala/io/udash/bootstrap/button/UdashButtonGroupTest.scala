package io.udash.bootstrap.button

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashFrontendTest
import io.udash.wrappers.jquery._

class UdashButtonGroupTest extends UdashFrontendTest {
  import scalatags.JsDom.all._

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
      val buttons = UdashButtonGroup.reactive(labels)((label, nested) => UdashButton()(nested(bind(label))).render)

      val el = buttons.render
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")

      labels.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")

      buttons.kill()
      labels.listenersCount() should be(0)
      labels.structureListenersCount() should be(0)
      labels.elemProperties.foreach(_.listenersCount() should be(0))
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
      buttons.listenersCount() should be(0)
      buttons.structureListenersCount() should be(0)
      buttons.elemProperties.foreach(_.listenersCount() should be(0))
      selected.listenersCount() should be(0)
      selected.structureListenersCount() should be(0)
      selected.elemProperties.foreach(_.listenersCount() should be(0))
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
      buttons.listenersCount() should be(0)
      buttons.structureListenersCount() should be(0)
      buttons.elemProperties.foreach(_.listenersCount() should be(0))
      selected.listenersCount() should be(0)
    }
  }
}
