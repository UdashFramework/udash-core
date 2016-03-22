package io.udash.core

import org.scalajs.dom.{Element, Event}

import scala.scalajs.js

@js.native
object DomWindow extends Element with js.GlobalScope

object Window {
  private var onHashChangedCallbacks: List[() => Any] = List()

  DomWindow.addEventListener("hashchange", (event: Event) => onHashChangedCallbacks.foreach(_.apply()))

  def onFragmentChange(onHashChange: () => Any) = onHashChangedCallbacks = onHashChange :: onHashChangedCallbacks
}

