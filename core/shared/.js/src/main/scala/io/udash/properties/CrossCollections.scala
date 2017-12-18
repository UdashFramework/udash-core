package io.udash.properties

import scala.scalajs.js

private[properties]
object CrossCollections {
  import scala.scalajs.js.JSConverters._

  type Array[T] = js.WrappedArray[T]
  type Dictionary[T] = js.Dictionary[T]

  def toCrossArray[T](t: Traversable[T]): Array[T] = t.toJSArray
  def createArray[T]: Array[T] = js.Array[T]()
  def createDictionary[T]: Dictionary[T] = js.Dictionary[T]()
  def copyArray[T](a: Array[T]): Array[T] = a.jsSlice()
  def slice[T](a: Array[T], from: Int, to: Int): Array[T] = a.jsSlice(from, to)
  def replace[T](a: Array[T], idx: Int, count: Int, items: T*): Unit = a.splice(idx, count, items:_*)
}
