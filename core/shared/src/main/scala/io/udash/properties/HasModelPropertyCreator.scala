package io.udash.properties

abstract class HasModelPropertyCreator[T](implicit mpc: MacroModelPropertyCreator[T]) {
  /**
    * Use this constructor and pass `ModelPropertyCreator.materialize` explicitly if you're getting the
    * "super constructor cannot be passed a self reference unless parameter is declared by-name" error.
    */
  def this(creator: => ModelPropertyCreator[T]) = this()(MacroModelPropertyCreator(creator))

  implicit val modelPropertyCreator: ModelPropertyCreator[T] = mpc.pc
}