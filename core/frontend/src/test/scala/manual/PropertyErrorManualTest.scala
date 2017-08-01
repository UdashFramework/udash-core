package manual

import io.udash._
import io.udash.properties.{ImmutableValue, ModelPart, ModelSeq, ModelValue}

object PropertyErrorManualTest {
  import scala.concurrent.ExecutionContext.Implicits.global

  class ClassicClass(i: Int) {
    def more: Int = i + 57
  }

  /**
    * Expected error:
    *
    * `manual.PropertyErrorManualTest.ClassicClass` should meet requirements for one of:
    *   * model value - it has to be an immutable value (io.udash.properties.ImmutableValue[manual.PropertyErrorManualTest.ClassicClass] has to exist), model part or model seq
    *   * model part - it has to be a trait with abstract methods returning valid model values or an immutable case class
    *   * model seq - it has to be Seq[T] where T is a valid model value
    *
    * Try to call one of these methods in your code to get more details:
    *   * ImmutableValue.isImmutable[manual.PropertyErrorManualTest.ClassicClass]
    *   * ModelValue.isModelValue[manual.PropertyErrorManualTest.ClassicClass]
    *   * ModelPart.isModelPart[manual.PropertyErrorManualTest.ClassicClass]
    *   * ModelSeq.isModelSeq[manual.PropertyErrorManualTest.ClassicClass]
    *
    *     val classicClassProperty = Property[ClassicClass]
    */
//  object GeneralPropertyError {
//    val classicClassProperty = Property[ClassicClass]
//  }

  /**
    * Expected error:
    * The type `manual.PropertyErrorManualTest.ClassicClass` does not meet model value requirements. This is not immutable value nor valid model part nor model seq.
    *
    * Model value checks:
    *   * isImmutable: false (check: ImmutableValue.isImmutable[manual.PropertyErrorManualTest.ClassicClass] for details)
    *   * isModelPart: false
    *   * isModelSeq: false
    *
    * Use ModelPart.isModelPart[manual.PropertyErrorManualTest.ClassicClass] and ModelSeq.isModelSeq[manual.PropertyErrorManualTest.ClassicClass] to get more details about `isModelPart` and `isModelSeq` checks.
    */
//  object ClassicClassIsModelValueError {
//    ModelValue.isModelValue[ClassicClass]
//  }

  /**
    * Expected error:
    * The type `manual.PropertyErrorManualTest.ClassicClass` does not meet model part requirements. It must be (not sealed) trait or immutable case class.
    *
    * Model part checks:
    * * for traits:
    *   * isTrait: false
    *   * isNotSealedTrait: true
    *   * isNotSeq: true
    *   * members: Visible only for traits.
    * * for case class:
    *   * isCaseClass: false
    *   * members: Visible only for case classes.
    *
    * Use ModelValue.isModelValue[T] to get more details about `isModelValue` check.
    */
//  object ClassicClassIsModelPartError {
//    ModelPart.isModelPart[ClassicClass]
//  }

  /**
    * Expected error:
    * The type `Seq[manual.PropertyErrorManualTest.ClassicClass]` does not meet model seq requirements. This must be Seq with valid model value as type arg.
    *
    * Model Seq checks:
    *   * isSeqType: true
    *   * type arg: manual.PropertyErrorManualTest.ClassicClass -> isModelValue: false;
    *
    * Use ModelValue.isModelValue[manual.PropertyErrorManualTest.ClassicClass] to get more details.
    */
//  object ClassicClassIsModelSeqError {
//    ModelSeq.isModelSeq[Seq[ClassicClass]]
//  }

  trait A {
    def x: A
    def y: B
  }
  trait B {
    def c: Seq[C]
  }
  case class C(d: D)
  trait D {
    def errorField: ClassicClass
  }

  /**
    * Expected error:
    * `manual.PropertyErrorManualTest.A` should meet requirements for one of:
    *   * model value - it has to be an immutable value (io.udash.properties.ImmutableValue[manual.PropertyErrorManualTest.A] has to exist), model part or model seq
    *   * model part - it has to be a trait with abstract methods returning valid model values or an immutable case class
    *   * model seq - it has to be Seq[T] where T is a valid model value
    *
    * Try to call one of these methods in your code to get more details:
    *   * ImmutableValue.isImmutable[manual.PropertyErrorManualTest.A]
    *   * ModelValue.isModelValue[manual.PropertyErrorManualTest.A]
    *   * ModelPart.isModelPart[manual.PropertyErrorManualTest.A]
    *   * ModelSeq.isModelSeq[manual.PropertyErrorManualTest.A]
    */
//  object DeepModelPropertyError {
//    val p = ModelProperty[A]
//  }

  /**
    * Expected error:
    * The type `manual.PropertyErrorManualTest.D` does not meet model value requirements. This is not immutable value nor valid model part nor model seq.
    *
    * Model value checks:
    *   * isImmutable: false (check: ImmutableValue.isImmutable[manual.PropertyErrorManualTest.D] for details)
    *   * isModelPart: false
    *   * isModelSeq: false
    *
    * Use ModelPart.isModelPart[manual.PropertyErrorManualTest.D] and ModelSeq.isModelSeq[manual.PropertyErrorManualTest.D] to get more details about `isModelPart` and `isModelSeq` checks.
    */
//  object CModelPartError {
//    ModelPart.isModelPart[C]
//  }

