package io.udash.properties

/**
  * Evidence that type `T` can be used to create ModelProperty.
  *
  * There are two valid model bases:
  * <ul>
  * <li>trait (not sealed trait) with following restrictions:<ul>
  * <li>it cannot contain vars</li>
  * <li>it can contain implemented vals and defs, but they are not considered as subproperties</li>
  * <li>all abstract vals and defs (without parameters) are considered as subproperties</li>
  * </ul></li>
  * <li>(case) class with following restrictions:<ul>
  * <li>it cannot contain vars</li>
  * <li>it can contain implemented vals and defs, but they are not considered as subproperties</li>
  * <li>it cannot have more than one parameters list in primary constructor</li>
  * <li>all elements of primary constructor are considered as subproperties</li>
  * </ul></li>
  * </ul>
  */
class IsModelPropertyTemplate[T]
object IsModelPropertyTemplate {
  implicit def checkModelPropertyTemplate[T]: IsModelPropertyTemplate[T] =
    macro io.udash.macros.PropertyMacros.checkModelPropertyTemplate[T]
}

