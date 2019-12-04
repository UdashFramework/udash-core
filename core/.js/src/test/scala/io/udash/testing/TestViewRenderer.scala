package io.udash.testing

import io.udash._

import scala.collection.mutable

class TestViewRenderer extends ViewRenderer(null) {
  val views = mutable.ArrayBuffer[View]()
  var lastSubPathToLeave: List[View] = Nil
  var lastPathToAdd: List[View] = Nil

  override def renderView(subPathToLeave: List[View], pathToAdd: List[View]): Unit = {
    views.clear()
    views.appendAll(subPathToLeave)
    views.appendAll(pathToAdd)

    lastSubPathToLeave = subPathToLeave
    lastPathToAdd = pathToAdd
  }
}
