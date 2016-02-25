package io.udash.utils

import io.udash.testing.UdashSharedTest

class BidirectionalTest extends UdashSharedTest {
  "Bidirectional" should {
    "create reversed partial function" in {
      val (pf, rpf) = Bidirectional[Int, String]({
        case 1 => "1"
        case 2 => "2"
        case 3 => "3"
      })

      pf(1) should be("1")
      pf(2) should be("2")
      pf(3) should be("3")
      rpf("1") should be(1)
      rpf("2") should be(2)
      rpf("3") should be(3)

      case class A(i: Int)
      case class B(s: String)

      val (ccpf, ccrpf) = Bidirectional[A, B]({
        case A(1) => B("1")
        case A(2) => B("2")
        case A(3) => B("3")
      })

      ccpf(A(1)) should be(B("1"))
      ccpf(A(2)) should be(B("2"))
      ccpf(A(3)) should be(B("3"))
      ccrpf(B("1")) should be(A(1))
      ccrpf(B("2")) should be(A(2))
      ccrpf(B("3")) should be(A(3))
    }

    "not compile if bodies are not unique" in {
      """val (pf, rpf) = Bidirectional[Int, String]({
        |  case 1 => "1"
        |  case 2 => "2"
        |  case 3 => "3"
        |})""".stripMargin should compile

      """val (pf, rpf) = Bidirectional[Int, String]({
        |  case 1 => "1"
        |  case 2 => "3"
        |  case 3 => "3"
        |})""".stripMargin shouldNot compile

      """case class A(a: Int)
        |case class B(b: String)
        |
        |val (pf, rpf) = Bidirectional[A, B]({
        |  case A(1) => B("1")
        |  case A(2) => B("2")
        |  case A(3) => B("3")
        |})""".stripMargin should compile

      """case class A(a: Int)
        |case class B(b: String)
        |
        |val (pf, rpf) = Bidirectional[A, B]({
        |  case A(1) => B("1")
        |  case A(2) => B("3")
        |  case A(3) => B("3")
        |})""".stripMargin shouldNot compile

      """var x = "OK"
        |val (pf, rpf) = Bidirectional[String, String]({
        |  case x if x == "ok" => "OK"
        |  case x => x
        |})""".stripMargin should compile

      """var x = "OK"
        |val (pf, rpf) = Bidirectional[String, String]({
        |  case x if x == "ok" => "OK"
        |  case x => "OK"
        |})""".stripMargin shouldNot compile
    }

    "not compile if pf contains expressions" in {
      """val t = "a"
        |val (pf, rpf) = Bidirectional[String, String]({
        |  case "A" => t + "OK"
        |  case "B" => "FAIL"
        |})""".stripMargin shouldNot compile
    }

    "not compile if pf uses wildcards in patterns" in {
      """var x = "ok"
        |val (pf, rpf) = Bidirectional[String, String]({
        |  case x if x == "ok" => "OK"
        |  case x => x
        |})""".stripMargin should compile

      """val (pf, rpf) = Bidirectional[String, String]({
        |  case x if x == "ok" => "OK"
        |})""".stripMargin shouldNot compile

      """val (pf, rpf) = Bidirectional[String, String]({
        |  case _ => "OK"
        |})""".stripMargin shouldNot compile
    }

  }
}
