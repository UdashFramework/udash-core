package io.udash
package rest.raw

import java.util.Locale

import com.avsystem.commons._
import io.udash.rest.raw.AbstractMapping.ConcatIterable

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable

sealed abstract class AbstractMapping[V] extends PartialFunction[String, V] {
  type Self >: this.type <: AbstractMapping[V]

  def entries: IIterable[(String, V)]
  def caseSensitive: Boolean

  protected def createNew(rawEntries: IIterable[(String, V)]): Self

  private def normKey(key: String): String =
    if (caseSensitive) key else key.toLowerCase(Locale.ENGLISH)

  lazy val toMap: IMap[String, V] =
    entries.iterator.map { case (name, value) => (normKey(name), value) }.toMap

  def isEmpty: Boolean = entries.isEmpty
  def nonEmpty: Boolean = entries.nonEmpty
  def iterator: Iterator[(String, V)] = entries.iterator

  def isDefinedAt(key: String): Boolean =
    toMap.contains(normKey(key))
  def apply(key: String): V =
    toMap(normKey(key))

  override def applyOrElse[A <: String, B >: V](key: A, default: A => B): B =
    if (caseSensitive) toMap.applyOrElse(key, default)
    else toMap.applyOrElse(normKey(key), (_: String) => default(key))

  def append(key: String, value: V): Self =
    if (entries.isEmpty) createNew(List((key, value)))
    else createNew(new ConcatIterable(entries, List((key, value))))

  def prepend(key: String, value: V): Self =
    if (entries.isEmpty) createNew(List((key, value)))
    else createNew(new ConcatIterable(List((key, value)), entries))

  def ++(other: Self): Self =
    if (entries.isEmpty) other
    else if (other.entries.isEmpty) this
    else createNew(new ConcatIterable(entries, other.entries))

  override def hashCode(): Int =
    entries.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case mapping: AbstractMapping[_] if mapping.caseSensitive == caseSensitive =>
      entries == mapping.entries
    case _ => false
  }

  override def toString(): String = super.toString()
}

object AbstractMapping {
  private class ConcatIterable[+V](first: IIterable[V], second: IIterable[V]) extends IIterable[V] {
    def iterator: Iterator[V] = first.iterator ++ second.iterator
  }
}

abstract class AbstractMappingCompanion[M[V] <: AbstractMapping[V]] {
  def apply[V](entries: IIterable[(String, V)]): M[V]

  def empty[V]: M[V] = apply()
  def apply[V](entries: (String, V)*): M[V] = apply(entries.toList)

  def newBuilder[V]: mutable.Builder[(String, V), M[V]] =
    new MListBuffer[(String, V)].mapResult(apply(_))

  implicit def canBuildFrom[V]: CanBuildFrom[Nothing, (String, V), M[V]] =
    new CanBuildFrom[Nothing, (String, V), M[V]] {
      def apply(from: Nothing): mutable.Builder[(String, V), M[V]] = newBuilder
      def apply(): mutable.Builder[(String, V), M[V]] = newBuilder
    }
}

final case class Mapping[V](entries: IIterable[(String, V)]) extends AbstractMapping[V] {
  type Self = Mapping[V]
  def caseSensitive: Boolean = true

  protected def createNew(entries: IIterable[(String, V)]): Mapping[V] = Mapping(entries)
}
object Mapping extends AbstractMappingCompanion[Mapping]

/**
  * A version of [[Mapping]] which is case-insensitive when looking up values by key.
  */
final case class IMapping[V](entries: IIterable[(String, V)]) extends AbstractMapping[V] {
  type Self = IMapping[V]
  def caseSensitive: Boolean = false

  protected def createNew(entries: IIterable[(String, V)]): IMapping[V] = IMapping(entries)
}
object IMapping extends AbstractMappingCompanion[IMapping]
