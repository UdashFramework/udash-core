package io.udash
package rest

case class TestRESTRecord(id: Option[Int], s: String)
object TestRESTRecord extends RestDataCompanion[TestRESTRecord]