package io.udash.bindings.modifiers

import com.avsystem.commons._
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import org.scalajs.dom._

import scala.collection.mutable

private[bindings]
class SeqPropertyWithIndexModifier[T, E <: ReadableProperty[T]](override val property: ReadableSeqProperty[T, E],
                                                                builder: (E, ReadableProperty[Int], Binding.NestedInterceptor) => BSeq[Node],
                                                                override val customElementsReplace: DOMManipulator.ReplaceMethod,
                                                                override val customElementsInsert: DOMManipulator.InsertMethod)
  extends SeqPropertyModifierUtils[T, E] {

  private val indexes: mutable.HashMap[E, Property[Int]] = mutable.HashMap.empty

  protected def indexProperty(p: E): ReadableProperty[Int] =
    if (indexes.contains(p)) indexes(p)
    else Property(0).setup { indexes(p) = _ }

  override protected def build(item: E): BSeq[Node] =
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

  override def applyTo(root: Element): Unit = {
    super.applyTo(root)
    property.elemProperties.zipWithIndex.foreach { case (p, i) =>
      indexes(p).set(i)
    }
  }
}


