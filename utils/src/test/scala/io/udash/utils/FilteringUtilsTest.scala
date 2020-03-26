package io.udash.utils

import io.udash.testing.UdashSharedTest

class FilteringUtilsTest extends UdashSharedTest {
  import FilteringUtils._

  "FilteringUtils" should {
    "find equal prefix in lists" in {
      val a = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
      val b = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findEqPrefix(a.iterator, b.iterator).toList should be(1 :: 2 :: 3 :: Nil)
      findEqPrefix(b.iterator, a.iterator).toList should be(1 :: 2 :: 3 :: Nil)

      val c = 3 :: 2 :: 3 :: 4 :: 5 :: Nil
      val d = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findEqPrefix(c.iterator, d.iterator).toList should be(Nil)
      findEqPrefix(d.iterator, c.iterator).toList should be(Nil)

      val e = Nil
      val f = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findEqPrefix(e.iterator, f.iterator).toList should be(Nil)
      findEqPrefix(f.iterator, e.iterator).toList should be(Nil)

      findEqPrefix(e.iterator, e.iterator).toList should be(Nil)
      findEqPrefix(f.iterator, f.iterator).toList should be(f)
    }

    "find different suffix in lists" in {
      val a = 1 :: 2 :: 3 :: 4 :: 5 :: Nil
      val b = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findDiffSuffix(a.iterator, b.iterator).toList should be(4 :: 5 :: Nil)
      findDiffSuffix(b.iterator, a.iterator).toList should be(6 :: 5 :: Nil)

      val c = 3 :: 2 :: 3 :: 4 :: 5 :: Nil
      val d = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findDiffSuffix(c.iterator, d.iterator).toList should be(3 :: 2 :: 3 :: 4 :: 5 :: Nil)
      findDiffSuffix(d.iterator, c.iterator).toList should be(1 :: 2 :: 3 :: 6 :: 5 :: Nil)

      val e = Nil
      val f = 1 :: 2 :: 3 :: 6 :: 5 :: Nil

      findDiffSuffix(e.iterator, f.iterator).toList should be(Nil)
      findDiffSuffix(f.iterator, e.iterator).toList should be(1 :: 2 :: 3 :: 6 :: 5 :: Nil)

      findDiffSuffix(e.iterator, e.iterator).toList should be(Nil)
      findDiffSuffix(f.iterator, f.iterator).toList should be(Nil)
    }
  }
}
