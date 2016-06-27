package io.udash.bootstrap.collapse

import io.udash.bootstrap.{BootstrapStyles, BootstrapTags, Listenable, ListenableEvent, UdashBootstrap, UdashBootstrapComponent}
import io.udash.wrappers.jquery.JQuery
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom.all._
import scalatags.generic.AttrPair

class UdashCollapse private(parentSelector: Option[String], toggleOnInit: Boolean)(mds: Modifier*)
  extends UdashBootstrapComponent with Listenable[UdashCollapse, UdashCollapse.CollapseEvent] {

  import BootstrapTags._
  import UdashCollapse._
  import io.udash.wrappers.jquery._

  val collapseId = UdashBootstrap.newId()

  def jQSelector(): UdashCollapseJQuery =
    jQ(s"#$collapseId").asCollapse()

  def toggle(): Unit = jQSelector().collapse("toggle")
  def show(): Unit = jQSelector().collapse("show")
  def hide(): Unit = jQSelector().collapse("hide")

  def toggleButtonAttrs(): Seq[AttrPair[Element, String]] = {
    import scalatags.JsDom.all._
    Seq(
      dataToggle := "collapse",
      dataTarget := s"#$collapseId"
    )
  }

  lazy val render: Element = {
    import scalacss.ScalatagsCss._

    val el = div(
      dataParent := parentSelector.getOrElse("false"), dataToggle := toggleOnInit,
      BootstrapStyles.Collapse.collapse, id := collapseId.id
    )(mds).render

    val jQEl = jQ(el)
    jQEl.on("show.bs.collapse", jQFire(CollapseShowEvent(this)))
    jQEl.on("shown.bs.collapse", jQFire(CollapseShownEvent(this)))
    jQEl.on("hide.bs.collapse", jQFire(CollapseHideEvent(this)))
    jQEl.on("hidden.bs.collapse", jQFire(CollapseHiddenEvent(this)))
    el
  }
}

object UdashCollapse {

  sealed trait CollapseEvent extends ListenableEvent[UdashCollapse]

  case class CollapseShowEvent(source: UdashCollapse) extends CollapseEvent

  case class CollapseShownEvent(source: UdashCollapse) extends CollapseEvent

  case class CollapseHideEvent(source: UdashCollapse) extends CollapseEvent

  case class CollapseHiddenEvent(source: UdashCollapse) extends CollapseEvent

  def apply(parentSelector: Option[String] = None, toggleOnInit: Boolean = true)(mds: Modifier*): UdashCollapse =
    new UdashCollapse(parentSelector, toggleOnInit)(mds)

  @js.native
  trait UdashCollapseJQuery extends JQuery {
    def collapse(cmd: String): UdashCollapseJQuery = js.native
  }

  implicit class UdashCollapseJQueryExt(jQ: JQuery) {
    def asCollapse(): UdashCollapseJQuery =
      jQ.asInstanceOf[UdashCollapseJQuery]
  }
}
