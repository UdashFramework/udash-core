package io.udash
package rest.raw

import org.scalatest.FunSuite

class MappingTest extends FunSuite {
  test("simple") {
    assert(Mapping.create("a" -> 1).toMap == Map("a" -> 1))
  }

  test("repeated key") {
    assert(Mapping.create("a" -> 1, "a" -> 2).toMap == Map("a" -> 2))
  }

  test("case sensitive") {
    assert(Mapping.create("a" -> 1, "A" -> 2).toMap == Map("a" -> 1, "A" -> 2))
  }

  test("append") {
    assert(Mapping.create("a" -> 1).append("b", 2) == Mapping.create("a" -> 1, "b" -> 2))
  }

  test("prepend") {
    assert(Mapping.create("a" -> 1).prepend("b", 2) == Mapping.create("b" -> 2, "a" -> 1))
  }

  test("case insensitive") {
    assert(IMapping.create("a" -> 1, "A" -> 2).toMap == Map("a" -> 2))
  }
}
