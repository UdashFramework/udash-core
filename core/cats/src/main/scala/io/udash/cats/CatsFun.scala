package io.udash.cats

import io.udash.properties.HasModelPropertyCreator


object CatsFun extends App {
  import _root_.cats._
  import _root_.cats.implicits._
  import io.udash.properties.Properties._

  implicit val rpApplicative: Applicative[ReadableProperty] = new Applicative[ReadableProperty] {
    override def pure[A](x: A): ReadableProperty[A] = x.toProperty
    override def ap[A, B](ff: ReadableProperty[A => B])(fa: ReadableProperty[A]): ReadableProperty[B] =
      ff.combine(fa)((f, a) => f(a))
  }

  implicit val pInvariant: Invariant[Property] = new Invariant[Property] {
    override def imap[A, B](fa: Property[A])(f: A => B)(g: B => A): Property[B] = fa.transform(f, g)
  }

  case class Test(i: Int, s: String)
  object Test extends HasModelPropertyCreator[Test]

  val mp = ModelProperty(Test(4, "2"))

  val r = (mp.roSubProp(_.i), mp.roSubProp(_.s)).mapN((i, s) => i+s)
  r.listen(println, initUpdate = true)


  mp.subProp(_.i).set(6)

  mp.subProp(_.s).set("9")

  CallbackSequencer().sequence {
    mp.subProp(_.i).set(6)
    mp.subProp(_.s).set("4")
  }
}
