package io.udash.core

import io.udash.properties.HasModelPropertyCreator
import org.scalajs.dom._
import scalatags.generic.Modifier

/**
  * Url wrapper - just for avoiding strings.
  */
case class Url(value: String) extends AnyVal
object Url extends HasModelPropertyCreator[Url]

/**
  * The Presenter should contain all business logic of a view: user interaction callbacks, server communication.
  * It should not call any methods of a View class. The View and the Presenter should communicate via Model properties.
  * When implementing Presenter, you should remember, that a handleState method can be called not only on view initialization.
  *
  * @tparam S State for which this presenter is defined.
  */
trait Presenter[-S <: State] {
  /**
    * This method will be called by [[io.udash.routing.RoutingEngine]] when relevant state need to be resolved.
    * It can be uses to get parameters from state and use it to call eg. external API.
    *
    * @param state the instance of resolved state
    */
  def handleState(state: S): Unit

  /**
    * This method will be called by [[io.udash.routing.RoutingEngine]] when this presenter is replaced
    * by another one. This is where you can do cleanup if needed.
    */
  def onClose(): Unit = ()
}

/**
  * The ViewFactory has to prepare model, [[io.udash.core.View]], [[io.udash.core.Presenter]] and link them together.
  *
  * @tparam S State for which this pair is defined.
  */
trait ViewFactory[S <: State] {
  /**
    * Factory method which should return ready to used instance of [[io.udash.core.Presenter]] and [[io.udash.core.View]].
    *
    * @return pair of presenter and view for state S
    */
  def create(): (View, Presenter[S])
}

/**
  * Abstract view which should be used in order to implement View for [[io.udash.core.ViewFactory]].
  * The View implementation usually gets the model and the [[io.udash.core.Presenter]] as constructor arguments.
  */
trait View {
  /**
    * Implementation of this method should return DOM representation of view.
    *
    * @return DOM representation of view
    */
  def getTemplate: Modifier[Element]

  /**
   * This method will be called by [[io.udash.routing.RoutingEngine]] when this view is replaced
   * by another one. This is where you can do cleanup.
   */
  def onClose(): Unit = ()
}

/** A [[io.udash.core.View]] which can render child view. */
trait ContainerView extends View {

  import scalatags.JsDom.all.div

  /** Default implementation renders child views inside this element. */
  protected val childViewContainer: Element = div().render

  /**
    * Will be invoked by [[io.udash.routing.RoutingEngine]] in order to render the child view inside
    * the parent view. <br/><br/>
    *
    * <b>This method can receive `None` as "view" argument, then previous child view should be removed.</b>
    *
    * The default implementation removes everything from `childViewContainer` and renders new subview inside.
    *
    * @param view view which origins from child
    */
  def renderChild(view: Option[View]): Unit = {
    while (childViewContainer.childElementCount > 0) {
      childViewContainer.removeChild(childViewContainer.firstChild)
    }
    view.foreach(_.getTemplate.applyTo(childViewContainer))
  }
}

/** The class which should be used to present the state for [[io.udash.routing.RoutingEngine]]. */
trait State {
  type HierarchyRoot <: State {type HierarchyRoot = State.this.HierarchyRoot}
  def parentState: Option[HierarchyRoot]
}

/**
  * The implementation of this trait should be injected to [[io.udash.routing.RoutingEngine]].
  * It is used to map [[State]] to [[ViewFactory]].
  */
trait ViewFactoryRegistry[HierarchyRoot <: State] {
  def matchStateToResolver(state: HierarchyRoot): ViewFactory[_ <: HierarchyRoot]
}
