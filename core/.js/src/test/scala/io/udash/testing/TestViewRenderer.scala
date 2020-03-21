package io.udash.testing

import com.avsystem.commons._
import io.udash._

class TestViewRenderer extends ViewRenderer(null) {
  val views = MArrayBuffer[View]()
  var lastSubPathToLeave: List[View] = Nil
  var lastPathToAdd: Iterable[View] = Nil

  override def renderView(subPathToLeave: Iterator[View], pathToAdd: Iterable[View]): Unit = {
    val subPathList = subPathToLeave.toList
    views.clear()
    views.appendAll(subPathList)
    views.appendAll(pathToAdd)

    lastSubPathToLeave = subPathList
    lastPathToAdd = pathToAdd
  }
}
