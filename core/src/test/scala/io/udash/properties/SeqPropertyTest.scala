package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.seq.{Patch, ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.testing.UdashCoreTest
import io.udash.utils.Registration

import scala.collection.mutable
import scala.util.Random

class SeqPropertyTest extends UdashCoreTest {
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

    override def equals(obj: Any): Boolean = obj match {
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

    override def equals(obj: Any): Boolean = obj match {
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

  "SeqProperty" should {
    "handle sequence of properties" in {
      val p = SeqProperty[Int](Seq(1, 2, 3))
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
      val p = SeqProperty[Int](1, 2, 3)
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

      p.set(Seq(1, 2, 3))
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
      val p = SeqProperty.blank[Int]

      val values = mutable.ArrayBuffer[Seq[Int]]()
      val listener = (s: Seq[Int]) => values += s

      p.listen(listener)

      p.set(Seq(1, 2, 3))
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
      val p = SeqProperty.blank[Int]

      val patches = mutable.ArrayBuffer[Patch[Property[Int]]]()
      val listener = (s: Patch[Property[Int]]) => patches += s

      p.listenStructure(listener)

      p.set(Seq(1, 2, 3))
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
      val p = SeqProperty.blank[Int]

      val patches = mutable.ArrayBuffer[Patch[Property[Int]]]()
      val listener = (s: Patch[Property[Int]]) => patches += s

      p.listenStructure(listener)

      p.set(Seq(1, 2, 3))
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
        (i: Int) => (1 to i)
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
      class BadEquals(val v: Int)
      val init: Seq[BadEquals] = (1 to 3).map(new BadEquals(_))
      val p = SeqProperty[BadEquals](init)

      val t = p.transform[T](
        (i: BadEquals) => TC1(i.v),
        (t: T) => t match {
          case TC1(i) => new BadEquals(i)
          case _: T => new BadEquals(0)
        }
      )

      p.get.map(_.v) should be(Seq(1, 2, 3))
      t.get should be(Seq(TC1(1), TC1(2), TC1(3)))

      t.prepend(TC1(0))

      p.get.map(_.v) should be(Seq(0, 1, 2, 3))
      t.get should be(Seq(TC1(0), TC1(1), TC1(2), TC1(3)))

      p.append(new BadEquals(4))

      p.get.map(_.v) should be(Seq(0, 1, 2, 3, 4))
      t.get should be(Seq(TC1(0), TC1(1), TC1(2), TC1(3), TC1(4)))

      t.set(Seq(TO1, TO2))

      t.get should be(Seq(TC1(0), TC1(0))) // Again notice that you wont get inserted values, because they were transformed.
      p.get.map(_.v) should be(Seq(0, 0))
      t.get should be(Seq(TC1(0), TC1(0)))

      t.elemProperties.foreach(_.set(TC1(42)))
      p.get.map(_.v) should be(Seq(42, 42))
      t.get should be(Seq(TC1(42), TC1(42)))
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

      val states = mutable.ArrayBuffer.empty[Seq[Int]]
      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[Int]]]

      ensureNoListeners(p)

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

      ensureNoListeners(p)
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

      evens.get should be(Seq(2))

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

      ensureNoListeners(doubles)
      ensureNoListeners(ints)

      doubles.set(Seq(3.2, 4.7, 5.2))
      ints.get should be(Seq(3, 4, 5))
      evens.get should be(Seq(4))

      doubles.append(8.1)
      ints.get should be(Seq(3, 4, 5, 8))
      evens.get should be(Seq(4, 8))
    }

    "provide valid patch when combined" in {
      val s = SeqProperty(1, 2, 3, 4)
      val p = Property(2)

      val c = s.combine(p)(_ * _)
      ensureNoListeners(s)
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
      ensureNoListeners(s)
      p.listenersCount() should be(0)
    }

    "provide reversed version" in {
      val p = SeqProperty(1, 2, 3)
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

      pValue should be(Seq(1, 2, 3, 4))
      rValue should be(Seq(4, 3, 2, 1))
      r2Value should be(Seq(1, 2, 3, 4))
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

      pValue should be(Seq(0, 1, 2, 3, 4))
      rValue should be(Seq(4, 3, 2, 1, 0))
      r2Value should be(Seq(0, 1, 2, 3, 4))
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

      pValue should be(Seq(0, 9, 9, 9, 3, 4))
      rValue should be(Seq(4, 3, 9, 9, 9, 0))
      r2Value should be(Seq(0, 9, 9, 9, 3, 4))
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
      val p = SeqProperty(-3, -2, -1, 0, 1, 2)
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

      fValue should be(Seq(1, 2, 3, 4))
      rValue should be(Seq(4, 3, 2, 1))
      r2Value should be(Seq(1, 2, 3, 4))
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

      fValue should be(Seq(1, 1, 2, 3, 4))
      rValue should be(Seq(4, 3, 2, 1, 1))
      r2Value should be(Seq(1, 1, 2, 3, 4))
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

      fValue should be(Seq(1, 9, 9, 9, 3, 4))
      rValue should be(Seq(4, 3, 9, 9, 9, 1))
      r2Value should be(Seq(1, 9, 9, 9, 3, 4))
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

      fValue should be(Seq(1, 9, 9, 9, 3, 4))
      rValue should be(Seq(4, 3, 9, 9, 9, 1))
      r2Value should be(Seq(1, 9, 9, 9, 3, 4))
    }

    "zip with another ReadableProperty" in {
      val numbers = SeqProperty(1, 2, 3, 4, 5, 6, 7, 8, 9)
      val odds: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 1)
      val evens: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 0)

      val pairs = odds.zip(evens)((_, _))

      ensureNoListeners(numbers)
      ensureNoListeners(odds)
      ensureNoListeners(evens)

      numbers.append(20, 21)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 20)))

      numbers.remove(21)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 20)))

      numbers.remove(20)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8)))

      numbers.append(10)

      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[(Int, Int)]]]
      val r1 = pairs.listenStructure(p => patches.append(p))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.elemProperties(3).set(8)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 8), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.elemProperties(3).set(4)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.elemProperties(2).set(9)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (9, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.elemProperties(2).set(3)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.append(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(0)

      numbers.append(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12)))
      patches.size should be(1)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(14)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12)))
      patches.size should be(1)

      numbers.append(13)
      pairs.size should be(7)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(2)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.remove(5)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 6), (9, 8), (11, 10), (13, 12)))
      patches.size should be(3)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(5)

      numbers.remove(6)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(4)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      numbers.elemProperties(7).set(20)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 20), (11, 12), (13, 14)))
      patches.size should be(4)

      numbers.elemProperties(7).set(10)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(4)

      numbers.remove(12)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 14)))
      patches.size should be(5)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(2)

      numbers.remove(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (13, 14)))
      patches.size should be(6)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      CallbackSequencer().sequence {
        numbers.remove(1)
        numbers.remove(2)
      }
      pairs.size should be(4)
      pairs.get should be(Seq((3, 4), (7, 8), (9, 10), (13, 14)))
      patches.size should be(8)
      patches.last.idx should be(0)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      numbers.touch()
      pairs.get should be(Seq((3, 4), (7, 8), (9, 10), (13, 14)))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      r1.cancel()

      ensureNoListeners(numbers)
      ensureNoListeners(odds)
      ensureNoListeners(evens)

      numbers.append(20, 21)
      pairs.get should be(Seq((3, 4), (7, 8), (9, 10), (13, 14), (21, 20)))

      numbers.remove(4)
      pairs.get should be(Seq((3, 8), (7, 10), (9, 14), (13, 20)))

      numbers.remove(9)
      pairs.get should be(Seq((3, 8), (7, 10), (13, 14), (21, 20)))
    }

    "zip all with another ReadableProperty" in {
      val numbers = SeqProperty(1, 2, 3, 4, 5, 6, 7, 8, 9)
      val odds: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 1)
      val evens: ReadableSeqProperty[Int, ReadableProperty[Int]] = numbers.filter(_ % 2 == 0)

      val defaultA = Property(-1)
      val defaultB = Property(-2)

      val pairs = odds.zipAll(evens)((x, y) => (x, y), defaultA, defaultB)

      ensureNoListeners(numbers)
      ensureNoListeners(odds)
      ensureNoListeners(evens)

      numbers.append(20, 21)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 20), (21, -2)))

      numbers.remove(21)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 20)))

      numbers.remove(20)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, -2)))

      val patches = mutable.ArrayBuffer.empty[Patch[ReadableProperty[(Int, Int)]]]
      val r1 = pairs.listenStructure(p => patches.append(p))

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      numbers.elemProperties(3).set(8)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 8), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      numbers.elemProperties(3).set(4)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      numbers.elemProperties(2).set(9)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (9, 4), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      numbers.elemProperties(2).set(3)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      defaultB.set(256)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 256)))
      patches.size should be(0)

      defaultB.set(-2)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, -2)))
      patches.size should be(0)

      numbers.append(10)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10)))
      patches.size should be(1)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.append(11)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, -2)))
      patches.size should be(2)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12)))
      patches.size should be(3)
      patches.last.idx should be(5)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.append(14)
      pairs.size should be(7)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12), (-1, 14)))
      patches.size should be(4)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(0)

      numbers.append(13)
      pairs.size should be(7)
      pairs.get should be(Seq((1, 2), (3, 4), (5, 6), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(5)
      patches.last.idx should be(6)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(1)

      numbers.remove(5)
      pairs.size should be(7)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 6), (9, 8), (11, 10), (13, 12), (-1, 14)))
      patches.size should be(6)
      patches.last.idx should be(2)
      patches.last.added.size should be(5)
      patches.last.removed.size should be(5)

      numbers.remove(6)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(7)
      patches.last.idx should be(2)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(5)

      numbers.elemProperties(7).set(20)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 20), (11, 12), (13, 14)))
      patches.size should be(7)

      numbers.elemProperties(7).set(10)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 12), (13, 14)))
      patches.size should be(7)

      numbers.remove(12)
      pairs.size should be(6)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (11, 14), (13, -2)))
      patches.size should be(8)
      patches.last.idx should be(4)
      patches.last.added.size should be(2)
      patches.last.removed.size should be(2)

      numbers.remove(11)
      pairs.size should be(5)
      pairs.get should be(Seq((1, 2), (3, 4), (7, 8), (9, 10), (13, 14)))
      patches.size should be(9)
      patches.last.idx should be(4)
      patches.last.added.size should be(1)
      patches.last.removed.size should be(2)

      CallbackSequencer().sequence {
        numbers.remove(1)
        numbers.remove(2)
      }
      pairs.size should be(4)
      pairs.get should be(Seq((3, 4), (7, 8), (9, 10), (13, 14)))
      patches.size should be(11)
      patches.last.idx should be(0)
      patches.last.added.size should be(4)
      patches.last.removed.size should be(4)

      odds.listenersCount() should be(0)
      evens.listenersCount() should be(0)
      odds.structureListenersCount() should be(1)
      evens.structureListenersCount() should be(1)

      r1.cancel()

      ensureNoListeners(numbers)
      ensureNoListeners(odds)
      ensureNoListeners(evens)

      numbers.append(20, 21)
      pairs.get should be(Seq((3, 4), (7, 8), (9, 10), (13, 14), (21, 20)))

      numbers.remove(4)
      pairs.get should be(Seq((3, 8), (7, 10), (9, 14), (13, 20), (21, -2)))

      numbers.remove(9)
      pairs.get should be(Seq((3, 8), (7, 10), (13, 14), (21, 20)))
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

      ensureNoListeners(numbers)

      indexed.get should be(numbers.get.zipWithIndex)

      numbers.append(-1)
      indexed.get should be(numbers.get.zipWithIndex)

      numbers.remove(-1)
      indexed.get should be(numbers.get.zipWithIndex)
    }

    "cancel listeners in a callback" in {
      val t = SeqProperty(42, 0, 99)
      val regs = mutable.ArrayBuffer.empty[Registration]
      val results = mutable.ArrayBuffer.empty[String]

      regs += t.listenStructure { _ =>
        results += "1"
        regs.foreach(_.cancel())
      }
      regs += t.listenStructure { _ =>
        results += "2"
        regs.foreach(_.cancel())
      }
      regs += t.listenStructure { _ =>
        results += "3"
        regs.foreach(_.cancel())
      }
      regs += t.listenStructure { _ =>
        results += "4"
        regs.foreach(_.cancel())
      }
      t.touch()

      results should contain theSameElementsInOrderAs Seq("1")
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
      ensureNoListeners(combined)

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
