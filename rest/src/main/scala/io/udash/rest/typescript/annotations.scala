package io.udash.rest.typescript

import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.serialization.optionalParam

import scala.annotation.StaticAnnotation

/**
 * Use this annotation on case class field or REST API method parameter to instruct the TypeScript generator
 * to emit an optional field or parameter.
 *
 * Parameter annotated as `@tsOptional` must be typed as an `Option`, `Opt`, `OptArg`, etc.
 * Its TypeScript type will be derived from the type wrapped into the option-like wrapper. For example,
 * `@tsOptional` parameter of type `Opt[Int]` will have TypeScript type `number` (not `number | null`
 * which would be the case if the parameter wasn't marked as optional).
 */
class tsOptional extends AnnotationAggregate {
  @optionalParam type Implied
}

class tsMutable(val mutable: Boolean = true) extends StaticAnnotation