  /**
    * Expected error:
    * The type `Seq[manual.PropertyErrorManualTest.C]` does not meet model seq requirements. This must be Seq with valid model value as type arg.
    *
    * Model Seq checks:
    *   * isSeqType: true
    *   * type arg: manual.PropertyErrorManualTest.C -> isModelValue: false;
    *
    * Use ModelValue.isModelValue[manual.PropertyErrorManualTest.C] to get more details.
    */
//  object SeqCModelSeqError {
//    ModelSeq.isModelSeq[Seq[C]]
//  }

  /**
    * Expected error:
    *  The type `Seq[manual.PropertyErrorManualTest.A]` does not meet model seq requirements. This must be Seq with valid model value as type arg.
    *
    * Model Seq checks:
    *   * isSeqType: true
    *   * type arg: manual.PropertyErrorManualTest.A -> isModelValue: false;
    *
    * Use ModelValue.isModelValue[manual.PropertyErrorManualTest.A] to get more details.
    */
//  object SeqAModelSeqError {
//    ModelSeq.isModelSeq[Seq[A]]
//  }

  case class CC(r: CC, i: Int, b: String, c: ClassicClass)

  /**
    * Expected error:
    * The type `manual.PropertyErrorManualTest.CC` does not meet immutable value requirements.
    *
    * Immutable value checks:
    *   * isImmutableCaseClass: false
    *     * isCaseClass: true
    *     * members:
    *       - c: manual.PropertyErrorManualTest.ClassicClass -> stable: true, isImmutableValue: false;
    *       - b: String -> stable: true, isImmutableValue: true;
    *       - i: Int -> stable: true, isImmutableValue: true;
    *       - r: manual.PropertyErrorManualTest.CC -> stable: true, isImmutableValue: true;
    *     Use ImmutableValue.isImmutable[manual.PropertyErrorManualTest.ClassicClass] to get more details about `isImmutableValue` check.
    *   * isImmutableSealedHierarchy: false
    *   * isImmutableSeq: false - for example: Seq[String]
    *   * isImmutableOption: false - for example: Option[String]

    */
//  object ImmutableCaseClassError {
//    ImmutableValue.isImmutable[CC]
//  }

  case class CCA(r: CCB)
  case class CCB(r: CCA, i: Int, b: String, c: ClassicClass)
  case class CCD(r: CCD)

  /**
    * Expected error:
    * Immutable value checks:
    *   * isImmutableCaseClass: false
    *     * isCaseClass: true
    *     * members:
    *       - r: manual.PropertyErrorManualTest.CCB -> stable: true, isImmutableValue: false;
    *     Use ImmutableValue.isImmutable[manual.PropertyErrorManualTest.CCB] to get more details about `isImmutableValue` check.
    *   * isImmutableSealedHierarchy: false
    *   * isImmutableSeq: false - for example: Seq[String]
    *   * isImmutableOption: false - for example: Option[String]
    * The type `manual.PropertyErrorManualTest.CCB` does not meet immutable value requirements.
    *
    * Immutable value checks:
    *   * isImmutableCaseClass: false
    *     * isCaseClass: true
    *     * members:
    *       - c: manual.PropertyErrorManualTest.ClassicClass -> stable: true, isImmutableValue: false;
    *       - b: String -> stable: true, isImmutableValue: true;
    *       - i: Int -> stable: true, isImmutableValue: true;
    *       - r: manual.PropertyErrorManualTest.CCA -> stable: true, isImmutableValue: true;
    *     Use ImmutableValue.isImmutable[manual.PropertyErrorManualTest.ClassicClass] to get more details about `isImmutableValue` check.
    *   * isImmutableSealedHierarchy: false
    *   * isImmutableSeq: false - for example: Seq[String]
    *   * isImmutableOption: false - for example: Option[String]

    */
//  object ImmutableRecursiveCaseClassError {
//    ImmutableValue.isImmutable[CCA]
//    ImmutableValue.isImmutable[CCB]
//    ImmutableValue.isImmutable[CCD]
//  }

  trait PathTestA {
    def s: String
    def b: PathTestB

    def test2: PathTestB = null
  }

  trait PathTestB {
    def s: String
    def a: PathTestA

    def test: String = "asd"
  }

  /**
    * The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.
    *  * test is NOT a valid subproperty (unimplemented def for trait based model or constructor element for case class based model)
    *  * String is an immutable value (check ImmutableValue.isImmutable[String])
    *  * String is NOT a ModelPart (check ModelPart.isModelPart[String])
    *  * String is NOT a ModelSeq (check ModelSeq.isModelSeq[String])
    *
    *     p.subProp(_.b.a.b.test)
    */
//  object PathTestError {
//    val p = ModelProperty.empty[PathTestA]
//    p.subProp(_.b.a.b.test)
//  }


  /**
    * The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.
    *  * test2 is NOT a valid subproperty (unimplemented def for trait based model or constructor element for case class based model)
    *  * manual.PropertyErrorManualTest.PathTestB is a ModelPart (check ModelPart.isModelPart[manual.PropertyErrorManualTest.PathTestB])
    *
    *     p.subProp(_.b.a.test2.a.b.a.s)
    */
//  object PathTestError2 {
//    val p = ModelProperty.empty[PathTestA]
//    p.subProp(_.b.a.test2.a.b.a.s)
//  }
}
