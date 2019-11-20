package io.udash
package catsinterop


object CatsFun extends App {
  import cats._
  import io.udash.properties.Properties._

  object ReadablePropertyInstances {
    implicit val rpApplicative: Applicative[ReadableProperty] = new Applicative[ReadableProperty] {
      override def pure[A](x: A): ReadableProperty[A] = x.toProperty
      override def ap[A, B](ff: ReadableProperty[A => B])(fa: ReadableProperty[A]): ReadableProperty[B] =
        ff.combine(fa)((f, a) => f(a))
    }

    implicit val rpropFunctor: Functor[ReadableProperty] = new Functor[ReadableProperty] {
      override def map[A, B](fa: ReadableProperty[A])(f: A => B): ReadableProperty[B] =
        fa.transform(f)
    }

    implicit val rpropSemigroupal: Semigroupal[ReadableProperty] = new Semigroupal[ReadableProperty] {
      override def product[A, B](fa: ReadableProperty[A], fb: ReadableProperty[B]): ReadableProperty[(A, B)] =
        fa.combine(fb)((_, _))
    }
  }

  object PropertyInstances {
    implicit val pInvariant: Invariant[Property] = new Invariant[Property] {
      override def imap[A, B](fa: Property[A])(f: A => B)(g: B => A): Property[B] = fa.transform(f, g)
    }
  }
}
