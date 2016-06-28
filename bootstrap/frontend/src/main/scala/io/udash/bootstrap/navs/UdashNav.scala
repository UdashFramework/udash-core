package io.udash.bootstrap
package navs

import io.udash.properties.SeqProperty
import io.udash.{properties, _}
import org.scalajs.dom
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalacss._
import scalatags.JsDom.all._

class UdashNav[ItemType, ElemType <: Property[ItemType]] private
              (navStyle: StyleA, stacked: Boolean, justified: Boolean)
              (panels: properties.SeqProperty[ItemType, ElemType])
              (elemFactory: (ElemType) => dom.Element,
               isActive: (ElemType) => ReadableProperty[Boolean],
               isDisabled: (ElemType) => ReadableProperty[Boolean],
               isDropdown: (ElemType) => ReadableProperty[Boolean])
  extends UdashBootstrapComponent {

  lazy val render: Element =
    ul(
      BootstrapStyles.Navigation.nav, navStyle,
      BootstrapStyles.Navigation.navJustified.styleIf(justified),
      BootstrapStyles.Navigation.navStacked.styleIf(stacked)
    )(
      repeat(panels)(panel => {
        li(role := "presentation")(
          BootstrapStyles.active.styleIf(isActive(panel)),
          BootstrapStyles.disabled.styleIf(isDisabled(panel)),
          BootstrapStyles.Dropdown.dropdown.styleIf(isDropdown(panel))
        )(elemFactory(panel)).render
      })
    ).render
}

object UdashNav {
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def apply[ItemType, ElemType <: Property[ItemType]]
           (stacked: Boolean = false, justified: Boolean = false)
           (panels: SeqProperty[ItemType, ElemType])
           (elemFactory: (ElemType) => Element,
            isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    pills(stacked, justified)(panels)(elemFactory, isActive, isDisabled, isDropdown)

  def pills[ItemType, ElemType <: Property[ItemType]]
           (stacked: Boolean = false, justified: Boolean = false)
           (panels: SeqProperty[ItemType, ElemType])
           (elemFactory: (ElemType) => Element,
            isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
            isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    new UdashNav(BootstrapStyles.Navigation.navPills, stacked, justified)(panels)(elemFactory, isActive, isDisabled, isDropdown)

  def tabs[ItemType, ElemType <: Property[ItemType]]
          (stacked: Boolean = false, justified: Boolean = false)
          (panels: SeqProperty[ItemType, ElemType])
          (elemFactory: (ElemType) => Element,
           isActive: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
           isDisabled: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false),
           isDropdown: (ElemType) => ReadableProperty[Boolean] = (_: ElemType) => Property(false)): UdashNav[ItemType, ElemType] =
    new UdashNav(BootstrapStyles.Navigation.navTabs, stacked, justified)(panels)(elemFactory, isActive, isDisabled, isDropdown)
}