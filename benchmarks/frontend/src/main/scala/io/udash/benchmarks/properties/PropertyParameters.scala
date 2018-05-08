package io.udash.benchmarks.properties

import io.udash._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._

object PropertyParameters {
  case class Entity(i: Int, s: String, r: Entity)
  object Entity extends HasModelPropertyCreator[Entity]

  private def renderProperty(p: ReadableProperty[String]) = {
    val r = p.listen(_ => ())
    p.get + r.isActive.toString
  }

  private def renderModelProperty(p: ReadableModelProperty[Entity]) = {
    val r = p.listen(_ => ())
    p.roSubProp(_.r.r.s).get + r.isActive.toString
  }

  private def renderSeqProperty(p: ReadableSeqProperty[String]) = {
    val r = p.listen(_ => ())
    val r2 = p.listenStructure(_ => ())
    p.elemProperties(2).get + r.isActive.toString + r2.isActive.toString
  }

  val bindStandardProperty = Benchmark("bind to a standard property") {
    for (_ <- 1 until 1000) renderProperty(Property("asd"))
  }

  val bindImmutableProperty = Benchmark("bind to an immutable property") {
    for (_ <- 1 until 1000) renderProperty("asd")
  }

  val bindStandardModelProperty = Benchmark("bind to a standard model property") {
    for (_ <- 1 until 1000) renderModelProperty(ModelProperty(Entity(5, "asd", Entity(5, "asd", Entity(5, "asd", null)))))
  }

  val bindImmutableModelProperty = Benchmark("bind to an immutable model property") {
    for (_ <- 1 until 1000) renderModelProperty(Entity(5, "asd", Entity(5, "asd", Entity(5, "asd", null))))
  }

  val bindStandardSeqProperty = Benchmark("bind to a standard seq property") {
    for (_ <- 1 until 1000) renderSeqProperty(SeqProperty("A", "B", "C"))
  }

  val bindImmutableSeqProperty = Benchmark("bind to an immutable seq property") {
    for (_ <- 1 until 1000) renderSeqProperty(Seq("A", "B", "C"))
  }

  val suite = GuiSuite(
    Suite("PropertyParameters")(
      bindStandardProperty,
      bindImmutableProperty,
      bindStandardModelProperty,
      bindImmutableModelProperty,
      bindStandardSeqProperty,
      bindImmutableSeqProperty
    )
  )
}
