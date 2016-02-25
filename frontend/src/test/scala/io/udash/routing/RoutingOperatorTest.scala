package io.udash.routing

import io.udash.testing.UdashFrontendTest

class RoutingOperatorTest extends UdashFrontendTest {
  "/:/ operator" should {
    "create valid string" in {
      import io.udash.routing.StringRoutingOps.StringRoutingOps
      "" /:/ "abc" /:/ 123 /:/ "asd" should be("/abc/123/asd")
    }

    "provide url slices in pattern matching" in {
      val url = "/abc/123/asd"

      (url match {
        case "" /:/ x /:/ y /:/ z if x == "abc" && y == "123" && z == "asd" => true
        case _ => false
      }) should be(true)

      (url match {
        case "" /:/ x /:/ y /:/ "asd" if x == "abc" && y == "123" => true
        case _ => false
      }) should be(true)

      (url match {
        case "" /:/ x /:/ y /:/ "asz" if x == "abc" && y == "123" => true
        case _ => false
      }) should be(false)

      (url match {
        case "" /:/ x /:/ "123" /:/ z if x == "abc" && z == "asd" => true
        case _ => false
      }) should be(true)

      (url match {
        case "" /:/ x /:/ "12" /:/ z if x == "abc" && z == "asd" => true
        case _ => false
      }) should be(false)

      (url match {
        case "" /:/ "abc" /:/ y /:/ z if y == "123" && z == "asd" => true
        case _ => false
      }) should be(true)

      (url match {
        case "" /:/ "abcd" /:/ y /:/ z if y == "123" && z == "asd" => true
        case _ => false
      }) should be(false)

      (url match {
        case "x" /:/ x /:/ y /:/ z if x == "abc" && y == "123" && z == "asd" => true
        case _ => false
      }) should be(false)
    }
  }
}
