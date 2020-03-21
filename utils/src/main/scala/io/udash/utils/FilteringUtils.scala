package io.udash.utils

import com.avsystem.commons._

object FilteringUtils {
  /** Finds the longest lists prefix with equal elements. */
  def findEqPrefix[T](newPath: Iterator[T], previousPath: Iterator[T]): Iterator[T] =
    newPath.zip(previousPath).takeWhile { case (h1, h2) => h1 == h2 }.map(_._1)

  /** Finds @newPath suffix which is different than in @previousPath. */
  def findDiffSuffix[T](newPath: Iterator[T], previousPath: Iterator[T]): Iterator[T] =
    newPath.map(_.opt).zipAll(previousPath.map(_.opt), Opt.Empty, Opt.Empty).dropWhile { case (h1, h2) =>
      h1 == h2
    }.flatMap(_._1)
}
