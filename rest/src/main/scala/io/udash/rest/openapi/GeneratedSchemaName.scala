package io.udash.rest.openapi

import com.avsystem.commons.Opt
import com.avsystem.commons.misc.{AnnotationOf, SimpleClassName}
import com.avsystem.commons.serialization.name

/**
 * A metadata typeclass that you can use to control names of [[RestSchema]]s macro-materialized
 * for ADTs (case classes & sealed hierarchies).
 *
 * [[GeneratedSchemaName]] is captured by [[RestStructure]] which is then used to construct
 * a [[RestSchema]] instance.
 *
 * Customizing this implicit is mostly useful when used collectively, i.e. you can derive schema name
 * as fully qualified class name from a `ClassTag`, in a generic `implicit def`.
 *
 * @param name the schema name or `Opt.Empty` for an unnamed (inline) schema
 */
final case class GeneratedSchemaName[T](name: Opt[String])
object GeneratedSchemaName {
  def apply[T](implicit sn: GeneratedSchemaName[T]): GeneratedSchemaName[T] = sn
  def of[T](implicit sn: GeneratedSchemaName[T]): Opt[String] = sn.name

  def none[T]: GeneratedSchemaName[T] = GeneratedSchemaName(Opt.Empty)
  def some[T](name: String): GeneratedSchemaName[T] = GeneratedSchemaName(Opt(name))

  implicit def annotSchemaName[T](implicit nameAnnot: AnnotationOf[name, T]): GeneratedSchemaName[T] =
    GeneratedSchemaName.some(nameAnnot.annot.name)

  implicit def classSchemaName[T: SimpleClassName]: GeneratedSchemaName[T] =
    GeneratedSchemaName.some(SimpleClassName.of[T])
}
