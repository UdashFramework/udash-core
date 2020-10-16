package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.ReqModels.SimpleSeq
import io.udash.properties.model.ModelProperty
import io.udash.properties.seq.{Patch, ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.testing.UdashCoreTest
import io.udash.utils.Registration
import org.scalactic.source.Position

import scala.collection.mutable
import scala.util.Try

class PropertyTest extends UdashCoreTest {
  class C(val i: Int, val s: String) {
    var variable: Int = 7
    override def equals(obj: Any): Boolean = obj match {
      case self if self.asInstanceOf[AnyRef] eq this.asInstanceOf[AnyRef] => true
      case c: C => c.i == this.i && c.s == this.s
      case _ => false
    }
  }

  sealed trait T
  case object TO1 extends T
  case object TO2 extends T
  case class TC1(i: Int) extends T
  case class TC2(s: String) extends T

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

    "toggle value" in {
      val booleanProperty = Property[Boolean](true)
      val blank = Property.blank[Boolean]

      import Properties._
      booleanProperty.toggle()
      booleanProperty.get shouldBe false
      booleanProperty.toggle()
      booleanProperty.get shouldBe true

      blank.toggle()
      blank.get shouldBe true

      """Property[JBoolean](true).toggle()""" shouldNot typeCheck
      """Property[String]("asd").toggle()""" shouldNot typeCheck
      """Property[AnyVal](true).toggle()""" shouldNot typeCheck

    }

    "fire listeners on value change" in {
      val values = MArrayBuffer[Any]()
      val oneTimeValues = MArrayBuffer[Any]()
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
      val values = MArrayBuffer[Any]()
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
      val values = MArrayBuffer[Any]()
      val listener = (v: Any) => values += v
      val oneTimeValues = MArrayBuffer[Any]()
      val oneTimeListener = (v: Any) => oneTimeValues += v

      val cp = Property[C](new C(1, "asd"))
      val tp = cp.bitransform(c => (TC1(c.i), TC2(c.s))) {
          case (TC1(i), TC2(s)) => new C(i, s)
          case _ => new C(0, "")
        }

      tp.listen(listener)
      cp.listen(listener)

      tp.listenOnce(oneTimeListener)
      cp.listenOnce(oneTimeListener)

      cp.get should be(new C(1, "asd"))
      tp.get should be(TC1(1) -> TC2("asd"))

      cp.set(new C(12, "asd2"))
      cp.get should be(new C(12, "asd2"))
      tp.get should be(TC1(12) -> TC2("asd2"))

      tp.set(TC1(-5) -> TC2("tp"))
      cp.get should be(new C(-5, "tp"))
      tp.get should be(TC1(-5) -> TC2("tp"))

      tp.set(TC1(-5) -> TC2("tp"))
      cp.get should be(new C(-5, "tp"))
      tp.get should be(TC1(-5) -> TC2("tp"))

      tp.touch()
      cp.get should be(new C(-5, "tp"))
      tp.get should be(TC1(-5) -> TC2("tp"))

      tp.set(TC1(-5) -> TC2("tp"), force = true)
      cp.get should be(new C(-5, "tp"))
      tp.get should be(TC1(-5) -> TC2("tp"))

      tp.clearListeners()
      tp.set(TC1(-12) -> TC2("tp"))

      tp.listen(listener)
      cp.listen(listener)
      tp.set(TC1(-13) -> TC2("tp"))

      cp.clearListeners()
      tp.set(TC1(-14) -> TC2("tp"))

      tp.listen(listener)
      cp.listen(listener)
      tp.set(TC1(-15) -> TC2("tp"))

      values.size should be(12)
      values should contain(new C(12, "asd2"))
      values should contain(TC1(12) -> TC2("asd2"))
      values should contain(TC1(-5) -> TC2("tp"))
      values should contain(new C(-5, "tp"))
      values should contain(TC1(-13) -> TC2("tp"))
      values should contain(new C(-13, "tp"))
      values should contain(TC1(-15) -> TC2("tp"))
      values should contain(new C(-15, "tp"))

      oneTimeValues.size should be(2)
      oneTimeValues should contain(new C(12, "asd2"))
      oneTimeValues should contain(TC1(12) -> TC2("asd2"))
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

      val totalPagesProperty = seenAllProperty.transform { all =>
        counter += 1
        if (all) Some(pageProperty.get) else None
      }

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

    "fire on transformed value changed or when forced" in {
      val origin: Property[Option[Int]] = Property(Some(0))
      val transformed: ReadableProperty[Boolean] = origin.transform((q: Option[Int]) => q.isDefined)
      var counter = 0

      transformed.listen(_ => counter += 1)

      origin.set(Some(0))
      counter shouldBe 0 //suppressed at origin

      origin.set(Some(1))
      counter shouldBe 0 //suppressed at transformed

      origin.set(None)
      counter shouldBe 1

      origin.set(None)
      counter shouldBe 1

      origin.set(None, force = true)
      counter shouldBe 2

      origin.touch()
      counter shouldBe 3
    }

    "fire on streamed value changed or when forced" in {
      val origin: Property[Option[Int]] = Property(Some(0))
      val target = Property.blank[Boolean]

      origin.streamTo(target)((q: Option[Int]) => q.isDefined)
      var counter = 0

      target.listen(_ => counter += 1)

      origin.set(Some(0))
      counter shouldBe 0 //suppressed at origin

      origin.set(Some(1))
      counter shouldBe 0 //suppressed at target

      origin.set(None)
      counter shouldBe 1

      origin.set(None)
      counter shouldBe 1

      //todo detect forced / touched?
      //origin.set(None, force = true)
      //counter shouldBe 2
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

      CallbackSequencer().sequence {
        p2.set(-2)
      }

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

      p1.set(7)
      p2.set(2)

      sum.get should be(9)
      mul.get should be(14)
    }

    "combine with other properties (model properties)" in {
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

      val sumCombine = sum.combine(s)((m, items) => items.map(_ * m))
      val sCombine = s.combine(sum)((items, m) => items.map(_ * m))
      val sCombineElements = s.combineElements(sum)(_ * _)

      p1.listenersCount() shouldBe 0
      p2.listenersCount() shouldBe 0
      sum.listenersCount() shouldBe 0
      sumCombine.listenersCount() shouldBe 0
      sCombine.listenersCount() shouldBe 0
      ensureNoListeners(s)
      ensureNoListeners(sCombineElements)

      sum.get shouldBe 10
      sumCombine.get shouldBe Seq(10, 20, 30, 40)
      sCombine.get shouldBe Seq(10, 20, 30, 40)
      sCombineElements.get shouldBe Seq(10, 20, 30, 40)

      var combinedElementsHeadChanges = 0
      val r1 = sCombineElements.elemProperties.head.listen(_ => combinedElementsHeadChanges += 1)

      p1.listenersCount() shouldBe 1
      p2.listenersCount() shouldBe 1
      sum.listenersCount() shouldBe 1
      sumCombine.listenersCount() shouldBe 0
      sCombine.listenersCount() shouldBe 0
      s.listenersCount() shouldBe 0
      ensureNoListeners(sCombineElements)

      var combineElementsValueChanges = 0
      var combinedElementsListenerValue = BSeq.empty[Int]
      val r2 = sCombineElements.listen { v =>
        combinedElementsListenerValue = v
        combineElementsValueChanges += 1
      }

      p1.listenersCount() shouldBe 1
      p2.listenersCount() shouldBe 1
      sum.listenersCount() shouldBe 2
      sumCombine.listenersCount() shouldBe 0
      sCombine.listenersCount() shouldBe 0
      s.listenersCount() shouldBe 1
      sCombineElements.listenersCount() shouldBe 1

      s.replace(1, 2, 7, 8, 9)

      sum.get shouldBe 10
      sumCombine.get shouldBe Seq(10, 70, 80, 90, 40)
      sCombine.get shouldBe Seq(10, 70, 80, 90, 40)
      sCombineElements.get shouldBe Seq(10, 70, 80, 90, 40)
      combinedElementsListenerValue shouldBe Seq(10, 70, 80, 90, 40)

      combinedElementsHeadChanges shouldBe 0
      combineElementsValueChanges shouldBe 1

      p1.set(0)
      p2.set(0)

      combinedElementsHeadChanges shouldBe 2
      combineElementsValueChanges shouldBe 3

      sum.get shouldBe 0
      sumCombine.get shouldBe Seq(0, 0, 0, 0, 0)
      sCombine.get shouldBe Seq(0, 0, 0, 0, 0)
      sCombineElements.get shouldBe Seq(0, 0, 0, 0, 0)
      combinedElementsListenerValue shouldBe Seq(0, 0, 0, 0, 0)

      r1.cancel()
      r2.cancel()

      p1.listenersCount() shouldBe 0
      p2.listenersCount() shouldBe 0
      sum.listenersCount() shouldBe 0
      sumCombine.listenersCount() shouldBe 0
      sCombine.listenersCount() shouldBe 0
      s.listenersCount() shouldBe 0
      ensureNoListeners(sCombineElements)

      p1.set(2)
      p2.set(3)
      sum.get shouldBe 5
      s.set(Seq(2, 1))
      sumCombine.get shouldBe Seq(10, 5)
      sCombine.get shouldBe Seq(10, 5)
      sCombineElements.get shouldBe Seq(10, 5)
      combinedElementsListenerValue shouldBe Seq(0, 0, 0, 0, 0) //r2 cancelled
    }

    "trigger once for multiply combined properties" in {
      val calls = mutable.Buffer.empty[(Int, Int, Int)]

      val p0 = Property(-1)
      val p1 = Property(-1)
      val p2 = Property(-1)

      p0.combine(p1)((_, _))
        .combine(p2)((_, _))
        .listen { case ((i1, i2), i3) =>
          calls.append((i1, i2, i3))
        }

      CallbackSequencer().sequence {
        p0.set(0)
        p1.set(1)
        p2.set(2)
      }

      calls should contain inOrderElementsOf Seq((0, 1, 2))

      calls.clear()

      CallbackSequencer().sequence {
        p2.set(3)
        p1.set(4)
        p0.set(5)
      }

      calls should contain inOrderElementsOf Seq((5, 4, 3))
    }

    "short-circuit loops on self" in {
      val p0 = Property.blank[Int]
      p0.listen { v => if (v < 10) p0.set(v + 1) }

      p0.set(1)

      p0.get shouldBe 2

      CallbackSequencer().sequence(p0.set(3))

      p0.get shouldBe 4
    }

    "transform to ReadableSeqProperty" in {
      val elemListeners = MMap.empty[ReadableProperty[_], Registration]
      var elementsUpdated = 0
      def registerElementListener(props: BSeq[ReadableProperty[_]]) =
        props.foreach { p =>
          elemListeners(p) = p.listen(_ => elementsUpdated += 1)
        }

      val p = Property("1,2,3,4,5")
      val s: ReadableSeqProperty[Int, ReadableProperty[Int]] =
        p.transformToSeq((v: String) => Try(v.split(",").map(_.trim.toInt).toSeq).getOrElse(Seq[Int]()))

      p.listenersCount() should be(0)

      registerElementListener(s.elemProperties)
      p.listenersCount() should be(1)

      var lastValue: BSeq[Int] = null
      var lastPatch: Patch[ReadableProperty[Int]] = null
      val r1 = s.listen(lastValue = _)
      val r2 = s.listenStructure { p =>
        registerElementListener(p.added)
        p.removed.foreach { p =>
          elemListeners(p).cancel()
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

      //suppressed at s
      p.set(" 5 ,4 ,3")
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
      s.get should be(Seq(1, 0, 1, 0, 1))

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("1,0,1,0,1,0,1")
      s.get should be(Seq(1, 0, 1, 0, 1, 0, 1))
      lastValue should be(s.get)
      lastPatch.added.size should be(2)
      lastPatch.removed.size should be(0)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.set("1,0,1,0,1")
      s.get should be(Seq(1, 0, 1, 0, 1))
      lastValue should be(s.get)
      lastPatch.idx should be(5)
      lastPatch.added.size should be(0)
      lastPatch.removed.size should be(2)
      elementsUpdated should be(0)

      lastValue = null
      lastPatch = null
      elementsUpdated = 0
      p.touch()
      s.get should be(Seq(1, 0, 1, 0, 1))
      lastValue should be(s.get)
      lastPatch should be(null)
      elementsUpdated should be(0)

      r1.cancel()
      r2.cancel()

      p.listenersCount() should be(1)

      elemListeners.foreach(_._2.cancel())

      p.listenersCount() should be(0)

      p.set("1,2,3")
      s.get should be(Seq(1, 2, 3))
      p.set("1,2,3,-1,-2,-3")
      s.get should be(Seq(1, 2, 3, -1, -2, -3))
    }

    "not allow children modification after transformation into ReadableSeqProperty" in {
      val p = Property("1,2,3,4,5")
      val s: ReadableSeqProperty[Int, ReadableProperty[Int]] =
        p.transformToSeq((v: String) => Try(v.split(",").map(_.toInt).toSeq).getOrElse(Seq[Int]()))

      s.elemProperties.foreach {
        case p: Property[Int] => p.set(20)
        case _: ReadableProperty[Int] => //ignore
      }

      s.get should be(Seq(1, 2, 3, 4, 5))
    }

    "not emit patches to listeners which observed updated value in transformToSeq" in {
      val patches = mutable.Buffer.empty[Patch[_]]

      def test()(implicit position: Position) = {
        val p = Property(1)
        val sp = p.transformToSeq(Seq(_))
        val transformed = sp.transformElements(_ + 1)
        sp.get shouldBe Seq(1)
        transformed.get should contain theSameElementsInOrderAs Seq(2) //current value observed immediately

        sp.listenStructure(patches += _)
      }

      test()
      patches shouldBe empty

      CallbackSequencer().sequence(test()) //should behave the same

      // No patch should be emitted to newly created structure listeners (after value was set).
      // Previously the patches would be emitted after exiting the sequencer,
      // so e.g. repeat based on `transformed`, would add a duplicate row.
      patches shouldBe empty
    }

    "transform to SeqProperty" in {
      val elemListeners = MMap.empty[ReadableProperty[_], Registration]
      var elementsUpdated = 0
      def registerElementListener(props: BSeq[ReadableProperty[_]]): Unit =
        props.foreach { p =>
          elemListeners(p) = p.listen(_ => elementsUpdated += 1)
        }

      val p = Property("1,2,3,4,5")
      val s: SeqProperty[Int, Property[Int]] =
        p.bitransformToSeq(v => Try(v.split(",").map(_.toInt).toSeq).getOrElse(Seq[Int]()))(_.mkString(","))

      p.listenersCount() should be(0)
      registerElementListener(s.elemProperties)
      p.listenersCount() should be(1)

      var lastValue: BSeq[Int] = null
      var lastPatch: Patch[ReadableProperty[Int]] = null
      val r1 = s.listen(lastValue = _)
      val r2 = s.listenStructure(p => {
        registerElementListener(p.added)
        p.removed.foreach { p =>
          elemListeners(p).cancel()
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

      s.set(Seq(1, 0, 1, 0, 1))
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
      s.set(Seq(1, 0, 1, 0, 1))
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

      p.set("1,-1,2,-2")
      s.get should be(Seq(1, -1, 2, -2))

      s.set(Seq(4, -4, 2, -2))
      p.get should be("4,-4,2,-2")
    }

    "transform SeqProperty to SeqProperty" in {
      class ClazzModel(val p: Int)
      val p = SeqProperty(new ClazzModel(42))
      var fromListen = BSeq.empty[Int]

      val s: ReadableSeqProperty[Int, ReadableProperty[Int]] = p.transformToSeq((v: BSeq[ClazzModel]) => v.map(_.p))
      p.get.map(_.p) shouldBe Seq(42)
      s.get shouldBe Seq(42)

      s.listen(fromListen = _, initUpdate = true)
      fromListen shouldBe Seq(42)

      p.set(p.get :+ new ClazzModel(66))
      s.get shouldBe Seq(42, 66)
      fromListen shouldBe Seq(42, 66)
    }

    "2-way transform SeqProperty to SeqProperty" in {
      class ClazzModel(val p: Int)
      val p = SeqProperty(new ClazzModel(42))
      var fromListen = BSeq.empty[Int]

      val s = p.bitransformToSeq(_.map(_.p))(_.map(new ClazzModel(_)))
      p.get.map(_.p) shouldBe Seq(42)
      s.get shouldBe Seq(42)

      p.listen(s => fromListen = s.map(_.p), initUpdate = true)
      fromListen shouldBe Seq(42)

      p.set(p.get :+ new ClazzModel(66))
      s.get shouldBe Seq(42, 66)
      fromListen shouldBe Seq(42, 66)

      s.set(Seq(1, 2, 3))
      s.get shouldBe Seq(1, 2, 3)
      fromListen shouldBe Seq(1, 2, 3)
    }

    "handle child modification in transformToSeq result" in {
      val s = Property("1,2,3,4,5,6")
      val i = s.bitransformToSeq(_.split(",").map(_.toInt))(_.map(_.toString).mkString(","))

      i.get should be(Seq(1, 2, 3, 4, 5, 6))

      var counter = 0
      val r1 = s.listen(_ => counter += 1)

      i.append(7)

      s.get should be("1,2,3,4,5,6,7")
      i.get should be(Seq(1, 2, 3, 4, 5, 6, 7))
      counter should be(1)

      CallbackSequencer().sequence {
        i.elemProperties.foreach(_.set(12))
      }

      s.get should be("12,12,12,12,12,12,12")
      i.get should be(Seq(12, 12, 12, 12, 12, 12, 12))
      counter should be(2)

      i.elemProperties.foreach(_.set(1))

      s.get should be("1,1,1,1,1,1,1")
      i.get should be(Seq(1, 1, 1, 1, 1, 1, 1))
      counter should be(9)

      r1.cancel()

      CallbackSequencer().sequence {
        i.elemProperties.foreach(_.set(13))
      }

      s.get should be("13,13,13,13,13,13,13")
      i.get should be(Seq(13, 13, 13, 13, 13, 13, 13))
      counter should be(9)

      i.elemProperties.foreach(_.set(1))

      s.get should be("1,1,1,1,1,1,1")
      i.get should be(Seq(1, 1, 1, 1, 1, 1, 1))
      counter should be(9)
    }

    "stream value to another property" in {
      val source = SeqProperty(1, 2, 3)
      val transformed = source.transformElements(_ * 2)
      val filtered = transformed.filter(_ < 10)
      val sum = filtered.transform(_.sum)

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
      t1.listenersCount() should be(1)
      t2.listenersCount() should be(0)

      r.cancel()

      p.listenersCount() should be(0)
      t1.listenersCount() should be(0)
      t2.listenersCount() should be(0)

      p.set(-7)
      t1.get should be(0)
      t2.get should be(1)
    }

    "fire transform on empty property" in {
      val p = Property(null: String)
      val t = p.transform(_ == null)

      t.get should be(true)

      p.set("asd")
      t.get should be(false)
    }

    "synchronize values with another property" in {
      val source = Property(1)
      var sourceListener = 0
      source.listen(sourceListener = _, initUpdate = true)

      val target = Property(5)
      var targetListener = 0
      target.listen(targetListener = _, initUpdate = true)

      source.get shouldBe 1
      sourceListener shouldBe 1
      target.get shouldBe 5
      targetListener shouldBe 5

      //Init update
      val registration = source.sync(target)((i: Int) => i * 2, (i: Int) => i / 2)

      registration.isActive shouldBe true
      source.get shouldBe 1
      sourceListener shouldBe 1
      target.get shouldBe 2
      targetListener shouldBe 2

      // Source update
      source.set(2)

      source.get shouldBe 2
      sourceListener shouldBe 2
      target.get shouldBe 4
      targetListener shouldBe 4

      // Source touch
      source.touch()

      source.get shouldBe 2
      sourceListener shouldBe 2
      target.get shouldBe 4
      targetListener shouldBe 4

      // Target update
      target.set(8)

      source.get shouldBe 4
      sourceListener shouldBe 4
      target.get shouldBe 8
      targetListener shouldBe 8

      // Source update
      source.set(2)

      source.get shouldBe 2
      sourceListener shouldBe 2
      target.get shouldBe 4
      targetListener shouldBe 4

      // Registration cancel and source update
      registration.cancel()
      source.set(1)

      registration.isActive shouldBe false
      source.get shouldBe 1
      sourceListener shouldBe 1
      target.get shouldBe 4
      targetListener shouldBe 4

      // Target update
      target.set(1)

      source.get shouldBe 1
      sourceListener shouldBe 1
      target.get shouldBe 1
      targetListener shouldBe 1

      // Restart streaming, source touch
      registration.restart()

      source.get shouldBe 1
      sourceListener shouldBe 1
      target.get shouldBe 2
      targetListener shouldBe 2

      // Target update
      target.set(8)

      source.get shouldBe 4
      sourceListener shouldBe 4
      target.get shouldBe 8
      targetListener shouldBe 8
    }

    "synchronize values with SeqProperty" in {
      val source = Property(1)
      val target = SeqProperty[Int](1, 2)

      source.get should be(1)
      target.get should be(Seq(1, 2))

      //Init update
      source.sync(target)((i: Int) => 1 to i, (s: BSeq[Int]) => s.length)

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

    "cancel listeners in a callback" in {
      val t = Property(42)
      val regs = MArrayBuffer.empty[Registration]
      val results = MArrayBuffer.empty[String]

      regs += t.listen { _ =>
        results += "1"
        regs.foreach(_.cancel())
      }
      regs += t.listen { _ =>
        results += "2"
        regs.foreach(_.cancel())
      }
      regs += t.listen { _ =>
        results += "3"
        regs.foreach(_.cancel())
      }
      regs += t.listen { _ =>
        results += "4"
        regs.foreach(_.cancel())
      }
      t.touch()

      results should contain theSameElementsInOrderAs Seq("1")
    }

    "safely cast to SeqProperty" in {
      val p = Property[Vector[Int]]((1 to 4).toVector)

      val sp = p.asSeq[Int]
      sp.append(5)

      sp.reversed().get shouldBe 5.to(1, -1)
      sp.get shouldBe (1 to 5)
      p.get shouldBe (1 to 5)
    }

    "safely cast nested SeqProperty" in {
      val p = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4)).elemProperties.head

      val sp = p.asSeq[Int]
      sp.prepend(0)

      sp.transformElements(_ + 1).reversed().get shouldBe 5.to(1, -1)
      sp.get shouldBe (0 to 4)
      p.get shouldBe (0 to 4)
    }

    "safely cast a model nested in a SeqProperty" in {
      val p = SeqProperty[Seq[M]](Seq(M(4, 2))).elemProperties.head

      val sp = p.asSeq
      val mp = sp.elemProperties.head.asModel
      mp.subProp(_.y).set(4)

      mp.get.x shouldBe 4
      mp.get.y shouldBe 4
      sp.get.head.x shouldBe 4
      sp.get.head.y shouldBe 4
      p.get.head.x shouldBe 4
      p.get.head.y shouldBe 4
    }

    "safely cast to ModelProperty" in {
      val p = Property[M](M(4, 2))

      val mp = p.asModel
      mp.subProp(_.y).set(4)

      mp.get.x shouldBe 4
      mp.get.y shouldBe 4
    }

    "safely cast nested ModelProperty" in {
      import ReqModels.Simple

      val p = Property[Simple](Simple(1, Simple(2, null)))

      val nested = p.asModel.subProp(_.s.s)
      nested.set(Simple(3, null))

      p.get shouldBe Simple(1, Simple(2, Simple(3, null)))
    }

    "safely cast SeqProperty nested in a model" in {
      val p = Property(SimpleSeq(Seq(SimpleSeq(Seq(SimpleSeq(Seq(), null)), null)), null))

      val sp = p.asModel.subSeq(_.i).elemProperties.head.asModel.subSeq(_.i).elemProperties.head.asModel.subSeq(_.i)
      sp.append(SimpleSeq(Seq(), null))

      p.get shouldBe SimpleSeq(Seq(SimpleSeq(Seq(SimpleSeq(Seq(SimpleSeq(Seq(), null)), null)), null)), null)
    }

    "support mirroring" in {
      val source = SeqProperty(1, 2, 3)
      val transformed = source.transformElements(_ * 2)
      val filtered = transformed.filter(_ < 10)
      val sum = filtered.transform(_.sum)

      println(source.listenersCount())

      val target = sum.mirror()

      println(source.listenersCount())

      target.get should be(12)

      source.append(4)
      target.get should be(20)

      source.touch()
      target.get should be(20)

      target.set(0)
      target.get should be(0)

      source.touch()
      target.get should be(20)

      source.remove(2)
      target.get should be(16)

      println(source.listenersCount())
    }
  }
}

private object ReqModels {
  case class Simple(i: Int, s: Simple)
  object Simple extends HasModelPropertyCreator[Simple]

  trait ReqT {
    def t: ReqT
  }
  object ReqT extends HasModelPropertyCreator[ReqT]

  case class SimpleSeq(i: Seq[SimpleSeq], s: SimpleSeq)
  object SimpleSeq extends HasModelPropertyCreator[SimpleSeq]
}
