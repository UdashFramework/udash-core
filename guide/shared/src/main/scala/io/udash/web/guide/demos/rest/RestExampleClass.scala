package io.udash.web.guide.demos.rest

import io.udash.rest.RestDataCompanion

final case class RestExampleClass(i: Int, s: String, inner: InnerClass)
object RestExampleClass extends RestDataCompanion[RestExampleClass]

final case class InnerClass(d: Double, s: String)
object InnerClass extends RestDataCompanion[InnerClass]