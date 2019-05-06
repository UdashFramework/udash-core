package io.udash.properties

import io.udash.properties.model.ModelProperty
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single.Property
import io.udash.testing.UdashCoreTest

class ReadableWrapperTest extends UdashCoreTest {
  "ReadableWrapper" should {
    "not allow access to the mutable API of Property" in {
      val p = Property[Int](7)
      p.readable match {
        case p: Property[Int] => p.set(42)
        case _ => // ignore
      }
      p.get should be(7)
      (p.readable eq p.readable.readable) should be(true)
    }

    "not allow access to the mutable API of ModelProperty" in {
      val p = ModelProperty[(Int, Int)]((7, 42))(ModelPropertyCreator.materialize)
      p.readable match {
        case p: ModelProperty[(Int, Int)] => p.set((42, 7))
        case _ => // ignore
      }
      p.get should be((7, 42))
      (p.readable eq p.readable.readable) should be(true)
    }

    "not allow access to the mutable API of SeqProperty" in {
      val p = SeqProperty(7, 42)
      val head = p.elemProperties.head
      p.readable match {
        case p: SeqProperty[Int, _] => p.append(52)
        case _ => // ignore
      }
      p.readable.elemProperties.head match {
        case p: Property[Int] => p.set(3)
        case _ => // ignore
      }
      p.readable.listenStructure { patch =>
        patch.added.foreach {
          case p: Property[Int] => p.set(3)
          case _ => // ignore
        }
        patch.removed.foreach {
          case p: Property[Int] => p.set(3)
          case _ => // ignore
        }
      }
      p.replace(0, 1, 12)
      p.get should be(Seq(12, 42))
      head.get should be(7)
      (p.readable eq p.readable.readable) should be(true)
    }
  }
}
