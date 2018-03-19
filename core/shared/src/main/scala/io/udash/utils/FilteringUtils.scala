package io.udash.utils

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
  def findDiffSuffix[T](newPath: List[T], previousPath: List[T]): List[T] = {
    @tailrec
    def _findDiffSuffix(newPath: List[T], previousPath: List[T], subPath: List[T]): List[T] = (newPath, previousPath) match {
      case (head1 :: tail1, head2 :: tail2) if subPath == Nil && head1 == head2 => _findDiffSuffix(tail1, tail2, subPath)
      case (head1 :: tail1, _ :: tail2) => _findDiffSuffix(tail1, tail2, subPath :+ head1)
      case (head1 :: tail1, Nil) => _findDiffSuffix(tail1, Nil, subPath :+ head1)
      case _ => subPath
    }

    _findDiffSuffix(newPath, previousPath, Nil)
  }
}
