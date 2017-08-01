package io.udash.core

import org.scalajs.dom._

import scalatags.generic.Modifier

/**
  * Url wrapper - just for avoiding strings.
  */
case class Url(value: String) extends AnyVal

/**
  * The Presenter should contain all business logic of a view: user interaction callbacks, server communication.
  * It should not call any methods of a View class. The View and the Presenter should communicate via Model properties.
  * When implementing Presenter, you should remember, that a handleState method can be called not only on view initialization.
  * @tparam S State for which this presenter is defined.
  */
trait Presenter[S <: State] {
  /**
    * This method will be called by [[io.udash.routing.RoutingEngine]] when relevant state need to be resolved.
    * It can be uses to get parameters from state and use it to call eg. external API.
    * @param state the instance of resolved state
    */
  def handleState(state: S): Unit

  /**
    * This method will be called by [[io.udash.routing.RoutingEngine]] when this presenter will be replaced
    * by other presenter. In this callback you should do cleanup if needed.
    */
  def onClose(): Unit = ()
}

/**
  * The ViewPresenter has to prepare model, [[io.udash.core.View]], [[io.udash.core.Presenter]] and link them together.
  * @tparam S State for which this pair is defined.
  */
trait ViewPresenter[S <: State] {
  /**
    * Factory method which should return ready to used instance of [[io.udash.core.Presenter]] and [[io.udash.core.View]].
    * @return pair of presenter and view for state S
    */
  def create(): (View, Presenter[S])
}

/**
  * Abstract view which should be used in order to implement View for [[io.udash.core.ViewPresenter]].
  * The View implementation usually gets the model and the [[io.udash.core.Presenter]] as constructor arguments.
  */
trait View {
  /**
    * Will be invoked by [[io.udash.routing.RoutingEngine]] in order to render the child view inside
    * the parent view. <br/><br/>
    *
    * <b>This method can receive `None` as "view" argument, then previous child view should be removed.</b>
    *
    * @param view view which origins from child
    */
  def renderChild(view: Option[View]): Unit

  /**
    * Implementation of this method should return DOM representation of view.
    * @return DOM representation of view
    */
  def getTemplate: Modifier[Element]

  def apply() = getTemplate
}

/** A [[io.udash.core.View]] which does not have any child view. */
trait FinalView extends View {
  override def renderChild(view: Option[View]): Unit =
    view.foreach(_ => throw View.UnexpectedChildView)
}

object View {
  case object UnexpectedChildView extends RuntimeException("This view is not expected to render child view. Did you forget to implement renderChild method?")
}

/**
  * The class which should be used to present the state for [[io.udash.routing.RoutingEngine]].
  */
trait State {
  def parentState: State
}

/**
  * The implementation of this trait should be injected to [[io.udash.routing.RoutingEngine]].
  * It should implement a bidirectional mapping between [[io.udash.core.Url]] and [[io.udash.core.State]].
  * @tparam S
  */
trait RoutingRegistry[S <: State] {
  def matchUrl(url: Url): S

  def matchState(state: S): Url
}

/**
  * The implementation of this trait should be injected to [[io.udash.routing.RoutingEngine]].
  * It is used to map [[State]] to [[ViewPresenter]].
  * @tparam S
  */
trait ViewPresenterRegistry[S <: State] {
  def matchStateToResolver(state: S): ViewPresenter[_ <: S]
}