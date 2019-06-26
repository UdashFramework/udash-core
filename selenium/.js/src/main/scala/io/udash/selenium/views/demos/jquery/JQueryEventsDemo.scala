package io.udash.selenium.views.demos.jquery

import io.udash._
import io.udash.bootstrap._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.wrappers.jquery._
import org.scalajs.dom.{Element, Event}

class JQueryEventsDemo {
  import scalatags.JsDom.all._

  private val onCallback = (_: Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added on every click").render)
  private val oneCallback = (_: Element, _: JQueryEvent) =>
    jQ("#jquery-events-demo ul").append(li("This will be added only once").render)

  def getTemplate: Element = {
    val content = div(id := "jquery-events-demo")(
      ul(),
      br,
      UdashButtonGroup()(
        UdashButton(componentId = ComponentId("click"))(_ => "Click me").render,
        UdashButton(componentId = ComponentId("off"))(_ => Seq[Modifier](
          onclick :+= ((_: Event) =>
            jQ("#jquery-events-demo #click")
              .off("click", onCallback)
              .off("click", oneCallback)
          ), "Off"
        )).render
      ).render
    ).render

    jQ(content).find("#click")
      .on("click", onCallback)
      .one("click", oneCallback)

    content
  }
}