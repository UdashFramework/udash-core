package io.udash
package catsinterop

import cats.kernel.Eq
import org.scalacheck.Arbitrary

trait CatsTestSpec {
  import cats.implicits._
  import io.udash.properties.Properties._



  implicit def eqReadableProperty[A: Eq]: Eq[ReadableProperty[A]] = Eq[A].contramap(_.get)
  implicit def eqProperty[A: Eq]: Eq[Property[A]] = Eq[A].contramap(_.get)

  implicit def arbitraryReadableProperty[A](implicit arb: Arbitrary[A]): Arbitrary[ReadableProperty[A]] =
    Arbitrary(arb.arbitrary.map(_.toProperty))
  implicit def arbitraryProperty[A](implicit arb: Arbitrary[A]): Arbitrary[Property[A]] =
    Arbitrary(arb.arbitrary.map(Property(_)))
}
