package io.udash.web.guide.views.ext.demo

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object JQueryCallbacksDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash.bootstrap.button.UdashButton
    import io.udash.wrappers.jquery._
    import scalatags.JsDom.all._

    import scala.scalajs.js

    val callbacks = jQ.callbacks[js.Function1[(Int, Int), js.Any], (Int, Int)]()
    callbacks.add((t: (Int, Int)) => {
      val (a, b) = t
      jQ("#jquery-callbacks-demo #plus").append(li(s"$a + $b = ${a + b}").render)
    })
    callbacks.add((t: (Int, Int)) => {
      val (a, b) = t
      jQ("#jquery-callbacks-demo #minus").append(li(s"$a - $b = ${a - b}").render)
    })
    callbacks.add((t: (Int, Int)) => {
      val (a, b) = t
      jQ("#jquery-callbacks-demo #mul").append(li(s"$a * $b = ${a * b}").render)
    })
    callbacks.add((t: (Int, Int)) => {
      val (a, b) = t
      jQ("#jquery-callbacks-demo #div").append(li(s"$a / $b = ${a / b}").render)
    })

    div(
      "Plus:",
      ul(id := "plus"),
      "Minus:",
      ul(id := "minus"),
      "Multiply:",
      ul(id := "mul"),
      "Divide:",
      ul(id := "div"),
      br,
      UdashButton()(_ => Seq[Modifier](
        id := "fire", "Fire",
        onclick := (() => {
          callbacks.fire((1, 1))
          callbacks.fire((3, 3))
          callbacks.fire((7, 4))
          callbacks.disable()
          callbacks.fire((1, 2))
        }))
      ).render
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (
      div(
        id := "jquery-callbacks-demo",
        GuideStyles.frame,
        GuideStyles.useBootstrap
      )(rendered),
      source
    )
}