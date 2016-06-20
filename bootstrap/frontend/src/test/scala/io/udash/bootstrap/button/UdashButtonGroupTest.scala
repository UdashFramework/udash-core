package io.udash.bootstrap.button

import io.udash._
import io.udash.bootstrap.BootstrapStyles
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
      val buttons = UdashButtonGroup.reactive(labels)(label => UdashButton()(label.get).render)

      val el = buttons.render
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")

      labels.append("Button 4")
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
    }

    "render checkbox buttons group" in {
      import UdashButtonGroup._
      val buttons = SeqProperty[CheckboxModel](Seq(
        DefaultCheckboxModel("Button 1", true),
        DefaultCheckboxModel("Button 2", true),
        DefaultCheckboxModel("Button 3", false)
      ))
      val group = UdashButtonGroup.checkboxes(buttons)(defaultCheckboxFactory)

      val el = group.render
      val children: JQuery = jQ(el).children()
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass("active") should be(true)
      children.at(1).hasClass("active") should be(true)
      children.at(2).hasClass("active") should be(false)

      buttons.append(DefaultCheckboxModel("Button 4", true))
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      val children4: JQuery = jQ(el).children()
      children4.at(0).hasClass("active") should be(true)
      children4.at(1).hasClass("active") should be(true)
      children4.at(2).hasClass("active") should be(false)
      children4.at(3).hasClass("active") should be(true)

      buttons.elemProperties.foreach(item => {
        val i = item.asModel.subProp(_.checked)
        i.set(!i.get)
      })
      children4.at(0).hasClass("active") should be(false)
      children4.at(1).hasClass("active") should be(false)
      children4.at(2).hasClass("active") should be(true)
      children4.at(3).hasClass("active") should be(false)
    }

    "render radio buttons group" in {
      import UdashButtonGroup._
      val buttons = SeqProperty[CheckboxModel](Seq(
        DefaultCheckboxModel("Button 1", true),
        DefaultCheckboxModel("Button 2", true),
        DefaultCheckboxModel("Button 3", false)
      ))
      val group = UdashButtonGroup.radio(buttons)

      val el = group.render
      val children: JQuery = jQ(el).children()
      el.childElementCount should be(3)
      el.textContent should be("Button 1Button 2Button 3")
      children.at(0).hasClass("active") should be(false)
      children.at(1).hasClass("active") should be(true)
      children.at(2).hasClass("active") should be(false)

      buttons.append(DefaultCheckboxModel("Button 4", true))
      el.childElementCount should be(4)
      el.textContent should be("Button 1Button 2Button 3Button 4")
      val children4: JQuery = jQ(el).children()
      children4.at(0).hasClass("active") should be(false)
      children4.at(1).hasClass("active") should be(false)
      children4.at(2).hasClass("active") should be(false)
      children4.at(3).hasClass("active") should be(true)

      val item = buttons.elemProperties(2).asModel.subProp(_.checked)
      item.set(!item.get)
      children4.at(0).hasClass("active") should be(false)
      children4.at(1).hasClass("active") should be(false)
      children4.at(2).hasClass("active") should be(true)
      children4.at(3).hasClass("active") should be(false)
    }
  }
}
