package io.udash.bindings.modifiers

import com.avsystem.commons.SharedExtensions._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import org.scalajs.dom._

import scala.collection.mutable

private[bindings] final class SeqPropertyWithIndexModifier[T, E <: ReadableProperty[T]](
  override val property: ReadableSeqProperty[T, E],
  builder: (E, ReadableProperty[Int], Binding.NestedInterceptor) => Seq[Node],
  override val customElementsReplace: DOMManipulator.ReplaceMethod,
  override val customElementsInsert: DOMManipulator.InsertMethod
) extends SeqPropertyModifierUtils[T, E] {

  private val indexes: mutable.HashMap[E, Property[Int]] = mutable.HashMap.empty

  protected def indexProperty(p: E): ReadableProperty[Int] =
    indexes.getOrElseUpdate(p, Property(property.elemProperties.indexOf(p).applyIf(_ == -1)(_ => 0)))

  override protected def build(item: E): Seq[Node] =
    builder(item, indexProperty(item), propertyAwareNestedInterceptor(item))

  override protected def handlePatch(root: Node)(patch: Patch[E]): Unit = {
    super.handlePatch(root)(patch)
    patch.removed.foreach(indexes.remove)
    property.elemProperties.zipWithIndex.drop(patch.idx).foreach { case (p, i) =>
      indexes(p).set(i)
    }
  }

  override def kill(): Unit = {
    super.kill()
    indexes.clear()
  }
}
