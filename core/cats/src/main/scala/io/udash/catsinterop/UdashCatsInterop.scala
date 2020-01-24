package io.udash.catsinterop

import io.udash.properties.Properties._

trait UdashCatsInterop {
  import cats._

  implicit val readablePropertyApplicative: Applicative[ReadableProperty] = new Applicative[ReadableProperty] {
    override def pure[A](x: A): ReadableProperty[A] = x.toProperty
    override def ap[A, B](ff: ReadableProperty[A => B])(fa: ReadableProperty[A]): ReadableProperty[B] =
      ff.combine(fa)((f, a) => f(a))
  }

  implicit val readablePropertyFunctor: Functor[ReadableProperty] = new Functor[ReadableProperty] {
    override def map[A, B](fa: ReadableProperty[A])(f: A => B): ReadableProperty[B] =
      fa.transform(f)
  }

  implicit val readablePropertySemigroupal: Semigroupal[ReadableProperty] = new Semigroupal[ReadableProperty] {
    override def product[A, B](fa: ReadableProperty[A], fb: ReadableProperty[B]): ReadableProperty[(A, B)] =
      fa.combine(fb)((_, _))
  }

  implicit val propertyInvariant: Invariant[Property] = new Invariant[Property] {
    override def imap[A, B](fa: Property[A])(f: A => B)(g: B => A): Property[B] = fa.bitransform(f)(g)
  }

  implicit val seqPropertyInvariant: Invariant[SeqProperty] = new Invariant[SeqProperty] {
    override def imap[A, B](fa: SeqProperty[A])(f: A => B)(g: B => A): SeqProperty[B] = fa.bitransformToSeq(_.map(f))(_.map(g))
  }

  implicit val roSeqPropertyFunctor: Functor[ReadableSeqProperty] = new Functor[ReadableSeqProperty] {
    override def map[A, B](fa: ReadableSeqProperty[A])(f: A => B): ReadableSeqProperty[B] =
      fa.transformElements(f)
  }

  implicit val roSeqPropertySemigroupal: Semigroupal[ReadableSeqProperty] = new Semigroupal[ReadableSeqProperty] {
    override def product[A, B](fa: ReadableSeqProperty[A], fb: ReadableSeqProperty[B]): ReadableSeqProperty[(A, B)] = {
      fa.zip(fb)((_, _))
    }
  }

  implicit val roSeqPropertyApplicative: Applicative[ReadableSeqProperty] = new Applicative[ReadableSeqProperty] {
    override def pure[A](x: A): ReadableSeqProperty[A] = Vector(x).toSeqProperty
    override def ap[A, B](ff: ReadableSeqProperty[A => B])(fa: ReadableSeqProperty[A]): ReadableSeqProperty[B] =
      ff.zip(fa)(_(_))
  }
//
//  implicit def readablePropertySemigroup[A: Semigroup]: Semigroup[ReadableProperty[A]] = Semigroup.instance {
//    (x: ReadableProperty[A], y: ReadableProperty[A]) => x.combine(y)(Semigroup[A].combine)
//  }
}

object UdashCatsInterop extends UdashCatsInterop
