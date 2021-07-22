package io.udash.benchmarks.properties

import com.avsystem.commons.universalOps
import io.udash._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

object ModelPropertyListeners extends BenchmarkUtils {
  private val properties: Seq[(String, () => (ModelProperty[ModelItem], ReadableProperty[ModelItem]))] = Seq(
    ("direct model property", () => {
      val p = ModelProperty(ModelItem.random)
      (p, p)
    }),
    ("one-way transformed property", () => {
      val p = ModelProperty(ModelItem.random)
      val t = p.transform(v => v.copy(i = v.i + 1))
      (p, t)
    }),
    ("both-ways transformed property", () => {
      val p = ModelProperty(ModelItem.random)
      val t = p.bitransform(v => v.copy(i = v.i + 1))(v => v.copy(i = v.i - 1))
      (p, t)
    }),
    ("one-way transformed property with slow transformer", () => {
      val p = ModelProperty(ModelItem.random)
      val t = p.transform(v => v.copy(i = slowInc(v.i)))
      (p, t)
    }),
    ("both-ways transformed property with slow transformer", () => {
      val p = ModelProperty(ModelItem.random)
      val t = p.bitransform(v => v.copy(i = slowInc(v.i)))(v => v.copy(i = slowDec(v.i)))
      (p, t)
    })
  )

  private val benchmarks = generateGetSetListenBenchmarks[ModelProperty[ModelItem], ReadableProperty[ModelItem]](properties)(
    Seq(20), Seq(0.1, 1, 10), Seq(0, 1, 10, 100),
    Seq(
      ("whole element set", (p, _) => p.set(ModelItem.random), _.get),
      ("subProp set", _.subProp(_.i).set(_), _.get)
    ),
    Seq(("empty listener", _.listen(_ => ()).discard))
  )

  val suite = GuiSuite(
    Suite("ModelProperty - set, get & listen")(benchmarks: _*)
  )
}
