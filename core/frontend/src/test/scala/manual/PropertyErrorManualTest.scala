package manual

import io.udash._
import io.udash.properties.{HasModelPropertyCreator, IsModelPropertyTemplate, ModelPropertyCreator}

object PropertyErrorManualTest {
  class ClassicClass(i: Int) {
    def more: Int = i + 57
  }

  class ClassicClassWithVar(i: Int, var x: String) {
    def more: Int = i + 57
  }

  /**
    * Expected error:
    * `manual.PropertyErrorManualTest.ClassicClassWithVar` is not a valid ModelProperty template.
    *
    * There are two valid model bases:
    *  * trait (not sealed trait) with following restrictions:
    *    * it cannot contain vars
    *    * it can contain implemented vals and defs, but they are not considered as subproperties
    *    * all abstract vals and defs (without parameters) are considered as subproperties
    *  * (case) class with following restrictions:
    *    * it cannot contain vars
    *    * it can contain implemented vals and defs, but they are not considered as subproperties
    *    * it cannot have more than one parameters list in primary constructor
    *    * all elements of primary constructor are considered as subproperties
    */
//    object ClassicClassIsModelTemplateError {
//      IsModelPropertyTemplate.checkModelPropertyTemplate[ClassicClassWithVar]
//    }

  /**
    * Expected error:
    * `manual.PropertyErrorManualTest.ClassicClassWithVar` is not a valid ModelProperty template.
    *
    * There are two valid model bases:
    *  * trait (not sealed trait) with following restrictions:
    *    * it cannot contain vars
    *    * it can contain implemented vals and defs, but they are not considered as subproperties
    *    * all abstract vals and defs (without parameters) are considered as subproperties
    *  * (case) class with following restrictions:
    *    * it cannot contain vars
    *    * it can contain implemented vals and defs, but they are not considered as subproperties
    *    * it cannot have more than one parameters list in primary constructor
    *    * all elements of primary constructor are considered as subproperties
    */
//  object ClassicClassModelMaterializeError {
//    ModelPropertyCreator.materialize[ClassicClassWithVar]
//  }

  trait A {
    def x: A
    def y: B
  }
  object A extends HasModelPropertyCreator[A]
  trait B {
    def c: Seq[C]
  }
  case class C(d: D)
  trait D {
    def errorField: ClassicClass
  }

  /**
    * Expected error:
    * Class manual.PropertyErrorManualTest.B cannot be used as ModelProperty template.
    * Add `extends HasModelPropertyCreator[manual.PropertyErrorManualTest.B]` to companion object of manual.PropertyErrorManualTest.B.
    */
//  object DeepModelPropertyError {
//    val p = ModelProperty[A]
//    p.subModel(_.x.y)
//  }

  /**
    * Expected error:
    * The path must consist of ModelProperties and only leaf can be a Property, ModelProperty or SeqProperty.
    *  * manual.PropertyErrorManualTest.B is NOT a ModelProperty
    *  * c is a valid subproperty (abstract val/def for trait based model or constructor element for (case) class based model)
    */
//  object DeepModelPropertyError2 {
//    val p = ModelProperty[A]
//    p.subSeq(_.x.y.c)
//  }

  trait PathTestA {
    def s: String
    def b: PathTestB

    def test2: PathTestB = null
  }
  object PathTestA extends HasModelPropertyCreator[PathTestA]

  trait PathTestB {
    def s: String
    def a: PathTestA

    def test: String = "asd"
  }
  object PathTestB extends HasModelPropertyCreator[PathTestB]

  /**
    * The path must consist of ModelProperties and only leaf can be a Property, ModelProperty or SeqProperty.
    *  * manual.PropertyErrorManualTest.PathTestB is a ModelProperty
    *  * test is NOT a valid subproperty (abstract val/def for trait based model or constructor element for (case) class based model)
    */
//  object PathTestError {
//    val p = ModelProperty.empty[PathTestA]
//    p.subProp(_.b.a.b.test)
//  }


  /**
    * The path must consist of ModelProperties and only leaf can be a Property, ModelProperty or SeqProperty.
    *  * manual.PropertyErrorManualTest.PathTestA is a ModelProperty
    *  * test2 is NOT a valid subproperty (abstract val/def for trait based model or constructor element for (case) class based model)
    */
//  object PathTestError2 {
//    val p = ModelProperty.empty[PathTestA]
//    p.subProp(_.b.a.b.a.test2)
//  }


  /**
    * The path must consist of ModelProperties and only leaf can be a Property, ModelProperty or SeqProperty.
    *  * manual.PropertyErrorManualTest.PathTestA is a ModelProperty
    *  * test2 is NOT a valid subproperty (abstract val/def for trait based model or constructor element for (case) class based model)
    */
//  object PathTestError3 {
//    val p = ModelProperty.empty[PathTestA]
//    p.subProp(_.b.a.test2.a.b.a.s)
//  }

  /**
    * Implicit PropertyCreator[Element] not found.
    */
//  object PropertyForGenericType {
//    def create[Element](v: Element): Property[Element] = Property(v)
//    create(72)
//  }
}
