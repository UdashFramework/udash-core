package io.udash.bootstrap

import io.udash._
import io.udash.testing.UdashFrontendTest
import rx.{Ctx, Rx, Var}

import scala.collection.mutable
import scala.util.Success

class RxConvertersTest extends UdashFrontendTest with RxConverters {

  sealed trait T

  case class C(i: Int, s: String)

  case class TC1(i: Int) extends T

  case class TC2(s: String) extends T

  case object TO1 extends T

  case object TO2 extends T


  "Var used as a Property" should {
    "update value" in {
      val p = Var[Int](5)
      val tp = Var[T](TO1)
      val cp = Var[C](C(1, "asd"))

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

      val p = Var[Int](5)
      val tp = Var[T](TO1)
      val cp = Var[C](C(1, "asd"))

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
    "cache adapters" in {
      testSameInstance[Var[Int], Property[Int]](Var(1))
      testSameInstance[Rx[Int], ReadableProperty[Int]](Var(1).r)
      testSameInstance[Var[Seq[Int]], SeqProperty[Int]](Var(Seq(1, 2, 3)))
      testSameInstance[Rx[Seq[Int]], ReadableSeqProperty[Int]](Var(Seq(1, 2, 3)).r)
    }
  }


  //https://github.com/lihaoyi/scala.rx/blob/9253578bf7b88575faa52a70340013fb57c90ad4/scalarx/shared/src/test/scala/rx/BasicTests.scala
  "Property used as a Var" should {
    "update dependent values" in {
      val a = Property(1)
      val b = Property(2)
      val c = Rx.build { (o: Ctx.Owner, d: Ctx.Data) => a()(d) + b()(d) }
      c.now shouldBe 3
      a() = 4
      c.now shouldBe 6
    }
    "maintain change order" in {
      var changes = ""
      val a = Property(1)
      val b = Rx {
        changes += "b"
        a() + 1
      }
      val c = Rx {
        changes += "c"
        a() + b()
      }
      changes shouldBe "bc"
      a() = 4
      changes shouldBe "bcbc"
    }
    "work with options" in {
      val a = Property[Option[Int]](None)
      val b = Property[Option[Int]](None)
      val c = Rx {
        a().flatMap { x =>
          b().map { y =>
            x + y
          }
        }
      }
      a() = Some(1)
      b() = Some(2)
      c.now should contain(3)
    }
    "work with pattern matching" in {
      val a = Property(1)
      val b = Property(2)
      val c = Rx {
        a() match {
          case 0 => b()
          case x => x
        }
      }
      c.now shouldBe 1
      a() = 0
      c.now shouldBe 2
    }
    "work in implicit conversions" in {
      val a = Property(1)
      val b = Property(2)
      val c = Rx {
        val t1 = a() + " and " + b()
        val t2 = a() to b()
        t1 + ": " + t2
      }
      c.now shouldBe "1 and 2: Range(1, 2)"
      a() = 0
      c.now shouldBe "0 and 2: Range(0, 1, 2)"
    }
    "allow use in by name parameters" in {
      val a = Property(1)
      val b = Rx {
        Some(1).getOrElse(a())
      }
      b.now shouldBe 1
    }
    "work as Obs" in {
      val a = Property(1)
      var count = 0
      val o = a.trigger {
        count = a.now + 1
      }
      count shouldBe 2
      a() = 4
      count shouldBe 5
    }
    "skip initial change" in {
      val a = Property(1)
      var count = 0
      val o = a.triggerLater {
        count = count + 1
      }

      count shouldBe 0
      a() = 2
      count shouldBe 1
      a() = 2
      count shouldBe 1
      a() = 3
      count shouldBe 2
    }
    "simply work" in {
      val a = Property(1)
      val b = Rx {
        a() * 2
      }
      val c = Rx {
        a() + 1
      }
      val d = Rx {
        b() + c()
      }
      var bS = 0
      val bO = b.trigger {
        bS += 1
      }
      var cS = 0
      val cO = c.trigger {
        cS += 1
      }
      var dS = 0
      val dO = d.trigger {
        dS += 1
      }

      bS shouldBe 1
      cS shouldBe 1
      dS shouldBe 1

      a() = 2
      bS shouldBe 2
      cS shouldBe 2
      dS shouldBe 2

      a() = 1
      bS shouldBe 3
      cS shouldBe 3
      dS shouldBe 3
    }
    "support killing" in {
      val a = Property(1)
      var i = 0
      val o = a.trigger(i += 1)
      a.Internal.observers.size shouldBe 1
      i shouldBe 1
      a() = 2
      i shouldBe 2
      o.kill()
      a() = 3
      a.Internal.observers.size shouldBe 0
      i shouldBe 2
    }

    "handle errors" in {
      val a = Property(1L)
      val b = Rx {
        1 / a()
      }
      b.now shouldBe 1
      b.toTry shouldBe Success(1L)
      a() = 0
      intercept[Exception] {
        b.now
      }
      b.toTry.isFailure shouldBe true
    }
    "handle errors in chains" in {
      val a = Property(1L)
      val b = Property(2L)

      val c = Rx {
        a() / b()
      }
      val d = Rx {
        a() * 5
      }
      val e = Rx {
        5 / b()
      }
      val f = Rx {
        a() + b() + 2
      }
      val g = Rx {
        f() + c()
      }

      c.toTry.get shouldBe 0
      d.toTry.get shouldBe 5
      e.toTry.get shouldBe 2
      f.toTry.get shouldBe 5
      g.toTry.get shouldBe 5

      b() = 0

      c.toTry.isFailure shouldBe true
      d.toTry.get shouldBe 5
      e.toTry.isFailure shouldBe true
      f.toTry.get shouldBe 3
      g.toTry.isFailure shouldBe true
    }
    "not propagate when unchanged" in {
      val a = Property(1)
      var ai = 0
      var thing = 0
      val b = Rx {
        math.max(a(), 0) + thing
      }
      var bi = 0
      val c = Rx {
        b() / 2
      }
      var ci = 0

      a.trigger {
        ai += 1
      }
      b.trigger {
        bi += 1
      }
      c.trigger {
        ci += 1
      }
      // if c doesn't change (because of rounding) don't update ci
      ai shouldBe 1
      bi shouldBe 1
      ci shouldBe 1
      a() = 0
      ai shouldBe 2
      bi shouldBe 2
      ci shouldBe 1
      a() = 1
      ai shouldBe 3
      bi shouldBe 3
      ci shouldBe 1
      // but if c changes then update ci
      a() = 2
      ai shouldBe 4
      bi shouldBe 4
      ci shouldBe 2

      // if a doesn't change, don't update anything
      a() = 2
      ai shouldBe 4
      bi shouldBe 4
      ci shouldBe 2

      // if b doesn't change, don't update bi or ci
      a() = 0
      ai shouldBe 5
      bi shouldBe 5
      ci shouldBe 3
      a() = -1
      ai shouldBe 6
      bi shouldBe 5
      ci shouldBe 3

      // all change then all update
      a() = 124
      ai shouldBe 7
      bi shouldBe 6
      ci shouldBe 4

      // recalcing with no change means no update
      b.recalc()
      ai shouldBe 7
      bi shouldBe 6
      ci shouldBe 4

      // recalcing with change (for whatever reason) updates downstream
      thing = 12
      b.recalc()
      ai shouldBe 7
      bi shouldBe 7
      ci shouldBe 5
    }
    "support printing" in {
      val var1: Var[Int] = Property(1)
      val v = var1.toString()
      val r = Rx(1).toString
      v.startsWith("Var@") shouldBe true
      v.endsWith("(1)") shouldBe true
      r.startsWith("Rx@") shouldBe true
      r.endsWith("(1)") shouldBe true
    }
    "cache adapters" in {
      testSameInstance[Property[Int], Var[Int]](Property(1))
      testSameInstance[ReadableProperty[Int], Rx[Int]](Property(1).transform(identity))
      testSameInstance[SeqProperty[Int], Var[Seq[Int]]](SeqProperty(Seq(1, 2, 3)))
      testSameInstance[ReadableSeqProperty[Int], Rx[Seq[Int]]](SeqProperty(Seq(1, 2, 3)).transform(identity))
    }
  }

  private def testSameInstance[T1 <: AnyRef, T2 <: AnyRef](obj: T1)(implicit ev: T1 => T2): Unit = {
    val obj1: T2 = obj
    val obj2: T2 = obj
    obj1 should be theSameInstanceAs obj2
  }

}
