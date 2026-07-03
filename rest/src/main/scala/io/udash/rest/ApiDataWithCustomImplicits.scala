package io.udash.rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.misc.AnnotationOf
import com.avsystem.commons.serialization.{GenCodec, GenObjectCodec, flatten}
import io.udash.rest.openapi.{HasRestSchema, RestFlattenedStructure, RestSchema, RestStructure}
import io.udash.rest.util.CaseNameValidatingCodec

import scala.reflect.ClassTag

trait PolyCodecWithStructure[C[_]] {
  def codec[T: GenCodec]: GenCodec[C[T]]
  def structure[T: RestSchema]: RestStructure[C[T]]
}

trait PolyObjectCodecWithStructure[C[_]] {
  def codec[T: GenCodec]: GenObjectCodec[C[T]]
  def structure[T: RestSchema]: RestStructure[C[T]]
}

trait Poly2CodecWithStructure[C[_, _]] {
  def codec[T1: GenCodec, T2: GenCodec]: GenCodec[C[T1, T2]]
  def structure[T1: RestSchema, T2: RestSchema]: RestStructure[C[T1, T2]]
}

/** TODO doc */
trait ApiDataWithCustomImplicits[Implicits] {

  protected def implicits: Implicits

  /**
   * Companion for case classes and sealed hierarchies used in REST APIs.
   */
  abstract class ApiDataCompanion[T](
    implicit instances: MacroInstances[Implicits, CodecWithStructure[T]]
  ) extends HasRestSchema[T] {
    implicit lazy val codec: GenCodec[T] = instances(implicits, this).codec
    implicit lazy val restStructure: RestStructure[T] = instances(implicits, this).structure
    implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.standaloneSchema)
  }

  /**
   * A version of [[ApiDataCompanion]] which injects additional implicits into macro materialization. Implicits are imported
   * from an object specified with type parameter `D`. It must be a singleton object type, i.e. `SomeObject.type`.
   */
  abstract class ApiDataCompanionWithDeps[D, T](
    implicit deps: ValueOf[D],
    instances: MacroInstances[D, CodecWithStructure[T]],
  ) extends HasRestSchema[T] {
    implicit val codec: GenCodec[T] = instances(deps.value, this).codec
    implicit lazy val restStructure: RestStructure[T] = instances(deps.value, this).structure
    implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.standaloneSchema)
  }

  /**
   * Companion for generic case classes and sealed hierarchies used in REST APIs.
   * This only works with types that have exactly one, unbounded type parameter.
   * However, you can relatively easily create a similar base companion class for any other parameterization
   * scheme (e.g. two or more generics with possible bounds etc).
   */
  abstract class PolyApiDataCompanion[C[_]](implicit instances: MacroInstances[Implicits, PolyCodecWithStructure[C]]) {
    implicit def codec[T: GenCodec]: GenCodec[C[T]] = instances(implicits, this).codec
    implicit def restStructure[T: RestSchema]: RestStructure[C[T]] = instances(implicits, this).structure
    implicit def restSchema[T: RestSchema]: RestSchema[C[T]] = restStructure[T].standaloneSchema.unnamed
  }

  abstract class PolyObjectApiDataCompanion[C[_]](implicit instances: MacroInstances[Implicits, PolyObjectCodecWithStructure[C]]) {
    implicit def codec[T: GenCodec]: GenObjectCodec[C[T]] = instances(implicits, this).codec
    implicit def restStructure[T: RestSchema]: RestStructure[C[T]] = instances(implicits, this).structure
    implicit def restSchema[T: RestSchema]: RestSchema[C[T]] = restStructure[T].standaloneSchema.unnamed
  }

  abstract class Poly2ApiDataCompanion[C[_, _]](implicit instances: MacroInstances[Implicits, Poly2CodecWithStructure[C]]) {
    implicit def codec[T1: GenCodec, T2: GenCodec]: GenCodec[C[T1, T2]] = instances(implicits, this).codec
    implicit def restStructure[T1: RestSchema, T2: RestSchema]: RestStructure[C[T1, T2]] = instances(implicits, this).structure
    implicit def restSchema[T1: RestSchema, T2: RestSchema]: RestSchema[C[T1, T2]] = restStructure[T1, T2].standaloneSchema.unnamed
  }

  /**
   * Use this class as base for companions of case classes in flat sealed hierarchies (i.e. the ones with
   * a discriminator field specified with [[flatten]] annotation) if you want the codec and schema for the case class
   * _itself_ to include the discriminator field as well.
   */
  abstract class ApiSealedCaseCompanion[T, R >: T](
    implicit rootCodec: GenCodec[R],
    rootFlattenAnnot: AnnotationOf[flatten, R],
    instances: MacroInstances[Implicits, () => RestStructure[T]],
  ) extends HasRestSchema[T] {
    private lazy val structure = instances(implicits, this).apply()

    implicit lazy val codec: GenCodec[T] =
      CaseNameValidatingCodec(rootCodec, caseFieldName = caseFieldName, caseName = caseName)
    implicit lazy val restSchema: RestSchema[T] =
      RestFlattenedStructure.caseRestSchema(structure, caseFieldName)

    private def caseFieldName: String = rootFlattenAnnot.annot.caseFieldName
    private def caseName: String = structure.info.rawName
  }

  /** TODO doc */
  abstract class ApiSealedSubHierarchyCompanion[T: ClassTag, R >: T](
    implicit rootCodec: GenCodec[R],
    instances: MacroInstances[Implicits, () => RestStructure[T]],
  ) extends HasRestSchema[T] {
    implicit lazy val codec: GenCodec[T] = new GenCodec.SubclassCodec[T, R](nullable = true)
    implicit lazy val restStructure: RestStructure[T] = instances(implicits, this).apply()
    implicit lazy val restSchema: RestSchema[T] = RestSchema.lazySchema(restStructure.standaloneSchema)
  }
}
