package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.Event
import scalatags.JsDom

object JQueryEventsDemo extends AutoDemo with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val onCallback = (_: dom.Element, _: JQueryEvent) =>
      jQ("#jquery-events-demo ul").append(li("This will be added on every click").render)
    val oneCallback = (_: dom.Element, _: JQueryEvent) =>
      jQ("#jquery-events-demo ul").append(li("This will be added only once").render)

    val content = div(
      ul(),
      br,
      UdashButtonGroup()(
        UdashButton(componentId = ComponentId("click"))(_ => "Click me").render,
        UdashButton(componentId = ComponentId("off"))(_ => Seq[Modifier](
          onclick :+= ((_: Event) =>
            jQ("#jquery-events-demo #click")
              .off("click", onCallback)
              .off("click", oneCallback)
            ),
          "Off"
        )).render
      ).render
    ).render

    jQ(content).find("#click")
      .on("click", onCallback)
      .one("click", oneCallback)

    content
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "jquery-events-demo", GuideStyles.frame, GuideStyles.useBootstrap)(rendered), source.lines.drop(1))
  }
}