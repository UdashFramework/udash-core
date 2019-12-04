package io.udash
package rest.openapi

import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.serialization.whenAbsent

import scala.annotation.StaticAnnotation

class customWa[+T](value: => T) extends AnnotationAggregate {
  @whenAbsent(value)
  final def aggregated: List[StaticAnnotation] = reifyAggregated
}
