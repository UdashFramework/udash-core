package io.udash.rpc.serialization.jawn

import jawn.SimpleFacade

import scala.util.Try

object JawnFacade extends SimpleFacade[JValue] {
  override def jnull() = JNull

  override def jtrue() = JBoolean(true)

  override def jfalse() = JBoolean(false)

  override def jnum(s: CharSequence, decIndex: Int, expIndex: Int): JValue = {
    val str = s.toString
    if (decIndex == -1 && expIndex == -1) {
      Try(JInt(str.toInt)).getOrElse(JDouble(str.toDouble))
    } else JDouble(str.toDouble)
  }

  override def jstring(s: CharSequence): JValue = JString(s.toString)

  override def jarray(vs: List[JValue]) = JList(vs)

  override def jobject(vs: Map[String, JValue]): JValue = JObject(vs)
}
