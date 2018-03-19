package io.udash.benchmarks

import io.udash.benchmarks.css.CssStylesApply
import io.udash.benchmarks.i18n.StaticTranslationBinding
import io.udash.benchmarks.properties._
import japgolly.scalajs.benchmark.gui.BenchmarkGUI
import org.scalajs.dom.document

object Main {
  def main(args: Array[String]): Unit = {
    BenchmarkGUI.renderMenu(document.getElementById("body"))(
      SinglePropertyListeners.suite,
      ModelPropertyListeners.suite,
      TransformedSeqPropertyListeners.suite,
      FilteredSeqPropertyListeners.suite,
      ReversedSeqPropertyListeners.suite,

      StaticTranslationBinding.suite,

      CssStylesApply.suite
    )
  }
}
