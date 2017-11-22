package io.udash.properties

import scala.language.higherKinds

/**
  * Evidence that type `T` is immutable.
  *
  * It's generated automatically by macros, but you can create it manually to force treating `T` as immutable.
  */
trait ImmutableValue[T]
object ImmutableValue {
  implicit val allowIntTpe: ImmutableValue[Int] = null
  implicit val allowLongTpe: ImmutableValue[Long] = null
  implicit val allowDoubleTpe: ImmutableValue[Double] = null
  implicit val allowFloatTpe: ImmutableValue[Float] = null
  implicit val allowStringTpe: ImmutableValue[String] = null
  implicit val allowCharTpe: ImmutableValue[Char] = null
  implicit val allowBooleanTpe: ImmutableValue[Boolean] = null

  implicit def allowImmutableCollections[T: ImmutableValue, M[_]](implicit ev: M[T] <:< collection.immutable.Traversable[T]): ImmutableValue[M[T]] = null

  implicit def isImmutable[T]: ImmutableValue[T] = macro io.udash.macros.PropertyMacros.reifyImmutableValue[T]
}

/**
  * Evidence that type `T` can be used to create ModelProperty.
  *
  * There are two valid model bases:
  * <ul>
  * <li>trait (not sealed trait) with following restrictions:<ul>
  * <li>it cannot contain vars</li>
  * <li>it can contain implemented vals and defs, but they are not considered as subproperties</li>
  * <li>all abstract vals and defs (without parameters) are considered as subproperties</li>
  * <li>all abstract vals and defs types have to be valid [[io.udash.properties.ModelValue]]</li>
  * </ul></li>
  * <li>case class with following restrictions:<ul>
  * <li>it cannot contain vars</li>
  * <li>it can contain implemented vals and defs, but they are not considered as subproperties</li>
  * <li>it cannot have more than one parameters list in primary constructor</li>
  * <li>all elements of primary constructor are considered as subproperties</li>
  * <li>all types of subproperties have to be valid [[io.udash.properties.ModelValue]]</li>
  * </ul></li>
  * </ul>
  */
trait ModelPart[T]
object ModelPart {
  implicit def isModelPart[T]: ModelPart[T] = macro io.udash.macros.PropertyMacros.reifyModelPart[T]
}

/**
  * Evidence that type `T` can be used to create SeqProperty.
  *
  * `T` has to be Seq[X] and `X` has to be valid [[io.udash.properties.ModelValue]].
  */
trait ModelSeq[T]
object ModelSeq {
  implicit def isModelSeq[T <: Seq[_]]: ModelSeq[T] = macro io.udash.macros.PropertyMacros.reifyModelSeq[T]
}

/**
  * Evidence that type `T` can be used to create Property.
  *
  * `T` has to be immutable ([[io.udash.properties.ImmutableValue]]), valid [[io.udash.properties.ModelPart]] or valid [[io.udash.properties.ModelSeq]].
  */
trait ModelValue[T]
object ModelValue {
  implicit def isModelValue[T]: ModelValue[T] = macro io.udash.macros.PropertyMacros.reifyModelValue[T]
}