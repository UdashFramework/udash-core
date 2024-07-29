package io.udash.rest.openapi

import com.avsystem.commons.{Opt, StaticAnnotation}
import com.avsystem.commons.annotation.NotInheritedFromSealedTypes
import com.avsystem.commons.misc.{AnnotationOf, SimpleClassName}
import com.avsystem.commons.serialization.name

/**
 * Can be used on case class or sealed hierarchy root to instruct OpenAPI schema derivation mechanism
 * to use particular value as [[RestSchema]] name instead of just using class name.
 *
 * Takes precedence over [[com.avsystem.commons.serialization.name]], but unlike
 * [[com.avsystem.commons.serialization.name]] does <b>NOT</b> change type discriminator value
 * when the class is a part of a sealed hierarchy. This is useful to resolve schema name conflicts
 * with other classes without changing serialized representation.
 */
class schemaName(val name: String) extends StaticAnnotation with NotInheritedFromSealedTypes

/**
 * A metadata typeclass that you can use to control names of [[RestSchema]]s macro-materialized
 * for ADTs (case classes & sealed hierarchies).
 *
 * [[GeneratedSchemaName]] is captured by [[RestStructure]] which is then used to construct
 * a [[RestSchema]] instance.
 *
 * Customizing this implicit is mostly useful when used collectively, i.e. you can derive schema name
 * as fully qualified class name from a `ClassTag`, in a generic `implicit def`. Example:
 *
 * {{{
 *   import io.udash.rest._
 *   import io.udash.rest.openapi._
 *
 *   trait FullyQualifiedRestImplicits extends DefaultRestImplicits {
 *     implicit def fullyQualifiedSchemaName[T](implicit ct: ClassTag[T]): GeneratedSchemaName[T] =
 *       GeneratedSchemaName.some(ct.runtimeClass.getCanonicalName)
 *   }
 *   object FullyQualifiedRestImplicits extends FullyQualifiedRestImplicits
 *
 *   abstract class FullyQualifiedRestDataCompanion[T](implicit
 *     instances: MacroInstances[FullyQualifiedRestImplicits, CodecWithStructure[T]]
 *   ) extends AbstractRestDataCompanion[FullyQualifiedRestImplicits, T](FullyQualifiedRestImplicits)
 *
 *   // USAGE
 *   case class Person(name: String, birthYear: Int)
 *   object Person extends FullyQualifiedRestDataCompanion[Person]
 * }}}
 *
 * @param name the schema name or `Opt.Empty` for an unnamed (inline) schema
 */
final case class GeneratedSchemaName[T](name: Opt[String])
object GeneratedSchemaName extends GeneratedSchemaNameLowPrio {
  def apply[T](implicit sn: GeneratedSchemaName[T]): GeneratedSchemaName[T] = sn
  def of[T](implicit sn: GeneratedSchemaName[T]): Opt[String] = sn.name

  /**
   * Creates an empty [[GeneratedSchemaName]], which indicates that a [[RestSchema]] based on
   * [[RestStructure]] should be anonymous.
   */
  def none[T]: GeneratedSchemaName[T] = GeneratedSchemaName(Opt.Empty)

  /**
   * Creates a [[GeneratedSchemaName]] with given name, which specifies the name of [[RestSchema]]
   * based on [[RestStructure]] for given type.
   */
  def some[T](name: String): GeneratedSchemaName[T] = GeneratedSchemaName(Opt(name))

  implicit def annotSchemaName[T](implicit schemaNameAnnot: AnnotationOf[schemaName, T]): GeneratedSchemaName[T] =
    GeneratedSchemaName.some(schemaNameAnnot.annot.name)
}

trait GeneratedSchemaNameLowPrio extends GeneratedSchemaNameLowestPrio { this: GeneratedSchemaName.type =>
  implicit def annotName[T](implicit nameAnnot: AnnotationOf[name, T]): GeneratedSchemaName[T] =
    GeneratedSchemaName.some(nameAnnot.annot.name)
}

trait GeneratedSchemaNameLowestPrio { this: GeneratedSchemaName.type =>
  implicit def classSchemaName[T: SimpleClassName]: GeneratedSchemaName[T] =
    GeneratedSchemaName.some(SimpleClassName.of[T])
}
