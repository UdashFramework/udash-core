package io.udash.view

import io.udash.core.{View, Window}
import io.udash.utils.FilteringUtils._
import org.scalajs.dom.Element

import scala.collection.mutable
import scala.scalajs.js.timers.RawTimers

/**
  * ViewRenderer is used to provide mechanism to render nested [[View]] within provided [[rootElement]].
  */
private[udash] class ViewRenderer(rootElement: Element) {
  private val endpoint = rootElement
  private val views = mutable.ArrayBuffer[View]()

  private def mergeViews(path: List[View]): View = {
    if (path.size == 1) {
      val singleView: View = path.head
      views.append(singleView)
      singleView
    } else {
      path.reduceLeft[View]((parent, child) => {
        parent.renderChild(child)
        views.append(parent)
        child
      })
      path.head
    }
  }

  private def replaceCurrentViews(path: List[View]) = {
    val rootView = mergeViews(path)
    views.clear()
    views.appendAll(path)
    val child: Element = rootView.getTemplate
    if (endpoint.hasChildNodes()) endpoint.replaceChild(child, endpoint.lastChild)
    else endpoint.appendChild(child)
  }

  /**
    * Updates views hierarchy.
    * <br/><br/>
    * Example: <br/>
    * Current views: A -> B -> C -> D <br/>
    * subPathToLeave: A -> B <br/>
    * pathToAdd: E -> F <br/>
    * <br/>
    * Calls:<br/>
    * A - nothing<br/>
    * B - renderChild(E)<br/>
    * E - getTemplate(); renderChild(F)<br/>
    * F - getTemplate()<br/>
    *
    * @param subPathToLeave prefix of views hierarchy, which will not be removed
    * @param pathToAdd views list, which will be added to hierarchy
    */
  def renderView(subPathToLeave: List[View], pathToAdd: List[View]): Unit = {
    val currentViewsToLeave = findEqPrefix(subPathToLeave, views.toList)

    if (currentViewsToLeave.isEmpty) {
      require(pathToAdd.nonEmpty, "You can not remove all views, without adding any new view.")
      replaceCurrentViews(pathToAdd)
    } else {
      views.trimEnd(views.size - currentViewsToLeave.size)
      val rootView = currentViewsToLeave.last
      val rootViewToAttach = if (pathToAdd.nonEmpty) mergeViews(pathToAdd) else null

      rootView.renderChild(rootViewToAttach)
    }
  }
}
