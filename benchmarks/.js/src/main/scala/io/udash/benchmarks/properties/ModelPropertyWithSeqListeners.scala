package io.udash
package benchmarks.properties

import com.avsystem.commons.universalOps
import io.udash.properties.ModelPropertyCreator
import japgolly.scalajs.benchmark.gui.GuiSuite
import japgolly.scalajs.benchmark.{Benchmark, Suite}

object ModelPropertyWithSeqListeners extends BenchmarkUtils {
  private def properties[T <: ModelWithSeqItem : ModelPropertyCreator](model: T): Seq[(String, () => (ModelProperty[T], ReadableProperty[T]))] = Seq(
    ("direct Seq model property", () => {
      val p = ModelProperty(model)
      (p, p)
    })
  )

  private def benchmarks[T <: ModelWithSeqItem : ModelPropertyCreator](model: T, seqLabel: String): Seq[Benchmark[Unit]] =
    generateGetSetListenBenchmarks[ModelProperty[T], ReadableProperty[T]](properties[T](model))(
      Seq(10), Seq(0.1, 1, 10), Seq(1, 10, 100),
      Seq(
        (s"whole element set on $seqLabel", (p, _) => p.set(model), _.get),
        (s"subProp set on $seqLabel", _.subProp(_.i).set(_), _.get),
        (s"subSeq set on $seqLabel", (p, _) => p.subSeq(_.seq).set(1 to 10, force = true), _.get),
        (s"subSeq replace on $seqLabel", (p, i) => p.subSeq(_.seq).replace(0, 10, i to i + 10: _*), _.get)
      ),
      Seq(("empty listener", _.listen(_ => ()).discard))
    )

  val suite = GuiSuite(
    Suite("Seq ModelProperty - set, get & listen")(
      benchmarks(ModelWithBSeqItem.random, "BSeq") ++
        benchmarks(ModelWithISeqItem.random, "ISeq"): _*
    )
  )
}
