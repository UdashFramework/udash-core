package io.udash
package utils

import com.avsystem.commons._
import scala.collection.compat._

private[udash]
object CrossCollections {
  def toCrossArray[T](t: Iterable[T]): MBuffer[T] = t.to(MArrayBuffer)
  def createArray[T]: MBuffer[T] = new MArrayBuffer[T]
  def createDictionary[T]: MMap[String, T] = new MHashMap[String, T]
  def copyArray[T](a: MBuffer[T]): MBuffer[T] = a.to(MArrayBuffer) // creates copy
  def slice[T](a: MBuffer[T], from: Int, to: Int): MBuffer[T] = a.slice(from, to)
  def replace[T](a: MBuffer[T], idx: Int, count: Int, items: T*): Unit =
    replaceSeq(a, idx, count, items)
  def replaceSeq[T](a: MBuffer[T], idx: Int, count: Int, items: BSeq[T]): Unit = {
    if (count > 0) a.remove(idx, count)
    if (items.nonEmpty) a.insertAll(idx, items)
  }
}
