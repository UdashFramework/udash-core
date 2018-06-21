package io.udash.properties

import com.avsystem.commons._
import com.github.ghik.silencer.silent
import io.udash.properties.model.ModelProperty
import io.udash.properties.seq.{Patch, ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{CastableProperty, Property, ReadableProperty}
import io.udash.testing.UdashSharedTest
import io.udash.utils.Registration

import scala.collection.mutable
import scala.util.{Random, Try}

class PropertyTest extends UdashSharedTest {
  class C(val i: Int, val s: String) {
    var variable: Int = 7
    override def equals(obj: Any): Boolean = obj match {
      case self if self.asInstanceOf[AnyRef] eq this.asInstanceOf[AnyRef] => true
      case c: C => c.i == this.i && c.s == this.s
      case _ => false
    }
  }

  trait TT {
    def i: Int
    def s: Option[String]
    def t: ST

    override def equals(obj: scala.Any): Boolean = obj match {
      case tt: TT =>
        i == tt.i && s == tt.s && t == tt.t
      case _ => false
    }
  }
  object TT extends HasModelPropertyCreator[TT] {
    implicit val default: Blank[TT] = Blank.Simple(null)
  }

  trait ST {
    def c: C
    def s: Seq[Char]

    override def equals(obj: scala.Any): Boolean = obj match {
      case st: ST =>
        c == st.c && s == st.s
      case _ => false
    }
  }
  object ST extends HasModelPropertyCreator[ST]

  sealed trait T
  case object TO1 extends T
  case object TO2 extends T
  case class TC1(i: Int) extends T
  case class TC2(s: String) extends T

  def newTT(iv: Int, sv: Option[String], cv: C, ssv: Seq[Char]) = new TT {
    override def i: Int = iv
    override def s: Option[String] = sv
    override def t: ST = new ST {
      override def c: C = cv
      override def s: Seq[Char] = ssv
    }
  }

  def randTT() = newTT(Random.nextInt(20), Some(Random.nextString(5)), new C(Random.nextInt(20), Random.nextString(5)), Random.nextString(20))

  "Property" should {
    "update value" in {
      val p = Property[Int](5)
      val tp = Property[T](TO1)
      val cp = Property[C](new C(1, "asd"))

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
      tp.touch()
      tp.get should be(TO2)
      tp.set(TO2, force = true)
      tp.get should be(TO2)
      tp.set(TC1(12))
      tp.get should be(TC1(12))
      tp.set(TO2, force = true)
      tp.get should be(TO2)

      cp.get should be(new C(1, "asd"))
      cp.set(new C(12, "asd2"))
      cp.get should be(new C(12, "asd2"))
    }

    "fire listeners on value change" in {
      val values = mutable.ArrayBuffer[Any]()
      val oneTimeValues = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v
      val oneTimeListener = (v: Any) => oneTimeValues += v

      val p = Property[Int](5)
      val tp = Property[T](TO1)
      val cp = Property[C](new C(1, "asd"))

      p.listen(listener)
      tp.listen(listener)
      cp.listen(listener)

      p.listenOnce(oneTimeListener)
      tp.listenOnce(oneTimeListener)
      cp.listenOnce(oneTimeListener)

      p.set(7)
      p.set(-321)
      p.set(-321)
      p.set(-321, force = true)
      p.set(-321)
      p.touch()
      tp.set(TC1(12))
      tp.set(TC1(12), force = true)
      tp.set(TC1(12))
      tp.set(TO2)
      tp.touch()
      cp.set(new C(12, "asd2"))
      cp.touch()
      cp.set(new C(12, "asd2"), force = true)
      cp.set(new C(12, "asd3"), force = true)

      p.clearListeners()
      p.set(1)
      p.touch()

      values.size should be(12)
      values(0) should be(7)
      values(1) should be(-321)
      values(2) should be(-321)
      values(3) should be(-321)
      values(4) should be(TC1(12))
      values(5) should be(TC1(12))
      values(6) should be(TO2)
      values(7) should be(TO2)
      values(8) should be(new C(12, "asd2"))
      values(9) should be(new C(12, "asd2"))
      values(10) should be(new C(12, "asd2"))
      values(11) should be(new C(12, "asd3"))

      oneTimeValues.size should be(3)
      oneTimeValues(0) should be(7)
      oneTimeValues(1) should be(TC1(12))
      oneTimeValues(2) should be(new C(12, "asd2"))
    }

    "fire listener callback when registered with initUpdate flag" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v

      val p = Property[Int](5)
      val tp = Property[T](TO1)
      val cp = Property[C](new C(1, "asd"))

      p.listen(listener, initUpdate = true)
      tp.listen(listener, initUpdate = true)
      cp.listen(listener, initUpdate = true)

      p.set(7)
      tp.set(TC1(12))
      cp.set(new C(12, "asd2"))

      values.size should be(6)
      values(0) should be(5)
      values(1) should be(TO1)
      values(2) should be(new C(1, "asd"))
      values(3) should be(7)
      values(4) should be(TC1(12))
      values(5) should be(new C(12, "asd2"))
    }

    "transform and synchronize value" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v
      val oneTimeValues = mutable.ArrayBuffer[Any]()
      val oneTimeListener = (v: Any) => oneTimeValues += v

      val cp = Property[C](new C(1, "asd"))
      val tp = cp.transform[(T, T)](
        (c: C) => Tuple2(TC1(c.i), TC2(c.s)),
        (t: (T, T)) => t match {
          case (TC1(i), TC2(s)) => new C(i, s)
          case _ => new C(0, "")
        }
      )

      tp.listen(listener)
      cp.listen(listener)

      tp.listenOnce(oneTimeListener)
      cp.listenOnce(oneTimeListener)

      cp.get should be(new C(1, "asd"))
      tp.get should be(Tuple2(TC1(1), TC2("asd")))

      cp.set(new C(12, "asd2"))
      cp.get should be(new C(12, "asd2"))
      tp.get should be(Tuple2(TC1(12), TC2("asd2")))

      tp.set(Tuple2(TC1(-5), TC2("tp")))
      cp.get should be(new C(-5, "tp"))
      tp.get should be(Tuple2(TC1(-5), TC2("tp")))

      tp.set(Tuple2(TC1(-5), TC2("tp")))
      cp.get should be(new C(-5, "tp"))
      tp.get should be(Tuple2(TC1(-5), TC2("tp")))

      tp.touch()
      cp.get should be(new C(-5, "tp"))
      tp.get should be(Tuple2(TC1(-5), TC2("tp")))

      tp.set(Tuple2(TC1(-5), TC2("tp")), force = true)
      cp.get should be(new C(-5, "tp"))
      tp.get should be(Tuple2(TC1(-5), TC2("tp")))

      tp.clearListeners()
      tp.set(Tuple2(TC1(-12), TC2("tp")))

      tp.listen(listener)
      cp.listen(listener)
      tp.set(Tuple2(TC1(-13), TC2("tp")))

      cp.clearListeners()
      tp.set(Tuple2(TC1(-14), TC2("tp")))

      tp.listen(listener)
      cp.listen(listener)
      tp.set(Tuple2(TC1(-15), TC2("tp")))

      values.size should be(12)
      values should contain(new C(12, "asd2"))
      values should contain(Tuple2(TC1(12), TC2("asd2")))
      values should contain(Tuple2(TC1(-5), TC2("tp")))
      values should contain(new C(-5, "tp"))
      values should contain(Tuple2(TC1(-13), TC2("tp")))
      values should contain(new C(-13, "tp"))
      values should contain(Tuple2(TC1(-15), TC2("tp")))
      values should contain(new C(-15, "tp"))

      oneTimeValues.size should be(2)
      oneTimeValues should contain(new C(12, "asd2"))
      oneTimeValues should contain(Tuple2(TC1(12), TC2("asd2")))
    }

    "fire transform method when needed" in {
      var counter = 0
      val p = Property[Boolean](true)
      val t = p.transform { v =>
        counter += 1
        !v
      }

      t.get should be(false)
      counter should be(1)

      p.set(false)
      t.get should be(true)
      counter should be(2)

      p.set(false)
      t.get should be(true)
      counter should be(2)

      p.set(true)
      t.get should be(false)
      counter should be(3)
    }

    "fire transform method when needed (2)" in {
      var counter = 0
      var counter2 = 0
      val pageProperty = Property(1)
      val seenAllProperty = Property(false)

      val totalPagesProperty = seenAllProperty.transform(all => {
        counter += 1
        if (all) Some(pageProperty.get) else None
      })

      val lastPageProperty = totalPagesProperty.combine(pageProperty) { (total, page) =>
        counter2 += 1
        total.exists(_ <= page)
      }

      counter should be(0)
      counter2 should be(0)

      pageProperty.set(1)
      counter should be(0)
      counter2 should be(0)

      pageProperty.set(2)
      counter should be(0)
      counter2 should be(0)

      pageProperty.set(3)
      counter should be(0)
      counter2 should be(0)

      totalPagesProperty.get should be(None)
      counter should be(1)
      counter2 should be(0)

      seenAllProperty.set(true)
      counter should be(1)
      counter2 should be(0)

      totalPagesProperty.get should be(Some(3))
      counter should be(2)
      counter2 should be(0)

      lastPageProperty.get should be(true)
      counter should be(2)
      counter2 should be(1)
    }

    "combine with other properties (single properties)" in {
      val p1 = Property(1)
      val p2 = Property(2)

      val sum = p1.combine(p2)(_ + _)
      val mul = p1.combine(p2)(_ * _)

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.get should be(3)
      mul.get should be(2)

      p1.set(12)

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.get should be(14)
      mul.get should be(24)

      var sumCallbackValue = 0
      var mulCallbackValue = 0
      val r1 = sum.listen(sumCallbackValue = _)
      val r2 = mul.listen(mulCallbackValue = _)
      p1.listenersCount() should be(2)
      p2.listenersCount() should be(2)

      p2.set(-2)

      sumCallbackValue should be(10)
      mulCallbackValue should be(-24)
      sum.get should be(10)
      mul.get should be(-24)

      p2.touch()

      sum.get should be(10)
      mul.get should be(-24)

      p1.listenersCount() should be(2)
      p2.listenersCount() should be(2)
      r1.cancel()
      p1.listenersCount() should be(1)
      p2.listenersCount() should be(1)
      r2.cancel()
      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
    }

    "combine with other properties (model properties)" in {
      trait M {
        def x: Double
        def y: Double
      }
      object M extends HasModelPropertyCreator[M] {
        def apply(_x: Double, _y: Double): M =
          new M {
            override def x: Double = _x
            override def y: Double = _y
          }
      }

      val p1 = Property(12)
      val p2 = Property(-2)

      val sum = p1.combine(p2)(_ + _)
      val m = ModelProperty[M](M(0.5, 0.3))

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)

      val mxc = sum.combine(m)(_ * _.x)
      val myc = sum.combine(m.subProp(_.y))(_ * _)

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.listenersCount() should be(0)
      m.listenersCount() should be(0)

      // sum.get == 10
      mxc.get should be(5.0)
      myc.get should be(3.0)

      var mxcChanges = 0
      var mycChanges = 0
      var mycChangesWithInit = 0
      val r1 = mxc.listen(_ => mxcChanges += 1)
      val r2 = myc.listen(_ => mycChanges += 1)
      val r3 = myc.listen(_ => mycChangesWithInit += 1, initUpdate = true)

      p1.listenersCount() should be(1)
      p2.listenersCount() should be(1)
      sum.listenersCount() should be(2)
      m.listenersCount() should be(1)
      m.subProp(_.x).listenersCount() should be(0)
      m.subProp(_.y).listenersCount() should be(1)

      CallbackSequencer().sequence {
        m.subProp(_.x).set(0.2)
        m.subProp(_.y).set(0.1)
      }

      // sum.get == 10
      mxc.get should be(2.0)
      myc.get should be(1.0)
      mxcChanges should be(1)
      mycChanges should be(1)
      mycChangesWithInit should be(2)

      p1.listenersCount() should be(1)
      p2.listenersCount() should be(1)
      sum.listenersCount() should be(2)
      m.listenersCount() should be(1)
      m.subProp(_.x).listenersCount() should be(0)
      m.subProp(_.y).listenersCount() should be(1)

      r1.cancel()
      r2.cancel()
      r3.cancel()

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.listenersCount() should be(0)
      m.listenersCount() should be(0)
      m.subProp(_.x).listenersCount() should be(0)
      m.subProp(_.y).listenersCount() should be(0)
    }

    "combine with other properties (seq properties)" in {
      val p1 = Property(12)
      val p2 = Property(-2)

      val sum = p1.combine(p2)(_ + _)
      val s = SeqProperty(1, 2, 3, 4)

      val sc = sum.combine(s)((m, items) => items.map(_ * m))
      val sqc = s.combine(sum)(_ * _)

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.listenersCount() should be(0)
      s.listenersCount() should be(0)

      // sum.get == 10
      sc.get should be(Seq(10, 20, 30, 40))
      sqc.get should be(Seq(10, 20, 30, 40))

      var sqcHeadChanges = 0
      val r1 = sqc.elemProperties.head.listen(_ => sqcHeadChanges += 1)

      p1.listenersCount() should be(1)
      p2.listenersCount() should be(1)
      sum.listenersCount() should be(1)
      s.listenersCount() should be(0)

      var sqcChanges = 0
      val r2 = sqc.listen(_ => sqcChanges += 1)

      p1.listenersCount() should be(1)
      p2.listenersCount() should be(1)
      sum.listenersCount() should be(2)
      s.listenersCount() should be(1)

      s.replace(1, 2, 7, 8, 9)

      // sum.get == 10
      sc.get should be(Seq(10, 70, 80, 90, 40))
      sqc.get should be(Seq(10, 70, 80, 90, 40))

      sqcHeadChanges should be(0)
      sqcChanges should be(1)

      p1.set(0)
      p2.set(0)

      sqcHeadChanges should be(2)
      sqcChanges should be(3)

      // sum.get == 0
      sc.get should be(Seq(0, 0, 0, 0, 0))
      sqc.get should be(Seq(0, 0, 0, 0, 0))

      r1.cancel()
      r2.cancel()

      p1.listenersCount() should be(0)
      p2.listenersCount() should be(0)
      sum.listenersCount() should be(0)
      s.listenersCount() should be(0)
    }

    "transform to ReadableSeqProperty" in {
      val elemListeners = mutable.Map.empty[PropertyId, Registration]
      var elementsUpdated = 0
      def registerElementListener(props: Seq[ReadableProperty[_]]) =
        props.foreach { p =>
          elemListeners(p.id) = p.listen(_ => elementsUpdated += 1)
        }

      val p = Property("1,2,3,4,5")
      val s: ReadableSeqProperty[Int, ReadableProperty[Int]] =
        p.transformToSeq((v: String) => Try(v.split(",").map(_.toInt).toSeq).getOrElse(Seq[Int]()))

      p.listenersCount() should be(0)

      registerElementListener(s.elemProperties)
      p.listenersCount() should be(1)

      var lastValue: Seq[Int] = null
      var lastPatch: Patch[ReadableProperty[Int]] = null
      val r1 = s.listen(lastValue = _)
      val r2 = s.listenStructure { p =>
        registerElementListener(p.added)
        p.removed.foreach { p =>
          elemListeners(p.id).cancel()
        }
        lastPatch = p
      }

      p.listenersCount() should be(1)
      s.get should be(Seq(1, 2, 3, 4, 5))

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("5,4,3")
      s.get should be(Seq(5, 4, 3))
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(2)
      elementsUpdated should be(2)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("5,4,3,2")
      s.get should be(Seq(5, 4, 3, 2))
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("6,5,4,3,2")
      s.get should be(Seq(6, 5, 4, 3, 2))
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("6,5,7,4,3,2")
      s.get should be(Seq(6, 5, 7, 4, 3, 2))
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("-1,-2,-3")
      s.get should be(Seq(-1, -2, -3))
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(3)
      elementsUpdated should be(3)

      //TODO: Does it make sense to use LCCS?
      //It could use two patches here
      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("0,-1,-2,-3,-4")
      s.get should be(Seq(0, -1, -2, -3, -4))
      lastValue should be(s.get)
      lastPatch.added.size should be(2)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(3) // could be 0 with LCCS

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("0,-1,-3,-4")
      s.get should be(Seq(0, -1, -3, -4))
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(1)
      elementsUpdated should be(0)

      p.set("-1,-2,-3")

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("")
      s.get should be(Seq())
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(3)
      elementsUpdated should be(0)

      p.set("1,0,1,0,1")
      s.get should be(Seq(1,0,1,0,1))

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("1,0,1,0,1,0,1")
      s.get should be(Seq(1,0,1,0,1,0,1))
      lastValue should be(s.get)
      lastPatch.added.size should be(2)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("1,0,1,0,1")
      s.get should be(Seq(1,0,1,0,1))
      lastValue should be(s.get)
      lastPatch.idx should be(5)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(2)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.touch()
      s.get should be(Seq(1,0,1,0,1))
      lastValue should be(s.get)
      lastPatch should be(null)
      elementsUpdated should be(0)

      r1.cancel()
      r2.cancel()

      p.listenersCount() should be(1)

      elemListeners.foreach(_._2.cancel())

      p.listenersCount() should be(0)

      p.set("1,2,3")
      s.get should be(Seq(1,2,3))
      p.set("1,2,3,-1,-2,-3")
      s.get should be(Seq(1,2,3,-1,-2,-3))
    }

    "not allow children modification after transformation into ReadableSeqProperty" in {
      val p = Property("1,2,3,4,5")
      val s: ReadableSeqProperty[Int, ReadableProperty[Int]] =
        p.transformToSeq((v: String) => Try(v.split(",").map(_.toInt).toSeq).getOrElse(Seq[Int]()))

      s.elemProperties.foreach {
        case p: Property[Int] => p.set(20)
        case _: ReadableProperty[Int] => //ignore
      }

      s.get should be(Seq(1,2,3,4,5))
    }

    "transform to SeqProperty" in {
      val elemListeners = mutable.Map.empty[PropertyId, Registration]
      var elementsUpdated = 0
      def registerElementListener(props: Seq[ReadableProperty[_]]): Unit =
        props.foreach { p =>
          elemListeners(p.id) = p.listen(_ => elementsUpdated += 1)
        }

      val p = Property("1,2,3,4,5")
      val s: SeqProperty[Int, Property[Int]] = p.transformToSeq(
        (v: String) => Try(v.split(",").map(_.toInt).toSeq).getOrElse(Seq[Int]()),
        (s: Seq[Int]) => s.mkString(",")
      )

      p.listenersCount() should be(0)
      registerElementListener(s.elemProperties)
      p.listenersCount() should be(1)

      var lastValue: Seq[Int] = null
      var lastPatch: Patch[ReadableProperty[Int]] = null
      val r1 = s.listen(lastValue = _)
      val r2 = s.listenStructure(p => {
        registerElementListener(p.added)
        p.removed.foreach { p =>
          elemListeners(p.id).cancel()
        }
        lastPatch = p
      })

      s.get should be(Seq(1, 2, 3, 4, 5))
      p.listenersCount() should be(1)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.set(Seq(5, 4, 3))
      p.get should be("5,4,3")
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(2)
      elementsUpdated should be(2)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.append(2)
      p.get should be("5,4,3,2")
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.prepend(6)
      p.get should be("6,5,4,3,2")
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.insert(2, 7)
      p.get should be("6,5,7,4,3,2")
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.set(Seq(-1, -2, -3))
      p.get should be("-1,-2,-3")
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(3)
      elementsUpdated should be(3)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.prepend(0)
      s.append(-4)
      p.get should be("0,-1,-2,-3,-4")
      lastValue should be(s.get)
      lastPatch.added.size should be(1)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.remove(2, 1)
      p.get should be("0,-1,-3,-4")
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(1)
      elementsUpdated should be(0)

      p.set("-1,-2,-3")

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.set(Seq())
      p.get should be("")
      lastValue should be(s.get)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(3)
      elementsUpdated should be(0)

      s.set(Seq(1,0,1,0,1))
      p.get should be("1,0,1,0,1")

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.append(0, 1)
      p.get should be("1,0,1,0,1,0,1")
      lastValue should be(s.get)
      lastPatch.added.size should be(2)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.set(Seq(1,0,1,0,1))
      p.get should be("1,0,1,0,1")
      lastValue should be(s.get)
      lastPatch.idx should be(5)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(2)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      s.touch()
      p.get should be("1,0,1,0,1")
      lastValue should be(s.get)
      lastPatch should be(null)
      elementsUpdated should be(0)

      r1.cancel()
      p.listenersCount() should be(1)
      r2.cancel()
      p.listenersCount() should be(1)
      elemListeners.foreach(_._2.cancel())
      p.listenersCount() should be(0)
    }

    "handle child modification in transformToSeq result" in {
      val s = Property("1,2,3,4,5,6")
      val i = s.transformToSeq(_.split(",").map(_.toInt), (v: Seq[Int]) => v.map(_.toString).mkString(","))

      var counter = 0
      s.listen(_ => counter += 1)

      i.append(7)

      s.get should be("1,2,3,4,5,6,7")
      i.get should be(Seq(1,2,3,4,5,6,7))
      counter should be(1)

      CallbackSequencer().sequence {
        i.elemProperties.foreach(_.set(12))
      }

      s.get should be("12,12,12,12,12,12,12")
      i.get should be(Seq(12,12,12,12,12,12,12))
      counter should be(2)

      i.elemProperties.foreach(_.set(1))

      s.get should be("1,1,1,1,1,1,1")
      i.get should be(Seq(1,1,1,1,1,1,1))
      counter should be(9)
    }

    "stream value to another property" in {
      val source = SeqProperty(1, 2, 3)
      val transformed = source.transform((i: Int) => i * 2)
      val filtered = transformed.filter(_ < 10)
      val sum = filtered.transform((s: Seq[Int]) => s.sum)

      val target = Property(42)
      val targetWithoutInit = Property(42)

      target.get should be(42)
      targetWithoutInit.get should be(42)

      // Init update
      val r1 = sum.streamTo(target)(id => id)
      val r2 = sum.streamTo(targetWithoutInit, initUpdate = false)(id => id * 2)

      target.get should be(12)
      targetWithoutInit.get should be(42)

      // Source change
      source.append(4)

      target.get should be(20)
      targetWithoutInit.get should be(40)

      // Source touch
      source.touch()

      target.get should be(20)
      targetWithoutInit.get should be(40)

      // Targets update
      target.set(0)
      targetWithoutInit.set(0)

      target.get should be(0)
      targetWithoutInit.get should be(0)

      // Update filtered out
      source.append(5)

      target.get should be(0)
      targetWithoutInit.get should be(0)

      // Source update
      source.remove(2)

      target.get should be(16)
      targetWithoutInit.get should be(32)

      // Stream cancel and update
      r1.cancel()
      r2.cancel()
      source.remove(1)

      target.get should be(16)
      targetWithoutInit.get should be(32)

      // Restart streaming
      r1.restart()
      r2.restart()
      source.touch()

      target.get should be(14)
      targetWithoutInit.get should be(28)
    }

    "clear transformer listener" in {
      val p = Property(6)
      val t1 = p.transform(_ + 7)
      val t2 = p.transform(_ + 8)

      p.listenersCount() should be(0)
      t1.listenersCount() should be(0)
      t2.listenersCount() should be(0)

      val r = t1.listen(_ => ())
      t2.listenOnce(_ => ())

      p.listenersCount() should be(2)
      t1.listenersCount() should be(1)
      t2.listenersCount() should be(1)

      p.set(25)

      p.listenersCount() should be(1)

      r.cancel()

      p.listenersCount() should be(0)
    }

    "fire transform on empty property" in {
      val p = Property.empty[String]: @silent
      val t = p.transform(_ == null)

      t.get should be(true)

      p.set("asd")
      t.get should be(false)
    }

    "synchronize values with another property" in {
      val source = Property(1)
      val target = Property(5)

      source.get should be(1)
      target.get should be(5)

      //Init update
      val registration = source.sync(target)((i: Int) => i * 2, (i: Int) => i / 2)

      registration.isActive should be(true)
      source.get should be(1)
      target.get should be(2)

      // Source update
      source.set(2)

      source.get should be(2)
      target.get should be(4)

      // Source touch
      source.touch()

      source.get should be(2)
      target.get should be(4)

      // Target update
      target.set(8)

      source.get should be(4)
      target.get should be(8)

      // Source update
      source.set(2)

      source.get should be(2)
      target.get should be(4)

      // Registration cancel and source update
      registration.cancel()
      source.set(1)

      registration.isActive should be(false)
      source.get should be(1)
      target.get should be(4)

      // Target update
      target.set(1)

      source.get should be(1)
      target.get should be(1)

      // Restart streaming, source touch
      registration.restart()

      source.get should be(1)
      target.get should be(2)

      // Target update
      target.set(8)

      source.get should be(4)
      target.get should be(8)
    }

    "synchronize values with SeqProperty" in {
      val source = Property(1)
      val target = SeqProperty[Int](1, 2)

      source.get should be(1)
      target.get should be(Seq(1, 2))

      //Init update
      source.sync(target)((i: Int) => 1 to i, (s: Seq[Int]) => s.length)

      source.get should be(1)
      target.get should be(Seq(1))

      // Source update
      source.set(5)

      source.get should be(5)
      target.get should be(Seq(1, 2, 3, 4, 5))

      // Target update
      target.set(Seq(1, 2, 3, 4))

      source.get should be(4)
      target.get should be(Seq(1, 2, 3, 4))
    }
  }

  "ModelProperty" should {
    "update value and provide access to subproperties" in {
      val p = ModelProperty.empty[TT]: @silent

      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))

      p.get.i should be(5)
      p.get.s should be(Some("s"))
      p.get.t.c should be(new C(123, "asd"))
      p.get.t.s.size should be(3)

      p.subProp(_.i).set(42)
      p.get.i should be(42)

      p.subModel(_.t).subSeq(_.s).insert(0, 'e')
      p.get.t.s.size should be(4)

      p.subSeq(_.t.s).insert(0, 'f')
      p.get.t.s.size should be(5)

      p.subProp(_.t.c).set(new C(321, "dsa"))
      p.get.t.c should be(new C(321, "dsa"))

      p.touch()
      p.get.i should be(42)
      p.get.s should be(Some("s"))
      p.get.t.s.size should be(5)
      p.get.t.c should be(new C(321, "dsa"))
    }

    "fire listeners on value change" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v
      val oneTimeValues = mutable.ArrayBuffer[Any]()
      val oneTimeListener = (v: Any) => oneTimeValues += v

      val p = ModelProperty.empty[TT]: @silent
      p.listen(listener, initUpdate = true)
      p.listenOnce(oneTimeListener)

      values.size should be(1)
      values.clear()

      val init = newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c'))
      p.setInitValue(newTT(123123, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))
      values.size should be(0)
      p.set(init)
      values.size should be(1)

      p.subProp(_.i).set(42)
      p.subProp(_.i).set(42)
      p.subProp(_.i).set(42)
      values.size should be(2)

      p.subModel(_.t).subSeq(_.s).insert(0, 'e')
      values.size should be(3)

      p.subSeq(_.t.s).insert(0, 'f')
      values.size should be(4)

      p.subProp(_.t.c).set(new C(321, "dsa"))
      values.size should be(5)

      CallbackSequencer().sequence {
        p.subSeq(_.t.s).insert(0, 'g')
        p.subSeq(_.t.s).insert(0, 'h')
        p.subSeq(_.t.s).insert(0, 'i')
        p.subProp(_.i).set(123)
      }
      values.size should be(6)

      p.touch()
      values.size should be(7)

      p.subSeq(_.t.s).touch()
      values.size should be(8)

      p.subProp(_.s).touch()
      values.size should be(9)

      p.subModel(_.t).touch()
      values.size should be(10)

      p.subProp(_.t.s).set("qweasd")
      values.size should be(11)

      p.subProp(_.t.s).set("asd2", force = true)
      values.size should be(12)

      p.subProp(_.t.s).set("asd2", force = true)
      values.size should be(13)

      p.subProp(_.t.s).set("asd2")
      values.size should be(13)

      p.clearListeners()
      p.subModel(_.t).touch()
      p.subProp(_.t.s).set("qwerty")
      values.size should be(13)

      oneTimeValues.size should be(1)
      oneTimeValues.head should be(init)
    }

    "transform and synchronize value" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v

      val p = ModelProperty.empty[TT]: @silent
      val t = p.transform[Int](
        (p: TT) => p.i + p.t.c.i,
        (x: Int) => newTT(x/2, None, new C(x/2, ""), Seq.empty)
      )

      p.listen(listener)
      t.listen(listener)

      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))
      t.get should be(123+5)

      t.set(64)
      p.get.i should be(32)
      p.get.t.c.i should be(32)

      t.touch()
      p.get.i should be(32)
      p.get.t.c.i should be(32)

      values.size should be(6)
      values should contain(64)
      values should contain(128)
    }

    "work with simple case class" in {
      case class Simple(i: Int, s:  String)
      implicit val propertyCreator: ModelPropertyCreator[Simple] = MacroModelPropertyCreator.materialize[Simple].pc

      val p = ModelProperty(Simple(1, "xxx"))
      p.get should be(Simple(1, "xxx"))
      val i = p.subProp(_.i)
      i.set(5)
      val s = p.subProp(_.s)
      s.set("asd")
      p.get should be(Simple(5, "asd"))
    }

    "work with tuples" in {
      val init = (123, "sth", true, new C(42, "s"))
      val p = ModelProperty(init)

      var changeCount = 0
      p.listen(_ => changeCount += 1)

      p.get should be(init)
      p.subProp(_._1).set(333)
      p.subProp(_._2).set("sth2")
      p.subProp(_._3).set(false)
      p.subProp(_._4).set(new C(24, "s2"))
      p.get should be((333, "sth2", false, new C(24, "s2")))
      changeCount should be(4)
    }

    "work with Tuple2" in {
      val init = (123, "sth")
      val p = ModelProperty(init)

      var changeCount = 0
      p.listen(_ => changeCount += 1)

      p.get should be(init)
      p.subProp(_._1).set(333)
      p.subProp(_._2).set("sth2")
      p.get should be((333, "sth2"))
      changeCount should be(2)
    }

    "work with recursive case class" in {
      import ReqModels._

      val p = ModelProperty(Simple(1, null))
      p.get should be(Simple(1, null))
      val i = p.subProp(_.i)
      i.set(5)
      val s = p.subModel(_.s)
      s.set(Simple(2, Simple(3, null)))
      p.get should be(Simple(5, Simple(2, Simple(3, null))))
      s.subProp(_.i).get should be(2)
      s.subProp(_.s.i).get should be(3)
    }

    "work with recursive trait" in {
      import ReqModels._

      val p = ModelProperty[ReqT](new ReqT { def t: ReqT = null })
      p.get.t should be(null)
      val s = p.subModel(_.t)
      s.set(new ReqT { def t: ReqT = null })
      s.get.t should be(null)
      p.get.t shouldNot be(null)
    }

    "work with recursive case class containing Seq" in {
      import ReqModels._

      val p = ModelProperty(SimpleSeq(Seq[SimpleSeq](), null))
      p.get should be(SimpleSeq(Seq(), null))
      val i = p.subSeq(_.i)
      i.set(Seq(SimpleSeq(Seq(), SimpleSeq(Seq(SimpleSeq(Seq(), null)), null))))
      val s = p.subModel(_.s)
      s.set(SimpleSeq(Seq(), SimpleSeq(Seq(), null)))
      p.get should be(SimpleSeq(Seq(SimpleSeq(Seq(), SimpleSeq(Seq(SimpleSeq(Seq(), null)), null))), SimpleSeq(Seq(), SimpleSeq(Seq(), null))))
      s.subProp(_.i).get should be(Seq())
      s.subProp(_.s.i).get should be(Seq())
      i.elemProperties.isEmpty should be(false)
    }

    "not get partial value from child property" in {
      case class CCWithRequire(a: Int, b: Int) {
        require((a > 0 && b > 0) || (a < 0 && b < 0))
      }
      implicit val propertyCreatorCC: ModelPropertyCreator[CCWithRequire] = MacroModelPropertyCreator.materialize[CCWithRequire].pc
      case class TopModel(child: CCWithRequire)
      implicit val propertyCreator: ModelPropertyCreator[TopModel] = MacroModelPropertyCreator.materialize[TopModel].pc

      val p = ModelProperty[TopModel](TopModel(CCWithRequire(1, 2)))
      val c = p.subModel(_.child)

      c.set(CCWithRequire(-10, -5))

      c.get.a should be(-10)
      c.get.b should be(-5)

      c.touch()

      c.get.a should be(-10)
      c.get.b should be(-5)
    }

    "handle trait with implemented defs and vals" in {
      trait ModelWithImplDef {
        def x: Int
        def y: Int = 5
      }
      implicit val propertyCreator: ModelPropertyCreator[ModelWithImplDef] = MacroModelPropertyCreator.materialize[ModelWithImplDef].pc
      trait ModelWithImplVal {
        val x: Int
        val y: Int = 5
      }
      implicit val propertyCreatorVal: ModelPropertyCreator[ModelWithImplVal] = MacroModelPropertyCreator.materialize[ModelWithImplVal].pc

      val p1 = ModelProperty.empty[ModelWithImplDef]: @silent
      val p2 = ModelProperty.empty[ModelWithImplVal]: @silent

      p1.subProp(_.x).set(12)
      p1.subProp(_.x).get should be(12)
      p1.get.x should be(12)
      p1.get.y should be(5)

      p2.subProp(_.x).set(12)
      p2.subProp(_.x).get should be(12)
      p2.get.x should be(12)
      p2.get.y should be(5)
    }

    "handle case class with implemented defs and vals" in {
      trait Utils {
        val userLabel: String = "User:"
      }
      case class User(login: String, name: Option[String]) extends Utils {
        val displayName: String = name.getOrElse(login)

        def withLabel: String =
          s"$userLabel $displayName"
      }
      implicit val propertyCreator: ModelPropertyCreator[User] = MacroModelPropertyCreator.materialize[User].pc

      val p = ModelProperty[User](User("udash", Some("Udash Framework")))
      p.get.withLabel should be("User: Udash Framework")

      p.subProp(_.name).set(None)
      p.get.withLabel should be("User: udash")

      p.subProp(_.login).set("tester")
      p.get.withLabel should be("User: tester")

      p.subProp(_.name).set(Some("Test Test"))
      p.get.withLabel should be("User: Test Test")
    }

    "handle empty model property after subProp call" in {
      case class SubTest(x: Int)
      implicit val propertyCreatorSub: ModelPropertyCreator[SubTest] = MacroModelPropertyCreator.materialize[SubTest].pc
      case class Test(a: SubTest, s: SubTest)
      implicit val propertyCreator: ModelPropertyCreator[Test] = MacroModelPropertyCreator.materialize[Test].pc

      val p = ModelProperty.empty[Test]: @silent
      val sub = p.subModel(_.s)

      p.get should be(null)
      sub.get should be(null)

      sub.subProp(_.x).set(7)

      p.get should be(Test(null, SubTest(7)))
      sub.get should be(SubTest(7))
    }

    "handle empty model property after subProp call (trait version)" in {
      trait SubTest {
        def x: Int
      }
      implicit val propertyCreatorSub: ModelPropertyCreator[SubTest] = MacroModelPropertyCreator.materialize[SubTest].pc
      trait Test {
        def a: SubTest
        def s: SubTest
      }
      implicit val propertyCreator: ModelPropertyCreator[Test] = MacroModelPropertyCreator.materialize[Test].pc

      val p = ModelProperty.empty[Test]: @silent
      val sub = p.subModel(_.s)

      p.get should be(null)
      sub.get should be(null)

      sub.subProp(_.x).set(7)

      p.get.a should be(null)
      p.get.s.x should be(7)
      sub.get.x should be(7)
    }

    "handle generic types" in {
      object Outer {
        case class Bla[Type](x: Int, s: String, t: Type)
        object Bla {
          implicit def pc[Type: PropertyCreator]: ModelPropertyCreator[Bla[Type]] =
            ModelPropertyCreator.materialize[Bla[Type]]
        }
      }

      def create[A : PropertyCreator, B : PropertyCreator, D : PropertyCreator](a: A, b: B, d: D): SeqProperty[(A, B, D), CastableProperty[(A, B, D)]] =
        SeqProperty(Seq.tabulate(10)(_ => (a, b, d)))

      val s = create(Outer.Bla(5, "asd2", Outer.Bla(7, "qwe", 1)), 8, "asd")
      s.elemProperties.foreach { v =>
        val p = v.asModel
        p.subProp(_._1.x).get should be(5)
        p.subProp(_._1.s).get should be("asd2")
        p.subProp(_._1.t.x).get should be(7)
        p.subProp(_._1.t.s).get should be("qwe")
        p.subProp(_._1.t.t).get should be(1)
        p.subProp(_._2).get should be(8)
        p.subProp(_._3).get should be("asd")
      }
    }

    "cache subproperties" in {
      val p = ModelProperty.empty[TT]: @silent
      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))

      p.subProp(_.i) should be theSameInstanceAs p.subProp(_.i)
      p.subProp(_.s) should be theSameInstanceAs p.subProp(_.s)
      p.subModel(_.t) should be theSameInstanceAs p.subModel(_.t)
      p.subModel(_.t) should be theSameInstanceAs p.subProp(_.t)
      p.subSeq(_.t.s) should be theSameInstanceAs p.subSeq(_.t.s)
      p.subSeq(_.t.s) should be theSameInstanceAs p.subProp(_.t.s)
    }
  }

  "SeqProperty" should {
    "handle sequence of properties" in {
      val p = SeqProperty[Int](Seq(1,2,3))
      val pt = SeqProperty[T](TO1, TC1(5), TO2)
      val ptt = SeqProperty[TT](randTT(), randTT(), randTT())

      def checkProperties(expectedSize: Int, props: Seq[SeqProperty[_, Property[_]]] = Seq(p, pt, ptt)) = {
        props.foreach(p => {
          p.get.size should be(expectedSize)
          p.get should be(p.elemProperties.map(_.get))
        })
      }

      checkProperties(expectedSize = 3)

      // Replace part
      var nextTT = randTT()
      p.replace(0, 2, 0)
      pt.replace(0, 2, TO2)
      ptt.replace(0, 2, nextTT)

      checkProperties(expectedSize = 2)
      p.get.head should be(0)
      pt.get.head should be(TO2)
      ptt.get.head.i should be(nextTT.i)
      ptt.get.head.s should be(nextTT.s)
      ptt.get.head.t.c should be(nextTT.t.c)

      // Replace whole
      nextTT = randTT()
      p.replace(0, 2, 1, 2, 3, 4)
      pt.replace(0, 2, TO1, TC1(1), TC2("asd"), TO2)
      ptt.replace(0, 2, nextTT, randTT(), randTT(), randTT())

      checkProperties(expectedSize = 4)
      p.get.head should be(1)
      pt.get.head should be(TO1)
      pt.get.head should be(TO1)
      ptt.get.head.i should be(nextTT.i)
      ptt.get.head.s should be(nextTT.s)
      ptt.get.head.t.c should be(nextTT.t.c)

      // Insert at the beginning
      nextTT = randTT()
      p.insert(0, 2, 3, 4)
      pt.insert(0, TC1(1), TC2("asd"), TO2)
      ptt.insert(0, nextTT, randTT(), randTT())

      checkProperties(expectedSize = 7)
      p.get.head should be(2)
      pt.get.head should be(TC1(1))
      ptt.get.head.i should be(nextTT.i)
      ptt.get.head.s should be(nextTT.s)
      ptt.get.head.t.c should be(nextTT.t.c)

      // Insert inside
      nextTT = randTT()
      p.insert(3, 5, 3)
      pt.insert(3, TC2("asd"), TO2)
      ptt.insert(3, nextTT, randTT())

      checkProperties(expectedSize = 9)
      p.get(3) should be(5)
      pt.get(3) should be(TC2("asd"))
      ptt.get(3).i should be(nextTT.i)
      ptt.get(3).s should be(nextTT.s)
      ptt.get(3).t.c should be(nextTT.t.c)

      // Insert at the end
      nextTT = randTT()
      p.insert(9, 5, 3)
      pt.insert(9, TC2("asd"), TO2)
      ptt.insert(9, nextTT, randTT())

      checkProperties(expectedSize = 11)
      p.get(9) should be(5)
      pt.get(9) should be(TC2("asd"))
      ptt.get(9).i should be(nextTT.i)
      ptt.get(9).s should be(nextTT.s)
      ptt.get(9).t.c should be(nextTT.t.c)

      // Remove by indexes
      p.remove(1, 8)
      pt.remove(1, 8)
      ptt.remove(1, 8)

      checkProperties(expectedSize = 3)
      p.get(1) should be(5)
      pt.get(1) should be(TC2("asd"))
      ptt.get(1).i should be(nextTT.i)
      ptt.get(1).s should be(nextTT.s)
      ptt.get(1).t.c should be(nextTT.t.c)

      // Remove value
      p.remove(5)
      pt.remove(TC2("asd"))
      ptt.remove(ptt.elemProperties(1).get)

      checkProperties(expectedSize = 2)
      p.get(1) should be(3)
      pt.get(1) should be(TO2)

      // Prepend
      nextTT = randTT()
      p.prepend(2, 3, 4)
      pt.prepend(TC1(1), TC2("asd"), TO2)
      ptt.prepend(nextTT, randTT(), randTT())

      checkProperties(expectedSize = 5)
      p.get.head should be(2)
      pt.get.head should be(TC1(1))
      ptt.get.head.i should be(nextTT.i)
      ptt.get.head.s should be(nextTT.s)
      ptt.get.head.t.c should be(nextTT.t.c)

      // Append
      nextTT = randTT()
      p.append(5, 3)
      pt.append(TC2("asd"), TO2)
      ptt.append(nextTT, randTT())

      checkProperties(expectedSize = 7)
      p.get(5) should be(5)
      pt.get(5) should be(TC2("asd"))
      ptt.get(5).i should be(nextTT.i)
      ptt.get(5).s should be(nextTT.s)
      ptt.get(5).t.c should be(nextTT.t.c)

      p.touch()
      pt.touch()
      ptt.touch()

      checkProperties(expectedSize = 7)
      p.get(5) should be(5)
      pt.get(5) should be(TC2("asd"))
      ptt.get(5).i should be(nextTT.i)
      ptt.get(5).s should be(nextTT.s)
      ptt.get(5).t.c should be(nextTT.t.c)
    }

    "handle null value as empty Seq" in {
      val p = SeqProperty[Int](1,2,3)
      p.set(null)
      p.get.size should be(0)
      p.touch()
      p.get.size should be(0)
    }

    "fire value listeners on structure change" in {
      val p = SeqProperty[Int](Seq(0, 0, 0))

      val values = mutable.ArrayBuffer[Seq[Int]]()
      val listener = (s: Seq[Int]) => values += s
      val oneTimeValues = mutable.ArrayBuffer[Seq[Int]]()
      val oneTImeListener = (s: Seq[Int]) => oneTimeValues += s

      p.listen(listener, initUpdate = true)
      p.listenOnce(oneTImeListener)

      values.size should be(1)
      values.clear()

      p.set(Seq(1,2,3))
      values.size should be(1)
      values.last should be(Seq(1, 2, 3))

      p.replace(1, 2, 5, 6)
      values.size should be(2)
      values.last should be(Seq(1, 5, 6))

      p.insert(3, 7, 7)
      values.size should be(3)
      values.last should be(Seq(1, 5, 6, 7, 7))

      p.remove(1, 2)
      values.size should be(4)
      values.last should be(Seq(1, 7, 7))

      p.remove(7)
      values.size should be(5)
      values.last should be(Seq(1, 7))

      p.prepend(1, 2)
      values.size should be(6)
      values.last should be(Seq(1, 2, 1, 7))

      p.append(1, 2)
      values.size should be(7)
      values.last should be(Seq(1, 2, 1, 7, 1, 2))

      p.touch()
      values.size should be(8)
      values.last should be(Seq(1, 2, 1, 7, 1, 2))

      p.clearListeners()
      p.touch()
      p.remove(7)
      values.size should be(8)

      oneTimeValues.size should be(1)
      oneTimeValues.last should be(Seq(1, 2, 3))
    }

    "fire value listeners on every child change" in {
      val p = SeqProperty.empty[Int]: @silent

      val values = mutable.ArrayBuffer[Seq[Int]]()
      val listener = (s: Seq[Int]) => values += s

      p.listen(listener)

      p.set(Seq(1,2,3))
      values.size should be(1)
      values.last should be(Seq(1, 2, 3))

      p.elemProperties(1).set(5)
      values.size should be(2)
      values.last should be(Seq(1, 5, 3))

      p.elemProperties(0).set(8)
      values.size should be(3)
      values.last should be(Seq(8, 5, 3))

      p.elemProperties(2).set(1)
      values.size should be(4)
      values.last should be(Seq(8, 5, 1))

      p.elemProperties(2).touch()
      values.size should be(5)
      values.last should be(Seq(8, 5, 1))
    }

    "fire structure listeners on structure change" in {
      val p = SeqProperty.empty[Int]: @silent

      val patches = mutable.ArrayBuffer[Patch[Property[Int]]]()
      val listener = (s: Patch[Property[Int]]) => patches += s

      p.listenStructure(listener)

      p.set(Seq(1,2,3))
      patches.size should be(1)
      patches.last.idx should be(0)
      patches.last.added.size should be(3)
      patches.last.removed.size should be(0)

      p.replace(1, 2, 5, 6)
      patches.size should be(2)
      patches.last.idx should be(1)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(2)

      p.insert(3, 7, 7)
      patches.size should be(3)
      patches.last.idx should be(3)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(0)

      p.remove(1, 2)
      patches.size should be(4)
      patches.last.idx should be(1)
      patches.last.added.size should be(0)
      patches.last.removed.size should be(2)

      p.remove(7)
      patches.size should be(5)
      patches.last.idx should be(1)
      patches.last.added.size should be(0)
      patches.last.removed.size should be(1)

      p.prepend(1, 2)
      patches.size should be(6)
      patches.last.idx should be(0)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(0)

      p.append(1, 2)
      patches.size should be(7)
      patches.last.idx should be(4)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(0)

      p.touch()
      patches.size should be(8)
      patches.last.idx should be(0)
      patches.last.added.size should be(6)
      patches.last.removed.size should be(6)

      p.clearListeners()
      p.touch()
      p.append(1, 2)
      patches.size should be(8)
    }

    "not fire structure listeners on child change" in {
      val p = SeqProperty.empty[Int]: @silent

      val patches = mutable.ArrayBuffer[Patch[Property[Int]]]()
      val listener = (s: Patch[Property[Int]]) => patches += s

      p.listenStructure(listener)

      p.set(Seq(1,2,3))
      patches.size should be(1)
      p.elemProperties(1).set(5)
      patches.size should be(1)
      p.elemProperties(0).set(8)
      patches.size should be(1)
      p.elemProperties(2).set(1)
      patches.size should be(1)
    }

    "transform into Property" in {
      val p = SeqProperty[Int](1, 2, 3)
      val t = p.transform[Int](
        (s: Seq[Int]) => s.sum,
        (i: Int) => (1 to i).toSeq
      )

      p.get should be(Seq(1, 2, 3))
      t.get should be(6)

      t.set(5)

      t.get should be(15) // Notice that `t.set(5)` sets original value to `Seq(1, 2, 3, 4, 5)` and `t.get` is equal 15, not 5!
      p.get should be(Seq(1, 2, 3, 4, 5))
      t.get should be(15)

      p.set(Seq(1, 2))

      p.get should be(Seq(1, 2))
      t.get should be(3)

      p.touch()

      p.get should be(Seq(1, 2))
      t.get should be(3)
    }

    "transform into another SeqProperty" in {
      val p = SeqProperty[Int](1, 2, 3)
      val t = p.transform[T](
        (i: Int) => TC1(i),
        (t: T) => t match {
          case TC1(i) => i
          case _: T => 0
        }
      )

      p.get should be(Seq(1, 2, 3))
      t.get should be(Seq(TC1(1), TC1(2), TC1(3)))

      t.prepend(TC1(0))

      p.get should be(Seq(0, 1, 2, 3))
      t.get should be(Seq(TC1(0), TC1(1), TC1(2), TC1(3)))

      p.append(4)

      p.get should be(Seq(0, 1, 2, 3, 4))
      t.get should be(Seq(TC1(0), TC1(1), TC1(2), TC1(3), TC1(4)))

      t.set(Seq(TO1, TO2))

      t.get should be(Seq(TC1(0), TC1(0))) // Again notice that you wont get inserted values, because they were transformed.
      p.get should be(Seq(0, 0))
      t.get should be(Seq(TC1(0), TC1(0)))
    }

    "return immutable sequence from get" in {
      val p = SeqProperty[Int](1, 2, 3)
      p.replace(0, 3, p.get.map(_ + 1): _*)
      p.get should be(Seq(2, 3, 4))
    }

    "return filtered version of sequence" in {
      val p = SeqProperty[Int](1, 2, 3)
      val f = p.filter(_ % 2 == 0)

      f.get should be(Seq(2))
      f.size should be(1)
      f.isEmpty should be(false)
      f.nonEmpty should be(true)

      p.append(4, 5, 6)
      p.prepend(-2, -4, -6)

      f.get should be(Seq(-2, -4, -6, 2, 4, 6))
      f.size should be(6)
      f.isEmpty should be(false)
      f.nonEmpty should be(true)

      p.elemProperties.foreach(el => if (el.get % 2 == 0) el.set(el.get + 1))

      f.get should be(Seq())
      f.size should be(0)
      f.isEmpty should be(true)
      f.nonEmpty should be(false)
    }

    "return filtered version of sequence with ModelProperty" in {
      trait M {
        def x: Int
      }

      case class MI(override val x: Int) extends M

      val p = SeqProperty[M](MI(1), MI(2), MI(3))
      val f = p.filter(_.x % 2 == 0)

      f.get.map(_.x) should be(Seq(2))

      p.append(MI(4), MI(5), MI(6))
      p.prepend(MI(-2), MI(-4), MI(-6))

      f.get.map(_.x) should be(Seq(-2, -4, -6, 2, 4, 6))

      p.elemProperties.foreach(el => if (el.get.x % 2 == 0) el.set(MI(el.get.x + 1)))

      f.get.map(_.x) should be(Seq())
    }

    "provide filtered version of sequence which should fire valid listeners" in {
      val p = SeqProperty[Int](1, 2, 3)
      val f = p.filter(_ % 2 == 0)

      val states = mutable.ArrayBuffer.empty[Seq[Int]]: @silent
      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[Int]]]: @silent

      p.listenersCount() should be(0)
      p.structureListenersCount() should be(0)

      val r1 = f.listen(v => states += v)
      val r2 = f.listenStructure(p => patches += p)

      p.listenersCount() should be(1)
      p.structureListenersCount() should be(1)

      p.append(4)

      states.last should be(Seq(2, 4))
      patches.size should be(1)
      patches.last.idx should be(1)
      patches.last.added.map(_.get) should be(Seq(4))
      patches.last.removed.map(_.get) should be(Seq())

      p.append(1, 2, 3, 4)

      states.last should be(Seq(2, 4, 2, 4))
      patches.last.idx should be(2)
      patches.last.added.map(_.get) should be(Seq(2, 4))
      patches.last.removed.map(_.get) should be(Seq())

      p.prepend(1, 2, 3, 4)

      states.last should be(Seq(2, 4, 2, 4, 2, 4))
      patches.last.idx should be(0)
      patches.last.added.map(_.get) should be(Seq(2, 4))
      patches.last.removed.map(_.get) should be(Seq())

      p.replace(2, 10, 0, 0, 0, 1)

      states.last should be(Seq(2, 0, 0, 0))
      patches.last.idx should be(1)
      patches.last.added.map(_.get) should be(Seq(0, 0, 0))
      patches.last.removed.map(_.get) should be(Seq(4, 2, 4, 2, 4))

      p.remove(1, 5)

      states.last should be(Seq())
      patches.last.idx should be(0)
      patches.last.added.map(_.get) should be(Seq())
      patches.last.removed.map(_.get) should be(Seq(2, 0, 0, 0))

      p.set(Seq(1, 2, 3))

      states.last should be(Seq(2))
      patches.last.idx should be(0)
      patches.last.added.map(_.get) should be(Seq(2))
      patches.last.removed.map(_.get) should be(Seq())

      states.clear()
      patches.clear()

      p.elemProperties.foreach(el => el.set(el.get + 1))

      states.size should be(3)
      states(0) should be(Seq(2, 2))
      states(1) should be(Seq(2))
      states(2) should be(Seq(2, 4))
      patches.size should be(3)
      patches(0).idx should be(0)
      patches(0).added.map(_.get) should be(Seq(2))
      patches(0).removed.map(_.get) should be(Seq())
      patches(1).idx should be(1)
      patches(1).added.map(_.get) should be(Seq())
      patches(1).removed.map(_.get) should be(Seq(3)) // now has different value (2 => 3)
      patches(2).idx should be(1)
      patches(2).added.map(_.get) should be(Seq(4))
      patches(2).removed.map(_.get) should be(Seq())

      p.set(Seq(1, 2, 3))

      states.last should be(Seq(2))
      patches.last.idx should be(0)
      patches.last.added.map(_.get) should be(Seq(2))
      patches.last.removed.map(_.get) should be(Seq(2, 4))

      states.clear()
      patches.clear()

      CallbackSequencer().sequence {
        p.elemProperties.foreach(el => el.set(el.get + 1))
        p.elemProperties.foreach(el => el.set(el.get - 1))
        p.elemProperties.foreach(el => el.set(el.get + 1))
      }

      states.size should be(1)
      states(0) should be(Seq(2, 4))
      patches.size should be(3)
      patches(0).idx should be(0)
      patches(0).added.map(_.get) should be(Seq(2))
      patches(0).removed.map(_.get) should be(Seq())
      patches(1).idx should be(1)
      patches(1).added.map(_.get) should be(Seq())
      patches(1).removed.map(_.get) should be(Seq(3)) // now has different value (2 => 3)
      patches(2).idx should be(1)
      patches(2).added.map(_.get) should be(Seq(4))
      patches(2).removed.map(_.get) should be(Seq())

      p.set(Seq(1, 2, 3))

      states.clear()
      patches.clear()

      p.elemProperties.foreach(el => el.set(el.get + 2))

      states.size should be(1)
      states(0) should be(Seq(4))
      patches.size should be(0)

      p.set(Seq(1, 2, 3))

      states.clear()
      patches.clear()

      p.elemProperties.foreach(el => if (el.get % 2 == 0) el.set(el.get + 1))

      states.size should be(1)
      states(0) should be(Seq())
      patches.size should be(1)
      patches(0).idx should be(0)
      patches(0).added.map(_.get) should be(Seq())
      patches(0).removed.map(_.get) should be(Seq(3)) // now has different value (2 => 3)

      p.set(Seq(1, 2, 3))

      states.clear()
      patches.clear()

      p.append(2)
      p.append(4)
      p.append(6)

      states.size should be(3)
      states(0) should be(Seq(2, 2))
      states(1) should be(Seq(2, 2, 4))
      states(2) should be(Seq(2, 2, 4, 6))
      patches.size should be(3)
      patches(0).idx should be(1)
      patches(0).added.map(_.get) should be(Seq(2))
      patches(0).removed.map(_.get) should be(Seq())
      patches(1).idx should be(2)
      patches(1).added.map(_.get) should be(Seq(4))
      patches(1).removed.map(_.get) should be(Seq())
      patches(2).idx should be(3)
      patches(2).added.map(_.get) should be(Seq(6))
      patches(2).removed.map(_.get) should be(Seq())

      p.set(Seq(1, 2, 3))

      states.clear()
      patches.clear()

      CallbackSequencer().sequence {
        p.append(2)
        p.append(4)
        p.append(6)
      }

      states.size should be(1)
      states(0) should be(Seq(2, 2, 4, 6))
      patches.size should be(3)
      patches(0).idx should be(1)
      patches(0).added.map(_.get) should be(Seq(2))
      patches(0).removed.map(_.get) should be(Seq())
      patches(1).idx should be(2)
      patches(1).added.map(_.get) should be(Seq(4))
      patches(1).removed.map(_.get) should be(Seq())
      patches(2).idx should be(3)
      patches(2).added.map(_.get) should be(Seq(6))
      patches(2).removed.map(_.get) should be(Seq())

      f.get should be(Seq(2, 2, 4, 6))

      p.listenersCount() should be(1)
      p.structureListenersCount() should be(1)

      r1.cancel()
      r2.cancel()

      p.listenersCount() should be(0)
      p.structureListenersCount() should be(0)
    }

    "be able to modify after transformation" in {
      val numbers = SeqProperty[Int](1, 2, 3)
      val strings = numbers.transform((i: Int) => i.toString, (s: String) => Integer.parseInt(s))

      strings.append("4", "5", "6")
      numbers.get should be(Seq(1, 2, 3, 4, 5, 6))

      strings.remove("4")
      strings.remove("6")
      numbers.get should be(Seq(1, 2, 3, 5))
    }

    "filter transformed property" in {
      val doubles = SeqProperty[Double](1.5, 2.3, 3.7)
      val ints = doubles.transform((d: Double) => d.toInt, (i: Int) => i.toDouble)
      val evens = ints.filter(_ % 2 == 0)

      doubles.listenersCount() should be(0)
      ints.listenersCount() should be(0)

      val r1 = evens.listenStructure(_ => ())

      doubles.listenersCount() should be(1)
      ints.listenersCount() should be(1)

      doubles.listenersCount() should be(1)
      doubles.structureListenersCount() should be(1)
      ints.listenersCount() should be(1)
      ints.structureListenersCount() should be(1)

      doubles.get should be(Seq(1.5, 2.3, 3.7))
      ints.get should be(Seq(1, 2, 3))
      evens.get should be(Seq(2))

      doubles.prepend(8.5)

      doubles.get should be(Seq(8.5, 1.5, 2.3, 3.7))
      ints.get should be(Seq(8, 1, 2, 3))
      evens.get should be(Seq(8, 2))

      ints.append(12)

      doubles.get should be(Seq(8.5, 1.5, 2.3, 3.7, 12.0))
      ints.get should be(Seq(8, 1, 2, 3, 12))
      evens.get should be(Seq(8, 2, 12))

      doubles.replace(1, 3, 4.5, 5.5, 6.6)

      doubles.get should be(Seq(8.5, 4.5, 5.5, 6.6, 12.0))
      ints.get should be(Seq(8, 4, 5, 6, 12))
      evens.get should be(Seq(8, 4, 6, 12))

      ints.remove(4)

      doubles.get should be(Seq(8.5, 5.5, 6.6, 12.0))
      ints.get should be(Seq(8, 5, 6, 12))
      evens.get should be(Seq(8, 6, 12))

      doubles.remove(6.6)

      doubles.get should be(Seq(8.5, 5.5, 12.0))
      ints.get should be(Seq(8, 5, 12))
      evens.get should be(Seq(8, 12))

      doubles.append(8.2, 10.3)

      doubles.get should be(Seq(8.5, 5.5, 12.0, 8.2, 10.3))
      ints.get should be(Seq(8, 5, 12, 8, 10))
      evens.get should be(Seq(8, 12, 8, 10))

      ints.remove(5)

      doubles.get should be(Seq(8.5, 12.0, 8.2, 10.3))
      ints.get should be(Seq(8, 12, 8, 10))
      evens.get should be(Seq(8, 12, 8, 10))

      r1.cancel()

      doubles.listenersCount() should be(0)
      doubles.structureListenersCount() should be(0)
      ints.listenersCount() should be(0)
      ints.structureListenersCount() should be(0)
    }

    "provide valid patch when combined" in {
      val s = SeqProperty(1, 2, 3, 4)
      val p = Property(2)

      val c = s.combine(p)(_ * _)
      s.listenersCount() should be(0)
      s.structureListenersCount() should be(0)
      p.listenersCount() should be(0)

      var lastPatch: Patch[ReadableProperty[Int]] = null
      val r2 = c.listenStructure(patch => lastPatch = patch)
      s.listenersCount() should be(0)
      s.structureListenersCount() should be(1)
      p.listenersCount() should be(0)

      val listenCalls = mutable.ListBuffer[Seq[Int]]()
      val r1 = c.listen(v => listenCalls += v)
      s.listenersCount() should be(1)
      s.structureListenersCount() should be(1)
      p.listenersCount() should be(1)

      c.get should be(Seq(2, 4, 6, 8))

      lastPatch = null
      CallbackSequencer().sequence {
        p.set(1)
      }
      c.get should be(Seq(1, 2, 3, 4))
      lastPatch should be(null)
      listenCalls.size should be(1)
      listenCalls should contain(Seq(1, 2, 3, 4))

      listenCalls.clear()
      CallbackSequencer().sequence {
        p.set(2)
      }
      listenCalls.size should be(1)
      listenCalls should contain(Seq(2, 4, 6, 8))

      listenCalls.clear()
      lastPatch = null
      s.append(1)
      c.get should be(Seq(2, 4, 6, 8, 2))
      lastPatch.idx should be(4)
      lastPatch.added.head.get should be(2)
      lastPatch.removed.size should be(0)
      lastPatch.clearsProperty should be(false)
      listenCalls.size should be(1)
      listenCalls should contain(Seq(2, 4, 6, 8, 2))

      listenCalls.clear()
      lastPatch = null
      s.remove(1, 3)
      c.get should be(Seq(2, 2))
      lastPatch.idx should be(1)
      lastPatch.added.size should be(0)
      lastPatch.removed.head.get should be(4)
      lastPatch.removed.last.get should be(8)
      lastPatch.clearsProperty should be(false)
      listenCalls.size should be(1)
      listenCalls should contain(Seq(2, 2))

      listenCalls.clear()
      lastPatch = null
      s.insert(1, 6, 7, 8)
      c.get should be(Seq(2, 12, 14, 16, 2))
      lastPatch.idx should be(1)
      lastPatch.added.head.get should be(12)
      lastPatch.added.last.get should be(16)
      lastPatch.removed.size should be(0)
      lastPatch.clearsProperty should be(false)
      listenCalls.size should be(1)
      listenCalls should contain(Seq(2, 12, 14, 16, 2))

      listenCalls.clear()
      lastPatch = null
      s.clear()
      c.get should be(Seq())
      lastPatch.idx should be(0)
      lastPatch.added.size should be(0)
      lastPatch.removed.head.get should be(2)
      lastPatch.removed.last.get should be(2)
      lastPatch.clearsProperty should be(true)
      listenCalls.size should be(1)
      listenCalls should contain(Seq())

      r1.cancel()
      s.listenersCount() should be(0)
      s.structureListenersCount() should be(1)
      p.listenersCount() should be(0)

      r2.cancel()
      s.listenersCount() should be(0)
      s.structureListenersCount() should be(0)
      p.listenersCount() should be(0)
    }

    "provide reversed version" in {
      val p = SeqProperty(1,2,3)
      val r: SeqProperty[Int, Property[Int]] = p.reversed()
      val r2: SeqProperty[Int, Property[Int]] = r.reversed()

      p.get should be(r.get.reverse)
      p.get should be(r2.get)

      var pValue = Seq.empty[Int]
      var rValue = Seq.empty[Int]
      var r2Value = Seq.empty[Int]
      var pPatch: Patch[Property[Int]] = null
      var rPatch: Patch[Property[Int]] = null
      var r2Patch: Patch[Property[Int]] = null
      p.listen(v => pValue = v)
      r.listen(v => rValue = v)
      r2.listen(v => r2Value = v)
      p.listenStructure(v => pPatch = v)
      r.listenStructure(v => rPatch = v)
      r2.listenStructure(v => r2Patch = v)

      p.append(4)

      pValue should be(Seq(1,2,3,4))
      rValue should be(Seq(4,3,2,1))
      r2Value should be(Seq(1,2,3,4))
      pPatch.idx should be(3)
      pPatch.added.size should be(1)
      pPatch.removed.size should be(0)
      rPatch.idx should be(0)
      rPatch.added.size should be(1)
      rPatch.removed.size should be(0)
      r2Patch.idx should be(3)
      r2Patch.added.size should be(1)
      r2Patch.removed.size should be(0)

      p.prepend(0)

      pValue should be(Seq(0,1,2,3,4))
      rValue should be(Seq(4,3,2,1,0))
      r2Value should be(Seq(0,1,2,3,4))
      pPatch.idx should be(0)
      pPatch.added.size should be(1)
      pPatch.removed.size should be(0)
      rPatch.idx should be(4)
      rPatch.added.size should be(1)
      rPatch.removed.size should be(0)
      r2Patch.idx should be(0)
      r2Patch.added.size should be(1)
      r2Patch.removed.size should be(0)

      p.replace(1, 2, 9, 9, 9)

      pValue should be(Seq(0,9,9,9,3,4))
      rValue should be(Seq(4,3,9,9,9,0))
      r2Value should be(Seq(0,9,9,9,3,4))
      pPatch.idx should be(1)
      pPatch.added.size should be(3)
      pPatch.removed.size should be(2)
      rPatch.idx should be(2)
      rPatch.added.size should be(3)
      rPatch.removed.size should be(2)
      r2Patch.idx should be(1)
      r2Patch.added.size should be(3)
      r2Patch.removed.size should be(2)
    }

    "provide reversed version of transformed and filtered SeqProperty" in {
      val p = SeqProperty(-3,-2,-1,0,1,2)
      val f = p.filter(_ >= 0).transform((i: Int) => i + 1)
      val r: ReadableSeqProperty[Int, ReadableProperty[Int]] = f.reversed()
      val r2: ReadableSeqProperty[Int, ReadableProperty[Int]] = r.reversed()

      f.get should be(r.get.reverse)
      f.get should be(r2.get)

      var fValue = Seq.empty[Int]
      var rValue = Seq.empty[Int]
      var r2Value = Seq.empty[Int]
      var fPatch: Patch[ReadableProperty[Int]] = null
      var rPatch: Patch[ReadableProperty[Int]] = null
      var r2Patch: Patch[ReadableProperty[Int]] = null
      f.listen(v => fValue = v)
      r.listen(v => rValue = v)
      r2.listen(v => r2Value = v)
      f.listenStructure(v => fPatch = v)
      r.listenStructure(v => rPatch = v)
      r2.listenStructure(v => r2Patch = v)

      p.append(3)

      fValue should be(Seq(1,2,3,4))
      rValue should be(Seq(4,3,2,1))
      r2Value should be(Seq(1,2,3,4))
      fPatch.idx should be(3)
      fPatch.added.size should be(1)
      fPatch.removed.size should be(0)
      rPatch.idx should be(0)
      rPatch.added.size should be(1)
      rPatch.removed.size should be(0)
      r2Patch.idx should be(3)
      r2Patch.added.size should be(1)
      r2Patch.removed.size should be(0)

      p.prepend(-1)
      p.prepend(0)

      fValue should be(Seq(1,1,2,3,4))
      rValue should be(Seq(4,3,2,1,1))
      r2Value should be(Seq(1,1,2,3,4))
      fPatch.idx should be(0)
      fPatch.added.size should be(1)
      fPatch.removed.size should be(0)
      rPatch.idx should be(4)
      rPatch.added.size should be(1)
      rPatch.removed.size should be(0)
      r2Patch.idx should be(0)
      r2Patch.added.size should be(1)
      r2Patch.removed.size should be(0)

      p.replace(5, 2, 8, 8, 8)

      fValue should be(Seq(1,9,9,9,3,4))
      rValue should be(Seq(4,3,9,9,9,1))
      r2Value should be(Seq(1,9,9,9,3,4))
      fPatch.idx should be(1)
      fPatch.added.size should be(3)
      fPatch.removed.size should be(2)
      rPatch.idx should be(2)
      rPatch.added.size should be(3)
      rPatch.removed.size should be(2)
      r2Patch.idx should be(1)
      r2Patch.added.size should be(3)
      r2Patch.removed.size should be(2)

      p.touch()

      fValue should be(Seq(1,9,9,9,3,4))
      rValue should be(Seq(4,3,9,9,9,1))
      r2Value should be(Seq(1,9,9,9,3,4))
    }

    "zip with another ReadableProperty" in {
      val numbers = SeqProperty(1, 2, 3, 4, 5, 6, 7, 8, 9)
      val odds: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 1)
      val evens: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 0)

      val pairs = odds.zip(evens)((_, _))

      numbers.listenersCount() should be(0)
      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)
      odds.structureListenersCount() should be(0)
      evens.structureListenersCount() should be(0)

      numbers.append(20, 21)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,20)))

      numbers.remove(21)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,20)))

      numbers.remove(20)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8)))

      numbers.append(10)

      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[(Int, Int)]]]
      val r1 = pairs.listenStructure(p => patches.append(p))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.elemProperties(3).set(8)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,8), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.elemProperties(3).set(4)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.elemProperties(2).set(9)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (9,4), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.elemProperties(2).set(3)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.append(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10)))
      patches.size should be(0)

      numbers.append(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12)))
      patches.size should be(1)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(14)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12)))
      patches.size should be(1)

      numbers.append(13)
      pairs.size should be(7)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(2)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.remove(5)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,6), (9,8), (11,10), (13,12)))
      patches.size should be(3)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(5)

      numbers.remove(6)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(4)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      numbers.elemProperties(7).set(20)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,20), (11,12), (13,14)))
      patches.size should be(4)

      numbers.elemProperties(7).set(10)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(4)

      numbers.remove(12)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,14)))
      patches.size should be(5)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(2)

      numbers.remove(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (13,14)))
      patches.size should be(6)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      CallbackSequencer().sequence {
        numbers.remove(1)
        numbers.remove(2)
      }
      pairs.size should be(4)
      pairs.get should be(Seq((3,4), (7,8), (9,10), (13,14)))
      patches.size should be(8)
      patches.last.idx should be(0)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      numbers.touch()
      pairs.get should be(Seq((3,4), (7,8), (9,10), (13,14)))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      r1.cancel()

      numbers.listenersCount() should be(0)
      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)
      odds.structureListenersCount() should be(0)
      evens.structureListenersCount() should be(0)

      numbers.append(20, 21)
      pairs.get should be(Seq((3,4), (7,8), (9,10), (13,14), (21, 20)))

      numbers.remove(4)
      pairs.get should be(Seq((3,8), (7,10), (9,14), (13,20)))

      numbers.remove(9)
      pairs.get should be(Seq((3,8), (7,10), (13,14), (21,20)))
    }

    "zip all with another ReadableProperty" in {
      val numbers = SeqProperty(1, 2, 3, 4, 5, 6, 7, 8, 9)
      val odds: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 1)
      val evens: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 0)

      val defaultA = Property(-1)
      val defaultB = Property(-2)

      val pairs = odds.zipAll(evens)((x, y) => (x, y), defaultA, defaultB)

      numbers.listenersCount() should be(0)
      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)
      odds.structureListenersCount() should be(0)
      evens.structureListenersCount() should be(0)

      numbers.append(20, 21)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,20), (21, -2)))

      numbers.remove(21)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,20)))

      numbers.remove(20)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,-2)))

      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[(Int, Int)]]]
      val r1 = pairs.listenStructure(p => patches.append(p))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      numbers.elemProperties(3).set(8)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,8), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      numbers.elemProperties(3).set(4)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      numbers.elemProperties(2).set(9)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (9,4), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      numbers.elemProperties(2).set(3)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      defaultB.set(256)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,256)))
      patches.size should be(0)

      defaultB.set(-2)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,-2)))
      patches.size should be(0)

      numbers.append(10)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10)))
      patches.size should be(1)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.append(11)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,-2)))
      patches.size should be(2)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12)))
      patches.size should be(3)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.append(14)
      pairs.size should be(7)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12), (-1, 14)))
      patches.size should be(4)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(13)
      pairs.size should be(7)
      pairs.get should be(Seq((1,2), (3,4), (5,6), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(5)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.remove(5)
      pairs.size should be(7)
      pairs.get should be(Seq((1,2), (3,4), (7,6), (9,8), (11,10), (13,12), (-1,14)))
      patches.size should be(6)
      patches.last.idx should be(2)
      patches.last.added.size should be(5)
      patches.last.removed.size should be(5)

      numbers.remove(6)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(7)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(5)

      numbers.elemProperties(7).set(20)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,20), (11,12), (13,14)))
      patches.size should be(7)

      numbers.elemProperties(7).set(10)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,12), (13,14)))
      patches.size should be(7)

      numbers.remove(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (11,14), (13,-2)))
      patches.size should be(8)
      patches.last.idx should be(4)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(2)

      numbers.remove(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1,2), (3,4), (7,8), (9,10), (13,14)))
      patches.size should be(9)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(2)

      CallbackSequencer().sequence {
        numbers.remove(1)
        numbers.remove(2)
      }
      pairs.size should be(4)
      pairs.get should be(Seq((3,4), (7,8), (9,10), (13,14)))
      patches.size should be(11)
      patches.last.idx should be(0)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      r1.cancel()

      numbers.listenersCount() should be(0)
      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)
      odds.structureListenersCount() should be(0)
      evens.structureListenersCount() should be(0)

      numbers.append(20, 21)
      pairs.get should be(Seq((3,4), (7,8), (9,10), (13,14), (21, 20)))

      numbers.remove(4)
      pairs.get should be(Seq((3,8), (7,10), (9,14), (13,20), (21, -2)))

      numbers.remove(9)
      pairs.get should be(Seq((3,8), (7,10), (13,14), (21,20)))
    }

    "zip with indexes" in {
      val numbers = SeqProperty(1, 2, 3, 4, 5, 6, 7, 8, 9)
      val indexed = numbers.zipWithIndex

      indexed.get should be(numbers.get.zipWithIndex)

      numbers.append(-1)
      indexed.get should be(numbers.get.zipWithIndex)

      numbers.remove(-1)
      indexed.get should be(numbers.get.zipWithIndex)

      numbers.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)

      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[(Int, Int)]]]
      val r1 = indexed.listenStructure(p => patches.append(p))

      numbers.listenersCount() should be(0)
      numbers.structureListenersCount() should be(1)

      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(0)

      numbers.elemProperties(3).set(8)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(0)

      numbers.elemProperties(3).set(4)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(0)

      numbers.elemProperties(2).set(9)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(0)

      numbers.elemProperties(2).set(3)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(0)

      numbers.append(10)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(1)
      patches.last.idx should be(9)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(11)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(2)
      patches.last.idx should be(10)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.remove(5)
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(3)
      patches.last.idx should be(4)
      patches.last.added.size should be(6)
      patches.last.removed.size should be(7)

      CallbackSequencer().sequence {
        numbers.remove(1)
        numbers.remove(3)
      }
      indexed.get should be(numbers.get.zipWithIndex)
      patches.size should be(5)
      patches.last.idx should be(1)
      patches.last.added.size should be(7)
      patches.last.removed.size should be(7)

      numbers.touch()
      indexed.get should be(numbers.get.zipWithIndex)

      numbers.listenersCount() should be(0)
      numbers.structureListenersCount() should be(1)

      r1.cancel()

      numbers.listenersCount() should be(0)
      numbers.structureListenersCount() should be(0)

      indexed.get should be(numbers.get.zipWithIndex)

      numbers.append(-1)
      indexed.get should be(numbers.get.zipWithIndex)

      numbers.remove(-1)
      indexed.get should be(numbers.get.zipWithIndex)
    }
  }

  "Seq[Property]" should {
    "combine into ReadableSeqProperty" in {
      def validateContents[A](propertySeq: ISeq[Property[A]], combined: ReadableSeqProperty[A, ReadableProperty[A]]): Unit = {
        combined.get.zip(propertySeq).foreach {
          case (c, s) => c should ===(s.get)
        }
      }

      var listenCounter = 0
      var listenStructureCounter = 0
      var listenOnceCounter = 0
      val listener = (v: Any) => listenCounter += 1
      val oneTimeListener = (v: Any) => listenOnceCounter += 1
      val structureListener = (v: Any) => listenStructureCounter += 1

      val propertySeq = ISeq(Property("test1"), Property("test2"), Property("test3"))

      propertySeq.map(_.listenersCount()) should be(Seq(0, 0, 0))

      import Properties._
      val combined = propertySeq.combineToSeqProperty

      propertySeq.map(_.listenersCount()) should be(Seq(0, 0, 0))
      combined.listenersCount() should be(0)
      combined.structureListenersCount() should be(0)

      propertySeq.foreach(_.listen(listener))
      propertySeq.foreach(_.listenOnce(oneTimeListener))
      val r1 = combined.listen(listener)
      val r2 = combined.listenOnce(oneTimeListener)
      val r3 = combined.listenStructure(structureListener)

      propertySeq.map(_.listenersCount()) should be(Seq(3, 3, 3))
      combined.listenersCount() should be(2)
      combined.structureListenersCount() should be(0)

      validateContents(propertySeq, combined)

      propertySeq.head.set("t1")
      validateContents(propertySeq, combined)
      listenCounter should ===(2)
      listenOnceCounter should ===(2)
      listenStructureCounter should ===(0)

      propertySeq.map(_.listenersCount()) should be(Seq(2, 3, 3))
      combined.listenersCount() should be(1)
      combined.structureListenersCount() should be(0)

      propertySeq(1).set("")
      validateContents(propertySeq, combined)
      listenCounter should ===(4)
      listenOnceCounter should ===(3)
      listenStructureCounter should ===(0)

      propertySeq.map(_.listenersCount()) should be(Seq(2, 2, 3))
      combined.listenersCount() should be(1)
      combined.structureListenersCount() should be(0)

      propertySeq(2).set("123123")
      validateContents(propertySeq, combined)
      listenCounter should ===(6)
      listenOnceCounter should ===(4)
      listenStructureCounter should ===(0)

      propertySeq.map(_.listenersCount()) should be(Seq(2, 2, 2))
      combined.listenersCount() should be(1)
      combined.structureListenersCount() should be(0)

      r1.cancel()
      propertySeq(1).set("test2")
      validateContents(propertySeq, combined)
      listenCounter should ===(7)
      listenOnceCounter should ===(4)
      listenStructureCounter should ===(0)

      propertySeq.map(_.listenersCount()) should be(Seq(1, 1, 1))
      combined.listenersCount() should be(0)
      combined.structureListenersCount() should be(0)

      r1.restart()
      propertySeq(1).set("")
      validateContents(propertySeq, combined)
      listenCounter should ===(9)
      listenOnceCounter should ===(4)
      listenStructureCounter should ===(0)

      propertySeq.map(_.listenersCount()) should be(Seq(2, 2, 2))
      combined.listenersCount() should be(1)
      combined.structureListenersCount() should be(0)

      r1.cancel()
      r2.cancel()
      r3.cancel()

      propertySeq.map(_.listenersCount()) should be(Seq(1, 1, 1))
      combined.listenersCount() should be(0)
      combined.structureListenersCount() should be(0)
    }
  }
}

private object ReqModels {
  case class Simple(i: Int, s:  Simple)
  object Simple extends HasModelPropertyCreator[Simple]

  trait ReqT {
    def t: ReqT
  }
  object ReqT extends HasModelPropertyCreator[ReqT]

  case class SimpleSeq(i: Seq[SimpleSeq], s: SimpleSeq)
  object SimpleSeq extends HasModelPropertyCreator[SimpleSeq]
}