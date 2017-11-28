package io.udash.properties

import io.udash.testing.UdashFrontendTest

class UtilsTest extends UdashFrontendTest {
  class ClassWithVar(x: String) {
    var s: String = "qwe"
  }

  class ClassicClass(i: Int) {
    def more: Int = i + 57
  }

  case class CCA(r: CCB)
  case class CCB(r: CCA, i: Int, b: String, c: ClassicClass, d: ClassWithVar)
  case class ValidCCA(r: ValidCCB)
  case class ValidCCB(r: ValidCCA, i: Int, b: String, d: CCD)
  case class CCD(r: CCD)

  trait A {
    def x: A
    def y: B
  }
  trait B {
    def c: Seq[C]
  }
  case class C(d: D)
  trait D {
    def errorField: ClassWithVar
  }

  trait ValidModelA {
    def x: ValidModelA
    def y: ValidModelB
  }
  trait ValidModelB {
    def c: Seq[ValidModelC]
  }
  case class ValidModelC(d: ValidModelD)
  trait ValidModelD {
    def field: ValidModelA
  }

  "ImmutableValue" should {
    "check if value is immutable" in {
      """ImmutableValue.isImmutable[ValidCCA]""".stripMargin should compile
      """ImmutableValue.isImmutable[ValidCCB]""".stripMargin should compile
      """ImmutableValue.isImmutable[CCD]""".stripMargin should compile

      """ImmutableValue.isImmutable[ClassicClass]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[CCA]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[CCB]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[A]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[B]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[C]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[D]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[ValidModelA]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[ValidModelB]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[ValidModelC]""".stripMargin shouldNot compile
      """ImmutableValue.isImmutable[ValidModelD]""".stripMargin shouldNot compile
    }

    "allow user to create property" in {
      import io.udash._
      trait TraitModel {
        def name: String
        def data: CCWithMap
      }

      case class CCWithMap(valueMap: Map[String, Map[Int, Double]])
      implicit val im: ImmutableValue[CCWithMap] = null

      val p = ModelProperty[TraitModel]

      val sp = p.subProp(_.data)
      sp.set(CCWithMap(Map("x" -> Map(5 -> 5.5), "y" -> Map.empty)))

      val m = sp.get
      m.valueMap.contains("x") should be(true)
      m.valueMap.contains("y") should be(true)
      m.valueMap.contains("z") should be(false)
      m.valueMap("x")(5) should be(5.5)
    }
  }

  "ModelValue" should {
    "check if value is valid model value" in {
      """ModelValue.isModelValue[ValidCCA]""".stripMargin should compile
      """ModelValue.isModelValue[ValidCCB]""".stripMargin should compile
      """ModelValue.isModelValue[CCD]""".stripMargin should compile
      """ModelValue.isModelValue[ValidModelA]""".stripMargin should compile
      """ModelValue.isModelValue[ValidModelB]""".stripMargin should compile
      """ModelValue.isModelValue[ValidModelC]""".stripMargin should compile
      """ModelValue.isModelValue[ValidModelD]""".stripMargin should compile
      """ModelValue.isModelValue[ClassicClass]""".stripMargin should compile

      """ModelValue.isModelValue[ClassWithVar]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[CCA]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[CCB]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[A]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[B]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[C]""".stripMargin shouldNot compile
      """ModelValue.isModelValue[D]""".stripMargin shouldNot compile
    }
  }

  "ModelPart" should {
    "check if value is valid model part" in {
      """ModelPart.isModelPart[ValidCCA]""".stripMargin should compile
      """ModelPart.isModelPart[ValidCCB]""".stripMargin should compile
      """ModelPart.isModelPart[CCD]""".stripMargin should compile
      """ModelPart.isModelPart[ValidModelA]""".stripMargin should compile
      """ModelPart.isModelPart[ValidModelB]""".stripMargin should compile
      """ModelPart.isModelPart[ValidModelC]""".stripMargin should compile
      """ModelPart.isModelPart[ValidModelD]""".stripMargin should compile
      """ModelPart.isModelPart[ClassicClass]""".stripMargin should compile

      """ModelPart.isModelPart[ClassWithVar]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[CCA]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[CCB]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[A]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[B]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[C]""".stripMargin shouldNot compile
      """ModelPart.isModelPart[D]""".stripMargin shouldNot compile
    }
  }

  "ModelSeq" should {
    "check if value is valid model part" in {
      """ModelSeq.isModelSeq[Seq[ValidCCA]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[ValidCCB]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[CCD]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[ValidModelA]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[ValidModelB]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[ValidModelC]]""".stripMargin should compile
      """ModelSeq.isModelSeq[Seq[ValidModelD]]""".stripMargin should compile

      """ModelSeq.isModelSeq[ValidCCA]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[ValidCCB]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[CCD]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[ValidModelA]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[ValidModelB]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[ValidModelC]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[ValidModelD]""".stripMargin shouldNot compile

      """ModelSeq.isModelSeq[Seq[ClassicClass]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[CCA]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[CCB]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[A]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[B]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[C]""".stripMargin shouldNot compile
      """ModelSeq.isModelSeq[Seq[D]""".stripMargin shouldNot compile
    }
  }
}
