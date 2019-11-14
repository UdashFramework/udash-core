package io.udash.web.guide.views.ext.demo

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object JQueryEventsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import io.udash.bootstrap.button._
    import io.udash.wrappers.jquery._
    import org.scalajs.dom._
    import scalatags.JsDom.all._

    val onCallback = (_: Element, _: JQueryEvent) =>
      jQ("#jquery-events-demo ul").append(li("This will be added on every click").render)
    val oneCallback = (_: Element, _: JQueryEvent) =>
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

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (
      div(
        id := "jquery-events-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source.linesIterator
    )
  }
}