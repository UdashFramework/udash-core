package io.udash.bootstrap
package collapse

import com.avsystem.commons.misc.AbstractCase
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.PropertyCreator
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom.all._
import scalatags.generic.AttrPair

final class UdashCollapse private(parentSelector: Option[String], toggleOnInit: Boolean,
                                  override val componentId: ComponentId)(content: Modifier*)
  extends UdashBootstrapComponent with Listenable[UdashCollapse, UdashCollapse.CollapseEvent] {

  import BootstrapTags._
  import UdashCollapse._
  import io.udash.css.CssView._
  import io.udash.wrappers.jquery._

  /** Toggle state of this collapse. */
  def toggle(): Unit = jQSelector().collapse("toggle")

  /** Shows this collapse. */
  def show(): Unit = jQSelector().collapse("show")

  /** Hides this collapse. */
  def hide(): Unit = jQSelector().collapse("hide")

  /** Attributes which should be added to the button toggling this collapse component.
    * Example: `UdashButton()(collapse.toggleButtonAttrs(), "Toggle...")`*/
  def toggleButtonAttrs(): Seq[AttrPair[Element, String]] = {
    import scalatags.JsDom.all._
    Seq(
      dataToggle := "collapse",
      dataTarget := s"#$componentId"
    )
  }

  override val render: Element = {
    val el = div(
      dataParent := parentSelector.getOrElse("false"), dataToggle := toggleOnInit,
      BootstrapStyles.Collapse.collapse, id := componentId
    )(content).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.collapse", jQFire(CollapseShowEvent(this)))
    jQEl.on("shown.bs.collapse", jQFire(CollapseShownEvent(this)))
    jQEl.on("hide.bs.collapse", jQFire(CollapseHideEvent(this)))
    jQEl.on("hidden.bs.collapse", jQFire(CollapseHiddenEvent(this)))
    el
  }

  private def jQSelector(): UdashCollapseJQuery =
    jQ(s"#$componentId").asInstanceOf[UdashCollapseJQuery]
}

object UdashCollapse {
  sealed trait CollapseEvent extends AbstractCase with ListenableEvent[UdashCollapse]
  final case class CollapseShowEvent(source: UdashCollapse) extends CollapseEvent
  final case class CollapseShownEvent(source: UdashCollapse) extends CollapseEvent
  final case class CollapseHideEvent(source: UdashCollapse) extends CollapseEvent
  final case class CollapseHiddenEvent(source: UdashCollapse) extends CollapseEvent

  object CollapseEvent {
    implicit val pc: PropertyCreator[CollapseEvent] = PropertyCreator.propertyCreator[CollapseEvent]
    implicit val pcO: PropertyCreator[Option[CollapseEvent]] = PropertyCreator.propertyCreator[Option[CollapseEvent]]
    implicit val pcS: PropertyCreator[Seq[CollapseEvent]] = PropertyCreator.propertyCreator[Seq[CollapseEvent]]
  }

  /**
    * Creates collapsible component.
    * More: <a href="http://getbootstrap.com/javascript/#collapse">Bootstrap Docs</a>.
    *
    * @param parentSelector If a selector is provided, all collapsible elements under the specified parent will be closed when this collapsible item is shown.
    * @param toggleOnInit   Toggles the collapsible element on invocation.
    * @param componentId    Id of the root DOM node.
    * @param content        Collapse component content
    * @return `UdashCollapse` component, call render to create DOM element.
    */
  def apply(parentSelector: Option[String] = None, toggleOnInit: Boolean = true,
            componentId: ComponentId = UdashBootstrap.newId())(content: Modifier*): UdashCollapse =
    new UdashCollapse(parentSelector, toggleOnInit, componentId)(content)

  @js.native
  private trait UdashCollapseJQuery extends JQuery {
    def collapse(cmd: String): UdashCollapseJQuery = js.native
  }
}
