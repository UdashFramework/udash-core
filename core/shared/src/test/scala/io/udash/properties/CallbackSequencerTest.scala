package io.udash.properties

import io.udash.testing.UdashSharedTest

import scala.collection.mutable
import scala.util.Random

class CallbackSequencerTest extends UdashSharedTest {
  "CallbackSequencer" should {
    "fire listeners immediately without sequencing" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "a"
      val l2 = () => fires += "b"
      val l3 = () => fires += "c"

      fires.size should be(0)

      CallbackSequencer.queue("1", l1)
      fires should contain("a")

      CallbackSequencer.queue("2", l2)
      fires should contain("b")

      CallbackSequencer.queue("3", l3)
      fires should contain("c")
    }

    "fire listeners immediately without sequencing (queue id should not matter)" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      fires.size should be(0)

      CallbackSequencer.queue("1", l1)
      fires should contain("l1")

      CallbackSequencer.queue("1", l2)
      fires should contain("l2")

      CallbackSequencer.queue("1", l3)
      fires should contain("l3")
    }

    "fire listener only once with sequencing" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      CallbackSequencer.sequence {
        fires.size should be(0)

        CallbackSequencer.queue("1", l1)
        fires shouldNot contain("l1")

        CallbackSequencer.queue("2", l2)
        fires shouldNot contain("l2")

        CallbackSequencer.queue("3", l3)
        fires shouldNot contain("l3")
      }

      fires.size should be(3)
      fires should contain("l1")
      fires should contain("l2")
      fires should contain("l3")
    }

    "fire listener only once with sequencing (now id should matter)" in {
      val fires = mutable.ArrayBuffer[String]()
      val l1 = () => fires += "l1"
      val l2 = () => fires += "l2"
      val l3 = () => fires += "l3"

      CallbackSequencer.sequence {
        fires.size should be(0)

        CallbackSequencer.queue("1", l1)
        fires shouldNot contain("l1")

        CallbackSequencer.queue("1", l2)
        fires shouldNot contain("l2")

        CallbackSequencer.queue("1", l3)
        fires shouldNot contain("l3")
      }

      fires.size should be(1)
      fires shouldNot contain("l1")
      fires shouldNot contain("l2")
      fires should contain("l3")
    }

    "fire last listener with selected id when sequencing" in {
      val count = Random.nextInt(50000 + 20)
      val fires = mutable.ArrayBuffer[String]()
      val listeners = mutable.ArrayBuffer[() => Any]()
      for (i <- 1 to count) { listeners += (() => fires += s"l$i") }

      listeners.size should be(count)

      CallbackSequencer.sequence {
        listeners.zipWithIndex.foreach { case (l, i) => CallbackSequencer.queue((i % 10).toString, l) }
      }

      fires.size should be(10)
      for (i <- count-9 to count) {
        fires should contain(s"l$i")
      }
    }
  }
}
