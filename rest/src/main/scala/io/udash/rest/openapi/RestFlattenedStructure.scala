package io.udash.rest
package openapi

import com.avsystem.commons.Opt

object RestFlattenedStructure {
  /**
   * Takes existing [[RestStructure]] for a class and creates [[RestSchema]] as it would look when the class is a part
   * of sealed hierarchy (forces inclusion of discriminator field)
   */
  def caseRestSchema[T](structure: => RestStructure[T], caseFieldName: => String): RestSchema[T] =
    RestSchema.lazySchema(structure match {
      case caseStructure: RestStructure.Case[T] => caseStructure.caseSchema(Opt(caseFieldName))
      case _ => throw new IllegalArgumentException("expected Record or Singleton REST structure")
    })
}
