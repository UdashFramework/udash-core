package io.udash.properties

import io.udash.properties.model.{ModelProperty, ReadableModelProperty}
import io.udash.properties.seq.{ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.testing.UdashCoreTest

class ImmutablePropertyTest extends UdashCoreTest {
  import ImmutablePropertyTest._

  "ImmutableProperty" should {
    "handle standard operations of ReadableProperty" in {
      val p: ReadableProperty[Int] = new ImmutableProperty[Int](7)

      p.get should be(7)

      var counter = 0
      p.listen(_ => counter += 1)
      counter should be(0)
      p.listen(_ => counter += 1, initUpdate = true)
      counter should be(1)

      val t = Property(0)
      p.streamTo(t, initUpdate = false)(identity)
      t.get should be(0)
      p.streamTo(t, initUpdate = true)(identity)
      t.get should be(7)
    }

    "support mirrors" in {
      val origin = new ImmutableProperty[Int](7)
      val p = origin.mirror()
      p.get should be(7)

      var counter = 0
      val listener1 = p.listen(_ => counter += 1)
      counter should be(0)
      val listener2 = p.listen(_ => counter += 1, initUpdate = true)
      counter should be(1)
      listener1.cancel()
      p.set(42)
      counter should be(2)

      val t = Property(0)
      val listener3 = p.streamTo(t, initUpdate = false)(identity)
      t.get should be(0)
      val listener4 = p.streamTo(t, initUpdate = true)(identity)
      t.get should be(42)
      p.set(64)
      t.get should be(64)

      origin.get should be(7)
      origin.listenersCount() should be(0)
      p.listenersCount() should be(3)

      Seq(listener2, listener3, listener4).foreach(_.cancel())
      p.listenersCount() should be(0)
    }
  }

  "ImmutableModelProperty" should {
    "handle standard operations of ReadableModelProperty" in {
      val e = ModelEntity("a", Seq(1), Vector(2, 3), ModelEntity("b", Seq(4), Vector(5, 6), ModelEntity("c", Seq(7), Vector(8, 9), null)))
      val p: ReadableModelProperty[ModelEntity] = new ImmutableModelProperty[ModelEntity](e)

      p.get should be(e)
      p.roSubProp(_.s).get should be("a")
      p.roSubProp(_.i).get should be(Seq(1))
      p.roSubProp(_.v).get shouldBe a[Vector[_]]
      p.roSubSeq(_.v).get shouldBe a[Vector[_]]
      p.roSubProp(_.v).get should be(Vector(2, 3))
      p.roSubSeq(_.v).get should be(Vector(2, 3))
      p.roSubProp(_.m).get should be(e.m)
      p.roSubProp(_.m.i).get should be(Seq(4))
      p.roSubProp(_.m.s).get should be("b")
      p.roSubSeq(_.m.m.i).elemProperties.head.get should be(7)
      p.roSubSeq(_.m.m.v).get shouldBe a[Vector[_]]
      p.roSubSeq(_.m.m.v).elemProperties.head.get should be(8)
    }

    "handle nested model" in {
      import io.udash.properties.Properties._
      val p = Nested(Nested(null)).toModelProperty

      p.roSubModel(_.s).roSubProp(_.s).get shouldBe null
    }

    "support mirrors" in {
      val e = ModelEntity("a", Seq(1), Vector(2, 3), ModelEntity("b", Seq(4), Vector(5, 6), ModelEntity("c", Seq(7), Vector(8, 9), null)))
      val origin: ReadableModelProperty[ModelEntity] = new ImmutableModelProperty[ModelEntity](e)
      val p: ModelProperty[ModelEntity] = origin.mirror().asModel

      p.get shouldBe e
      val subModel = p.subModel(_.m)
      subModel.get shouldBe e.m
      val subProp = subModel.subProp(_.s)
      subProp.get shouldBe e.m.s
      subProp.set("d")
      subProp.get shouldBe "d"
      p.get.m.s shouldBe "d"

      val subSeq = p.subSeq(_.v)
      subSeq.elemProperties.head.set(3)
      subSeq.get shouldBe Seq(3,3)

      origin.listenersCount() shouldBe 0
      origin.get shouldBe e
    }
  }

  "ImmutableSeqProperty" should {
    "handle standard operations of ReadableSeqProperty" in {
      val p: ReadableSeqProperty[Int, ReadableProperty[Int]] = new ImmutableSeqProperty[Int, Seq](Seq(1, 2, 3))

      p.get should be(Seq(1,2,3))
      p.elemProperties.map(_.get) should be(Seq(1,2,3))
      p.transformElements(_ + 1).get should be(Seq(2, 3, 4))

      var counter = 0
      p.listen(_ => counter += 1)
      counter should be(0)
      p.listen(_ => counter += 1, initUpdate = true)
      counter should be(1)
      p.listenStructure(_ => counter += 1)
      counter should be(1)
    }

    "support mirrors" in {
      val origin = new ImmutableSeqProperty[Int, Seq](Seq(1, 2, 3))
      val p: SeqProperty[Int, Property[Int]] = origin.mirror().asSeq

      p.get should be(Seq(1,2,3))
      p.elemProperties.map(_.get) should be(Seq(1,2,3))
      p.transformElements(_ + 1).get should be(Seq(2, 3, 4))

      var counter = 0
      p.listen(_ => counter += 1)
      counter should be(0)
      p.listen(_ => counter += 1, initUpdate = true)
      counter should be(1)
      p.listenStructure(_ => counter += 1)
      counter should be(1)

      p.elemProperties.head.set(7)
      p.get should be(Seq(7,2,3))
      counter should be(3)
      origin.listenersCount() should be(0)
    }
  }
}

object ImmutablePropertyTest {
  case class Nested(s: Nested)
  object Nested extends HasModelPropertyCreator[Nested]

  case class ModelEntity(s: String, i: Seq[Int], v: Vector[Int], m: ModelEntity)
  object ModelEntity extends HasModelPropertyCreator[ModelEntity]
}