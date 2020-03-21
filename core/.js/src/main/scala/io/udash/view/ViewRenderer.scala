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
      case rest =>
        throw new RuntimeException(s"Only instances of ContainerView can render a child view! Check the states hierarchy of view $rest.")
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

  private def replaceCurrentViews(path: List[View]): Unit = {
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
    val currentViewsToLeave = findEqPrefix(subPathToLeave.iterator, views.iterator)

    if (currentViewsToLeave.isEmpty) {
      require(pathToAdd.nonEmpty, "You cannot remove all views, without adding any new view.")
      replaceCurrentViews(pathToAdd)
    } else {
      val removedViews = views.size - currentViewsToLeave.size
      views.trimEnd(removedViews)
      val rootView = currentViewsToLeave.last
      val rootViewToAttach = if (pathToAdd.nonEmpty) mergeViews(pathToAdd) else None
      if (removedViews > 0 || rootViewToAttach.isDefined) {
        renderChild(rootView, rootViewToAttach)
      }
    }
  }
}
