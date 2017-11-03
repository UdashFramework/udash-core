package io.udash.benchmarks.properties

import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

import io.udash._

object SinglePropertyListeners extends BenchmarkUtils {
  private val properties: Seq[(String, () => (Property[Int], ReadableProperty[Int]))] = Seq(
    ("direct property", () => {
      val p = Property(0)
      (p, p)
    }),
    ("one-way transformed property", () => {
      val p = Property(0)
      val t = p.transform((v: Int) => v + 1)
      (p, t)
    }),
    ("both-ways transformed property", () => {
      val p = Property(0)
      val t = p.transform((v: Int) => v + 1, (v: Int) => v - 1)
      (p, t)
    }),
    ("one-way transformed property with slow transformer", () => {
      val p = Property(0)
      val t = p.transform((v: Int) => slowInc(v))
      (p, t)
    }),
    ("both-ways transformed property with slow transformer", () => {
      val p = Property(0)
      val t = p.transform((v: Int) => slowInc(v), (v: Int) => slowDec(v))
      (p, t)
    }),
  )

  private val benchmarks = generateGetSetListenBenchmarks[Property[Int], ReadableProperty[Int]](properties)(
    Seq(20), Seq(0.1, 1, 10), Seq(0, 1, 10, 100),
    Seq(("simple int set", _.set(_), _.get)),
    Seq(("empty listener", _.listen(_ => ())))
  )

  val suite = GuiSuite(
    Suite("Property - set, get & listen")(benchmarks: _*)
  )
}
