package io.udash.benchmarks.properties

import com.avsystem.commons.universalOps
import io.udash._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

object FilteredSeqPropertyListeners extends BenchmarkUtils {
  val seqSize = 50
  val properties: Seq[(String, () => (SeqProperty[Int], ReadableSeqProperty[Int]))] = Seq(
    ("direct property", () => {
      val p = SeqProperty(Seq.tabulate(seqSize)(identity))
      (p, p)
    }),
    ("filtered elements", () => {
      val p = SeqProperty(Seq.tabulate(seqSize)(identity))
      val t = p.filter(_ % 2 == 0)
      (p, t)
    }),
    ("filtered elements (slow filter)", () => {
      val p = SeqProperty(Seq.tabulate(seqSize)(identity))
      val t = p.filter(v => slowInc(v) % 2 == 0)
      (p, t)
    })
  )

  private val benchmarks = generateGetSetListenBenchmarks[SeqProperty[Int], ReadableSeqProperty[Int]](properties)(
    Seq(20), Seq(0.1, 1, 10), Seq(0, 1, 10, 100),
    Seq(
      ("whole Seq set", (p, i) => p.set(Seq.tabulate(seqSize)(_ + i)), _.get),
      ("replace part of Seq", replaceElements, _.get)
    ),
    Seq(("empty listener", _.listen(_ => ()).discard))
  )

  val suite = GuiSuite(
    Suite("SeqProperty - filter - set, get & listen")(benchmarks: _*)
  )
}
