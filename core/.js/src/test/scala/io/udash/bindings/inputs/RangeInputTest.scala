package io.udash.bindings.inputs

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

class RangeInputTest extends AsyncUdashFrontendTest {
  "Input" should {
    //test cases ignored due to bugs with min/max in jsdom
    "synchronise state with property changes" ignore {
      val p = Property[Double](7)
      val input = RangeInput(p, 0d.toProperty, 100d.toProperty, 0.1.toProperty)()
      val inputEl = input.render

      inputEl.valueAsNumber should be(7.0)

      p.set(15.5)
      p.set(17.5)
      p.set(85.2)
      p.set(53.7)
      inputEl.valueAsNumber should be(53.7)

      p.set(0)
      inputEl.valueAsNumber should be(0d)

      p.set(100)
      inputEl.valueAsNumber should be(100d)

      p.set(120)
      inputEl.valueAsNumber should be(100d)

      p.set(-7.5)
      inputEl.valueAsNumber should be(0)

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }

    "synchronise property with state changes" in {
      val p = Property[Double](7)
      val input = RangeInput(p, 0d.toProperty, 100d.toProperty, 0.1.toProperty)()
      val inputEl = input.render

      inputEl.valueAsNumber = 78.5
      inputEl.onchange(null)
      p.get should be(78.5)

      inputEl.valueAsNumber = 18.5
      inputEl.onchange(null)
      p.get should be(18.5)

      inputEl.valueAsNumber = 18
      inputEl.onchange(null)
      p.get should be(18)

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }

    "synchronise value on bound and step changes" ignore {
      val p = Property[Double](8)
      val min = Property(0d)
      val max = Property(100d)
      val step = Property(2d)
      val input = RangeInput(p, min, max, step)()

      p.get should be(8)

      min.set(20)
      p.get should be(20)

      min.set(0)
      max.set(10)
      p.get should be(10)

      max.set(100)
      p.set(7)
      step.set(20)
      p.get should be(0)

      p.listenersCount() should be(1)
      input.kill()
      p.listenersCount() should be(0)
    }
  }
}
