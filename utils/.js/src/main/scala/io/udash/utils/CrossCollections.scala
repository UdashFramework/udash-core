package io.udash
package utils

import com.avsystem.commons._

import scala.scalajs.js

private[udash] object CrossCollections {

  import scala.scalajs.js.JSConverters._

  def toCrossArray[T](t: Iterable[T]): MBuffer[T] = t.toJSArray
  def createArray[T]: MBuffer[T] = js.Array[T]()
  def createDictionary[T]: MMap[String, T] = js.Dictionary[T]()
  def copyArray[T](a: MBuffer[T]): MBuffer[T] = a.toJSArray.jsSlice()
  def slice[T](a: MBuffer[T], from: Int, to: Int): MBuffer[T] = a.toJSArray.jsSlice(from, to)
  def replace[T](a: MBuffer[T], idx: Int, count: Int, items: T*): Unit = replaceSeq(a, idx, count, items)
  def replaceSeq[T](a: MBuffer[T], idx: Int, count: Int, items: BSeq[T]): Unit =
    a.toJSArray.splice(idx, count, items.toSeq: _*)
}
