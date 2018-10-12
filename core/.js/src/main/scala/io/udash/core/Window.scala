package io.udash.core

import org.scalajs.dom.{Element, Event}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope

@js.native
@JSGlobalScope
@deprecated("Use `org.scalajs.dom.window` instead.", "0.7.0")
object DomWindow extends Element

@deprecated("The application should not directly depend on URL fragment.", "0.7.0")
object Window {
  private var onHashChangedCallbacks: List[() => Any] = List()

  DomWindow.addEventListener("hashchange", (event: Event) => onHashChangedCallbacks.foreach(_.apply()))

  def onFragmentChange(onHashChange: () => Any) = onHashChangedCallbacks = onHashChange :: onHashChangedCallbacks
}

