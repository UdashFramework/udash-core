package io.udash.wrappers.highcharts
package config
package utils

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js
import scala.scalajs.js.|

sealed class Animation(val value: Boolean | js.Object)

object Animation {
  private var wrapperEasingFunNum = 0

  val Enabled = new Animation(true)
  val Disabled = new Animation(false)
  case class Custom(duration: js.UndefOr[FiniteDuration] = js.undefined,
                    easing: js.UndefOr[(Double) => Double] = js.undefined)
    extends Animation({
      val name = easing.map(f => {
        wrapperEasingFunNum += 1
        val name = "wrapperEasingFun" + wrapperEasingFunNum
        js.Dynamic.global.Math.updateDynamic(name)(js.Any.fromFunction1(f))
        name
      })

      js.Dynamic.literal(
        duration = duration.map(_.toMillis.toDouble),
        easing = name
      )
    })
}