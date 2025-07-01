package io.udash
package rest

final case class TestRESTRecord(id: Option[Int], s: String)
object TestRESTRecord extends RestDataCompanion[TestRESTRecord]