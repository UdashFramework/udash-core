package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.web.guide.Context
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Event

import scala.language.postfixOps

object JQueryEventsDemo {
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._
  import Context._

  val onCallback = (_: dom.Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added on every click").render)
  val oneCallback = (_: dom.Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added only once").render)

  def apply(): dom.Element = {
    val content = div(id := "jquery-events-demo", GuideStyles.get.frame, GuideStyles.get.useBootstrap)(
      ul(),
      br,
      UdashButtonGroup()(
        UdashButton(componentId = ComponentId("click"))("Click me").render,
        UdashButton(componentId = ComponentId("off"))(
          onclick :+= ((_: Event) => {
            jQ("#jquery-events-demo #click")
              .off("click", onCallback)
              .off("click", oneCallback)
            false
          }), "Off"
        ).render
      ).render
    ).render

    jQ(content).find("#click")
      .on("click", onCallback)
      .one("click", oneCallback)

    content
  }
}