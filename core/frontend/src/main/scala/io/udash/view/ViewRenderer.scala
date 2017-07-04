package io.udash.view

import io.udash.core.{ContainerView, View}
import io.udash.utils.FilteringUtils._
import org.scalajs.dom.Element

import scala.collection.mutable

/**
  * ViewRenderer is used to provide mechanism to render nested [[View]] within provided [[rootElement]].
  */
private[udash] class ViewRenderer(rootElement: => Element) {
  private lazy val endpoint = rootElement
  private val views = mutable.ArrayBuffer[View]()

  private def renderChild(parent: View, child: Option[View]): Unit =
    parent match {
      case p: ContainerView =>
        p.renderChild(child)
      case _ =>
        throw new RuntimeException("Final view cannot render child view! Check your states hierarchy.")
    }

  private def mergeViews(path: List[View]): Option[View] = {
    if (path.size == 1) {
      val singleView: View = path.head
      views.append(singleView)
      Some(singleView)
    } else {
      val lastElement = path.reduceLeft[View]((parent, child) => {
        renderChild(parent, Some(child))
        views.append(parent)
        child
      })
      views.append(lastElement)
      path.headOption
    }
  }

  private def replaceCurrentViews(path: List[View]) = {
    val rootView = mergeViews(path)

    views.clear()
    views.appendAll(path)

    // Clear root element
    for (_ <- 0 until endpoint.childElementCount)
      endpoint.removeChild(endpoint.childNodes(0))

    rootView.foreach(_.getTemplate.applyTo(endpoint))
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
      require(pathToAdd.nonEmpty, "You cannot remove all views, without adding any new view.")
      replaceCurrentViews(pathToAdd)
    } else {
      views.trimEnd(views.size - currentViewsToLeave.size)
      val rootView = currentViewsToLeave.last
      val rootViewToAttach = if (pathToAdd.nonEmpty) mergeViews(pathToAdd) else None
      renderChild(rootView, rootViewToAttach)
    }
  }
}
