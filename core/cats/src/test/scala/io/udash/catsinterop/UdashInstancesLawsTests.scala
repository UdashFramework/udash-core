package io.udash
package catsinterop

import cats.laws.discipline.{ApplicativeTests, FunctorTests, InvariantTests, SemigroupalTests}
import io.udash.properties.single.{Property, ReadableProperty}
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline


class UdashInstancesLawsTests extends AnyFunSuite with CatsTestSpec with Discipline {
  import cats.implicits._

  checkAll("ReadableProperty.FunctorLaws", FunctorTests[ReadableProperty].functor[Int, Int, String])
  checkAll("ReadableProperty.ApplicativeLaws", ApplicativeTests[ReadableProperty].applicative[Int, Int, String])
  checkAll("ReadableProperty.SemigroupalLaws", SemigroupalTests[ReadableProperty].semigroupal[Int, Int, String])
  checkAll("Property.InvariantLaws", InvariantTests[Property].invariant[Int, Int, String])
}

