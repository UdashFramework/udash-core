package io.udash.selenium.views.demos.jquery

import io.udash.bootstrap.button.UdashButton
import io.udash.wrappers.jquery._
import org.scalajs.dom

import scala.scalajs.js

class JQueryCallbacksDemo {
  import scalatags.JsDom.all._

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

  def getTemplate: dom.Element = {
    div(id := "jquery-callbacks-demo")(
      "Plus:",
      ul(id := "plus"),
      "Minus:",
      ul(id := "minus"),
      "Multiply:",
      ul(id := "mul"),
      "Divide:",
      ul(id := "div"),
      br,
      UdashButton()(id := "fire", onclick := (() => {
        callbacks.fire((1, 1))
        callbacks.fire((3, 3))
        callbacks.fire((7, 4))
        callbacks.disable()
        callbacks.fire((1, 2))
      }), "Fire").render
    ).render
  }
}