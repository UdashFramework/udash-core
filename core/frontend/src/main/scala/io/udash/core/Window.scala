package io.udash.core

import org.scalajs.dom.{Element, Event}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope

@js.native
@JSGlobalScope
object DomWindow extends Element

object Window {
  private var onHashChangedCallbacks: List[() => Any] = List()

  DomWindow.addEventListener("hashchange", (event: Event) => onHashChangedCallbacks.foreach(_.apply()))

  def onFragmentChange(onHashChange: () => Any) = onHashChangedCallbacks = onHashChange :: onHashChangedCallbacks
}

