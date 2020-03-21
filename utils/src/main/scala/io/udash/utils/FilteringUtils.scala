package io.udash.utils

import com.avsystem.commons._

import scala.annotation.tailrec

object FilteringUtils {
  /** Finds the longest lists prefix with equal elements. */
  def findEqPrefix[T](newPath: List[T], previousPath: List[T]): List[T] = {
    @tailrec
    def _findEqPrefix(newPath: List[T], previousPath: List[T], subPath: List[T]): List[T] = {
      (newPath, previousPath) match {
        case (head1 :: tail1, head2 :: tail2) if head1 == head2 => _findEqPrefix(tail1, tail2, subPath :+ head1)
        case _ => subPath
      }
    }

    _findEqPrefix(newPath, previousPath, Nil)
  }

  /** Finds @newPath suffix which is different than in @previousPath. */
  def findDiffSuffix[T](newPath: Iterator[T], previousPath: Iterator[T]): List[T] = {
    newPath.map(_.opt).zipAll(previousPath.map(_.opt), Opt.Empty, Opt.Empty).dropWhile { case (h1, h2) =>
      h1 == h2
    }.flatMap(_._1).toList
  }
}
