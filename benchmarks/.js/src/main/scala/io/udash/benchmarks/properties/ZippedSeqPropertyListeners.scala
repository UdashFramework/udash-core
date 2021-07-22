package io.udash.benchmarks.properties

import com.avsystem.commons.universalOps
import io.udash._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

object ZippedSeqPropertyListeners extends BenchmarkUtils {
  private val seqSize = 50
  private val properties: Seq[(String, () => (SeqProperty[Int], ReadableSeqProperty[Int]))] = Seq(
    ("direct property", () => {
      val p = SeqProperty(Seq.tabulate(seqSize)(identity))
      (p, p)
    }),
    ("zipped elements", () => {
      val p = SeqProperty(Seq.tabulate(seqSize)(identity))
      val t = p.zip(p)(_ + _)
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
    Suite("SeqProperty - zipped - set, get & listen")(benchmarks: _*)
  )
}
