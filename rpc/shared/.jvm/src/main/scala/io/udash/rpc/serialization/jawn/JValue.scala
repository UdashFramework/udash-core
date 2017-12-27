package io.udash.rpc.serialization.jawn

sealed trait JValue

case object JNull extends JValue
case class JString(value: String) extends JValue
case class JBoolean(value: Boolean) extends JValue

sealed trait JNum extends JValue
case class JInt(value: Int) extends JNum
case class JDouble(value: Double) extends JNum

case class JList(value: List[JValue]) extends JValue
case class JObject(value: Map[String, JValue]) extends JValue

