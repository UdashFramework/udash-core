package io.udash.properties

import io.udash.testing.UdashCoreTest

class UtilsTest extends UdashCoreTest {
  class ClassWithVar(x: String) {
    var s: String = s"qwe$x"
  }

  class ClassicClass(i: Int) {
    def more: Int = i + 57
  }

  case class CCA(r: CCB)
  case class CCB(r: CCA, i: Int, b: String, c: ClassicClass, d: ClassWithVar)
  case class ReqCCA(r: ReqCCB)
  case class ReqCCB(r: ReqCCA, i: Int, b: String, d: ReqCCD)
  case class ReqCCD(r: ReqCCD)

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

  trait ReqModelA {
    def x: ReqModelA
    def y: ReqModelB
  }
  trait ReqModelB {
    def c: Seq[ReqModelC]
  }
  case class ReqModelC(d: ReqModelD)
  trait ReqModelD {
    def field: ReqModelA
  }

  "IsModelPropertyTemplate" should {
    "check if value is valid model template" in {
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqCCA]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqCCB]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqCCD]""".stripMargin should compile

      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqModelA]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqModelB]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqModelC]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[ReqModelD]""".stripMargin should compile

      """IsModelPropertyTemplate.checkModelPropertyTemplate[ClassicClass]""".stripMargin should compile

      """IsModelPropertyTemplate.checkModelPropertyTemplate[CCA]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[CCB]""".stripMargin should compile

      """IsModelPropertyTemplate.checkModelPropertyTemplate[A]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[B]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[C]""".stripMargin should compile
      """IsModelPropertyTemplate.checkModelPropertyTemplate[D]""".stripMargin should compile

      """IsModelPropertyTemplate.checkModelPropertyTemplate[ClassWithVar]""".stripMargin shouldNot compile
    }
  }
}
