package io.udash.properties

import scala.collection.mutable
import scala.scalajs.js

private[properties]
object CrossCollections {
  import scala.scalajs.js.JSConverters._

  def toCrossArray[T](t: Traversable[T]): mutable.Buffer[T] = t.toJSArray
  def createArray[T]: mutable.Buffer[T] = js.Array[T]()
  def createDictionary[T]: mutable.Map[String, T] = js.Dictionary[T]()
  def copyArray[T](a: mutable.Buffer[T]): mutable.Buffer[T] = a.toJSArray.jsSlice()
  def slice[T](a: mutable.Buffer[T], from: Int, to: Int): mutable.Buffer[T] = a.toJSArray.jsSlice(from, to)
  def replace[T](a: mutable.Buffer[T], idx: Int, count: Int, items: T*): Unit = a.toJSArray.splice(idx, count, items:_*)
}
