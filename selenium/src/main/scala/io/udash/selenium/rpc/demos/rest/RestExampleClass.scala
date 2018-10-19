package io.udash.selenium.rpc.demos.rest

import io.udash.rest.RestDataCompanion

case class RestTuple(double: Double, string: String)
object RestTuple extends RestDataCompanion[RestTuple]

case class RestExampleClass(i: Int, s: String, tuple: RestTuple)
object RestExampleClass extends RestDataCompanion[RestExampleClass]
