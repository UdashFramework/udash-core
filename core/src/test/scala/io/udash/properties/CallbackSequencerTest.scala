package io.udash.properties

import io.udash.testing.UdashCoreTest

import scala.collection.mutable
import scala.util.Random

class CallbackSequencerTest extends UdashCoreTest {
  "CallbackSequencer" should {
    "fire listeners immediately without sequencing" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "a"
      val l2 = () => fires += "b"
      val l3 = () => fires += "c"

      fires shouldBe empty

      CallbackSequencer().queue("1", l1)
      fires should contain theSameElementsInOrderAs Seq("a")

      CallbackSequencer().queue("2", l2)
      fires should contain theSameElementsInOrderAs Seq("a", "b")

      CallbackSequencer().queue("3", l3)
      fires should contain theSameElementsInOrderAs Seq("a", "b", "c")
    }

    "fire listeners immediately without sequencing (queue id should not matter)" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      fires shouldBe empty

      CallbackSequencer().queue("1", l1)
      fires should contain theSameElementsInOrderAs Seq("l1")

      CallbackSequencer().queue("1", l2)
      fires should contain theSameElementsInOrderAs Seq("l1", "l2")

      CallbackSequencer().queue("1", l3)
      fires should contain theSameElementsInOrderAs Seq("l1", "l2", "l3")
    }

    "fire listener only once with sequencing" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      CallbackSequencer().sequence {
        fires shouldBe empty

        CallbackSequencer().queue("1", l1)
        fires shouldNot contain("l1")

        CallbackSequencer().queue("2", l2)
        fires shouldNot contain("l2")

        CallbackSequencer().queue("3", l3)
        fires shouldNot contain("l3")
      }

      fires should contain theSameElementsInOrderAs Seq("l1", "l2", "l3")
    }

    "fire listener only once with sequencing (now id should matter)" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      CallbackSequencer().sequence {
        fires shouldBe empty

        CallbackSequencer().queue("1", l1)
        fires shouldBe empty

        CallbackSequencer().queue("1", l2)
        fires shouldBe empty

        CallbackSequencer().queue("1", l3)
        fires shouldBe empty
      }

      fires.size should be(1)
      fires should contain theSameElementsInOrderAs Seq("l3")
    }

    "fire last listener with selected id when sequencing" in {
      val count = Random.nextInt(50000 + 20)
      val fires = mutable.ArrayBuffer[String]()
      val listeners = mutable.ArrayBuffer[() => Any]()
      for (i <- 1 to count) { listeners += (() => fires += s"l$i") }

      listeners.size should be(count)

      CallbackSequencer().sequence {
        listeners.zipWithIndex.foreach { case (l, i) => CallbackSequencer().queue((i % 10).toString, l) }
      }

      fires.size should be(10)
      for (i <- count-9 to count) {
        fires should contain(s"l$i")
      }
    }

    "fire listeners queued by listeners" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "a"
      val l2a = () => CallbackSequencer().queue("2a", () => fires += "b")
      val l3 = () => fires += "c"
      val lz = () => CallbackSequencer().queue("z", () => fires += "z")

      fires shouldBe empty

      CallbackSequencer().queue("1", l1)
      fires should contain theSameElementsInOrderAs Seq("a")

      CallbackSequencer().queue("2", l2a)
      fires should contain theSameElementsInOrderAs Seq("a", "b")

      CallbackSequencer().queue("3", l3)
      fires should contain theSameElementsInOrderAs Seq("a", "b", "c")

      CallbackSequencer().queue("z", lz) //same id won't be triggered again
      fires should contain theSameElementsInOrderAs Seq("a", "b", "c")
    }

    "fire listeners queued by sequenced listeners" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "a"
      val l2a = () => CallbackSequencer().queue("2a", () => fires += "b")
      val l3 = () => fires += "c"
      val lz = () => CallbackSequencer().queue("z", () => fires += "z")

      fires shouldBe empty

      CallbackSequencer().sequence {
        CallbackSequencer().queue("1", l1)
        fires shouldBe empty

        CallbackSequencer().queue("2", l2a)
        fires shouldBe empty

        CallbackSequencer().queue("3", l3)
        fires shouldBe empty

        CallbackSequencer().queue("z", lz)
        fires shouldBe empty
      }

      fires should contain theSameElementsInOrderAs Seq("a", "c", "b")
    }
  }
}
