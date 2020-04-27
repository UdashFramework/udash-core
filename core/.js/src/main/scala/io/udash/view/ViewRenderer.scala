package io.udash.view

import com.avsystem.commons._
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

  private def mergeViews(path: Iterator[View]): Option[View] =
    path.nextOpt.setup(_.foreach { top =>
      val lastElement = path.fold(top) { case (parent, child) =>
        renderChild(parent, Some(child))
        views.append(parent)
        child
      }
      views.append(lastElement)
    }).toOption

  private def replaceCurrentViews(path: Iterable[View]): Unit = {
    val rootView = mergeViews(path.iterator)

    views.clear()
    views.appendAll(path)

    // Clear root element
    while (endpoint.firstChild != null) endpoint.removeChild(endpoint.firstChild)

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
   * @param pathToAdd      views list, which will be added to hierarchy
   */
  def renderView(subPathToLeave: Iterator[View], pathToAdd: Iterable[View]): Unit = {
    val currentViewsToLeaveSize = findEqPrefix(subPathToLeave, views.iterator).size

    if (currentViewsToLeaveSize == 0) {
      require(pathToAdd.nonEmpty, "You cannot remove all views, without adding any new view.")
      replaceCurrentViews(pathToAdd)
    } else {
      val removedViews = views.size - currentViewsToLeaveSize
      views.trimEnd(removedViews)
      val rootView = views.last
      val rootViewToAttach = mergeViews(pathToAdd.iterator)
      if (removedViews > 0 || rootViewToAttach.isDefined) {
        renderChild(rootView, rootViewToAttach)
      }
    }
  }
}
