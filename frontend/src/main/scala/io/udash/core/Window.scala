package io.udash.core

import io.udash.wrappers.jquery.JQuery._
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js

@js.native
object DomWindow extends js.GlobalScope

object Window {
  def resize = jQ(DomWindow).resize()

  jQ(DomWindow).resize((element: Element, _: JQueryEvent) => onResizeCallbacks.foreach(_.apply()))

  jQ(DomWindow).scroll((element: Element, _: JQueryEvent) => onScrollCallbacks.foreach(_.apply()))

  jQ(DomWindow).on("hashchange", (jThis: Element, event: JQueryEvent) => onHashChangedCallbacks.foreach(_.apply()))

  var onResizeCallbacks: List[() => Any] = List()

  var onScrollCallbacks: List[() => Any] = List()

  var onHashChangedCallbacks: List[() => Any] = List()

  def onResize(callback: () => Any) = onResizeCallbacks = callback :: onResizeCallbacks

  def onScroll(onScrollCallback: () => Any) = onScrollCallbacks = onScrollCallback :: onResizeCallbacks

  def onFragmentChange(onHashChange: () => Any) = onHashChangedCallbacks = onHashChange :: onHashChangedCallbacks

  def height = jQ(DomWindow).height()

  def width = jQ(DomWindow).width()
}

