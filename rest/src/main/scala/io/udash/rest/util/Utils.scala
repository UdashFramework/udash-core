package io.udash
package rest.util

import monix.eval.Task
import monix.reactive.Observable

import java.io.ByteArrayOutputStream

private[rest] object Utils {

  def mergeArrays(data: Observable[Array[Byte]]): Task[Array[Byte]] =
    data.foldLeftL(new ByteArrayOutputStream()) { case (acc, elem) =>
      acc.write(elem)
      acc
    }.map(_.toByteArray)
}
