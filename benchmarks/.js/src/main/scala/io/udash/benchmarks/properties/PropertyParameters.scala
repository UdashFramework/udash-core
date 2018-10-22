package io.udash.benchmarks.properties

import io.udash._
import japgolly.scalajs.benchmark._
import japgolly.scalajs.benchmark.gui._
import scalatags.JsDom.all._

object PropertyParameters {
  case class Entity(i: Int, s: String, r: Entity)
  object Entity extends HasModelPropertyCreator[Entity]

  private def listenProperty(p: ReadableProperty[String]) = {
    val r = p.listen(_ => ())
    p.get + r.isActive.toString
  }

  private def listenModelProperty(p: ReadableModelProperty[Entity]) = {
    val r = p.listen(_ => ())
    p.roSubProp(_.r.r.s).get + r.isActive.toString
  }

  private def listenSeqProperty(p: ReadableSeqProperty[String]) = {
    val r = p.listen(_ => ())
    val r2 = p.listenStructure(_ => ())
    p.elemProperties(2).get + r.isActive.toString + r2.isActive.toString
  }

  private val createStandardProperty = Benchmark("create a standard property") {
    for (_ <- 1 until 1000) Property("asd")
  }

  private val listenStandardProperty = Benchmark("listen to a standard property") {
    for (_ <- 1 until 1000) listenProperty(Property("asd"))
  }

  private val renderDiv = Benchmark("render a div") {
    for (_ <- 1 until 1000) div().render
  }

  private val renderStandardProperty = Benchmark("render a standard property") {
    for (_ <- 1 until 1000) div(bind(Property("asd"))).render
  }

  private val listenImmutableProperty = Benchmark("listen to an immutable property") {
    for (_ <- 1 until 1000) listenProperty("asd".toProperty)
  }

  private val listenStandardModelProperty = Benchmark("listen to a standard model property") {
    for (_ <- 1 until 1000) listenModelProperty(ModelProperty(Entity(5, "asd", Entity(5, "asd", Entity(5, "asd", null)))))
  }

  private val listenImmutableModelProperty = Benchmark("listen to an immutable model property") {
    for (_ <- 1 until 1000) listenModelProperty(Entity(5, "asd", Entity(5, "asd", Entity(5, "asd", null))).toModelProperty)
  }

  private val listenStandardSeqProperty = Benchmark("listen to a standard seq property") {
    for (_ <- 1 until 1000) listenSeqProperty(SeqProperty("A", "B", "C"))
  }

  private val listenImmutableSeqProperty = Benchmark("listen to an immutable seq property") {
    for (_ <- 1 until 1000) listenSeqProperty(Seq("A", "B", "C").toSeqProperty)
  }

  val suite = GuiSuite(
    Suite("PropertyParameters")(
      createStandardProperty,
      listenStandardProperty,
      renderDiv,
      renderStandardProperty,
      listenImmutableProperty,
      listenStandardModelProperty,
      listenImmutableModelProperty,
      listenStandardSeqProperty,
      listenImmutableSeqProperty
    )
  )
}
