package io.udash
package rest.raw

import org.scalatest.FunSuite

class MappingTest extends FunSuite {
  test("simple") {
    assert(Mapping("a" -> 1).toMap == Map("a" -> 1))
  }

  test("repeated key") {
    assert(Mapping("a" -> 1, "a" -> 2).toMap == Map("a" -> 2))
  }

  test("case sensitive") {
    assert(Mapping("a" -> 1, "A" -> 2).toMap == Map("a" -> 1, "A" -> 2))
  }

  test("append") {
    assert(Mapping("a" -> 1).append("b", 2) == Mapping("a" -> 1, "b" -> 2))
  }

  test("prepend") {
    assert(Mapping("a" -> 1).prepend("b", 2) == Mapping("b" -> 2, "a" -> 1))
  }

  test("case insensitive") {
    assert(IMapping("a" -> 1, "A" -> 2).toMap == Map("a" -> 2))
  }
}
