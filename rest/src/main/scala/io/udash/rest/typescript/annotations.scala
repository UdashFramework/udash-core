package io.udash.rest.typescript

import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.serialization.{transientDefault, whenAbsent}

import scala.annotation.StaticAnnotation

/**
 * Use this annotation on case class field or REST API method parameter to instruct the TypeScript generator
 * to emit an optional field or parameter.
 *
 * For `Option` and `Option`-like types, this annotation also changes the type of generated TypeScript field.
 * For example, case class field `int: Option[Int]` is normally represented as `int: number | null`
 * but with `@tsOptional(None)` annotation applied, it becomes `int?: number`.
 *
 * @param serverFallbackValue fallback value to be used on server-side during deserialization
 */
class tsOptional[+T](serverFallbackValue: => T) extends AnnotationAggregate {
  // make all @tsOptional params automatically @transientDefault and give them fallback value
  @transientDefault @whenAbsent(serverFallbackValue) type Implied

  def fallbackValue: T = serverFallbackValue
}

class tsMutable(val mutable: Boolean = true) extends StaticAnnotation
