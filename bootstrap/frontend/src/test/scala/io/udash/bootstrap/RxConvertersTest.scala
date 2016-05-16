package io.udash.bootstrap

import io.udash.properties.CastableProperty
import io.udash.testing.UdashFrontendTest
import rx.Var

import scala.collection.mutable

class RxConvertersTest extends UdashFrontendTest with UdashBootstrapImplicits {

  case class C(i: Int, s: String)

  sealed trait T

  case object TO1 extends T

  case object TO2 extends T

  case class TC1(i: Int) extends T

  case class TC2(s: String) extends T

  "Var used as a Property" should {
    "update value" in {
      val p: CastableProperty[Int] = Var[Int](5)
      val tp: CastableProperty[T] = Var[T](TO1)
      val cp: CastableProperty[C] = Var[C](C(1, "asd"))

      p.get should be(5)
      for (i <- Range(-200000, 200000, 666)) {
        p.set(i)
        p.get should be(i)
      }

      tp.get should be(TO1)
      tp.set(TC1(12))
      tp.get should be(TC1(12))
      tp.set(TO2)
      tp.get should be(TO2)

      cp.get should be(C(1, "asd"))
      cp.set(C(12, "asd2"))
      cp.get should be(C(12, "asd2"))
    }

    "fire listeners on value change" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v

      val p: CastableProperty[Int] = Var[Int](5)
      val tp: CastableProperty[T] = Var[T](TO1)
      val cp: CastableProperty[C] = Var[C](C(1, "asd"))

      p.listen(listener)
      tp.listen(listener)
      cp.listen(listener)

      p.set(7)
      p.set(-321)
      p.set(-321)
      p.set(-321)
      p.set(-321)
      tp.set(TC1(12))
      tp.set(TO2)
      cp.set(C(12, "asd2"))

      values.size should be(5)
      values(0) should be(7)
      values(1) should be(-321)
      values(2) should be(TC1(12))
      values(3) should be(TO2)
      values(4) should be(C(12, "asd2"))
    }

    "transform and synchronize value" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v

      val cp = Var[C](C(1, "asd"))
      val tp = cp.transform[(T, T)](
        (c: C) => Tuple2(TC1(c.i), TC2(c.s)),
        (t: (T, T)) => t match {
          case (TC1(i), TC2(s)) => C(i, s)
          case _ => C(0, "")
        }
      )

      tp.listen(listener)
      cp.listen(listener)

      cp.get should be(C(1, "asd"))
      tp.get should be(Tuple2(TC1(1), TC2("asd")))

      cp.set(C(12, "asd2"))
      cp.get should be(C(12, "asd2"))
      tp.get should be(Tuple2(TC1(12), TC2("asd2")))

      tp.set(Tuple2(TC1(-5), TC2("tp")))
      cp.get should be(C(-5, "tp"))
      tp.get should be(Tuple2(TC1(-5), TC2("tp")))

      values.size should be(4)
      values should contain(C(12, "asd2"))
      values should contain(Tuple2(TC1(12), TC2("asd2")))
      values should contain(Tuple2(TC1(-5), TC2("tp")))
      values should contain(C(-5, "tp"))
    }
  }


}
