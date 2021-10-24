package io.udash
package rest.raw

import com.avsystem.commons._
import com.avsystem.commons.collection.CrossFactory
import io.udash.rest.raw.AbstractMapping.ConcatSeq

import scala.collection.compat._

/**
 * Represents an immutable, ordered sequence of key-value pairs with textual keys. Mapping additionally holds a lazy
 * initialized map which allows fast lookup by key. When looking up values by key, duplicate entries are dropped and
 * only the last value for given key is returned.
 *
 * Mappings have O(1) prepend, append and concatenation operations.
 */
final case class Mapping[V](entries: ISeq[(String, V)]) extends AbstractMapping[V] {
  type Self = Mapping[V]
  def caseSensitive: Boolean = true

  protected def createNew(entries: ISeq[(String, V)]): Mapping[V] = Mapping(entries)
}
object Mapping extends AbstractMappingCompanion[Mapping]

/**
 * A version of [[Mapping]] which is case-insensitive when looking up values by key.
 * Used primarily to represent [[io.udash.rest.Header Header]] parameter values.
 */
final case class IMapping[V](entries: ISeq[(String, V)]) extends AbstractMapping[V] {
  type Self = IMapping[V]
  def caseSensitive: Boolean = false

  protected def createNew(entries: ISeq[(String, V)]): IMapping[V] = IMapping(entries)
}
object IMapping extends AbstractMappingCompanion[IMapping]

sealed abstract class AbstractMapping[V] extends PartialFunction[String, V] {
  type Self >: this.type <: AbstractMapping[V]

  def entries: ISeq[(String, V)]
  def caseSensitive: Boolean

  protected def createNew(rawEntries: ISeq[(String, V)]): Self

  private def normKey(key: String): String =
    if (caseSensitive) key else key.toLowerCase

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
    else createNew(new ConcatSeq(entries, List((key, value))))

  def prepend(key: String, value: V): Self =
    if (entries.isEmpty) createNew(List((key, value)))
    else createNew(new ConcatSeq(List((key, value)), entries))

  def ++(other: Self): Self =
    if (entries.isEmpty) other
    else if (other.entries.isEmpty) this
    else createNew(new ConcatSeq(entries, other.entries))

  override def hashCode(): Int =
    entries.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case mapping: AbstractMapping[_] =>
      caseSensitive == mapping.caseSensitive && entries == mapping.entries
    case _ => false
  }

  override def toString(): String = {
    val pref = if (caseSensitive) "Mapping(" else "IMapping("
    entries.iterator.map({ case (k, v) => s"$k->$v" }).mkString(pref, ",", ")")
  }
}

object AbstractMapping {
  private class ConcatSeq[+V](first: ISeq[V], second: ISeq[V]) extends ISeq[V] {
    require(first.nonEmpty && second.nonEmpty)

    def length: Int = first.length + second.length
    def apply(idx: Int): V = if (idx < first.length) first(idx) else second(idx)
    def iterator: Iterator[V] = first.iterator ++ second.iterator

    override def isEmpty: Boolean = false
  }
}

abstract class AbstractMappingCompanion[M[V] <: AbstractMapping[V]] { companion =>
  def apply[V](entries: ISeq[(String, V)]): M[V]
  def create[V](entries: (String, V)*): M[V] = apply(entries)

  def empty[V]: M[V] = create()

  def newBuilder[V]: MBuilder[(String, V), M[V]] =
    new MListBuffer[(String, V)].mapResult(create)

  private val reusableFactory = new CrossFactory[(String, Any), M[Any]] {
    def fromSpecific(it: IterableOnce[(String, Any)]): M[Any] = {
      val b = newBuilder
      b ++= it
      b.result()
    }

    def newBuilder: MBuilder[(String, Any), M[Any]] = companion.newBuilder
  }

  implicit def canBuildFrom[V]: Factory[(String, V), M[V]] =
    reusableFactory.asInstanceOf[Factory[(String, V), M[V]]]

  implicit def toFactory[V](companion: AbstractMappingCompanion[M]): Factory[(String, V), M[V]] =
    companion.canBuildFrom[V]
}
