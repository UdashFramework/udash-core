package io.udash.selenium.views.demos.jquery

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Event

class JQueryEventsDemo {
  import scalatags.JsDom.all._

  val onCallback = (_: dom.Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added on every click").render)
  val oneCallback = (_: dom.Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added only once").render)

  def getTemplate: dom.Element = {
    val content = div(id := "jquery-events-demo")(
      ul(),
      br,
      UdashButtonGroup()(
        UdashButton(componentId = ComponentId("click"))("Click me").render,
        UdashButton(componentId = ComponentId("off"))(
          onclick :+= ((_: Event) =>
            jQ("#jquery-events-demo #click")
              .off("click", onCallback)
              .off("click", oneCallback)
          ), "Off"
        ).render
      ).render
    ).render

    jQ(content).find("#click")
      .on("click", onCallback)
      .one("click", oneCallback)

    content
  }
}