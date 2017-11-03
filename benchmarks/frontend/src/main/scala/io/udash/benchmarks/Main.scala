package io.udash.benchmarks

import io.udash.benchmarks.properties._
import org.scalajs.dom.document
import japgolly.scalajs.benchmark.gui.BenchmarkGUI

object Main {
  def main(args: Array[String]): Unit = {
    BenchmarkGUI.renderMenu(document.getElementById("body"))(
      SinglePropertyListeners.suite,
      ModelPropertyListeners.suite,
      TransformedSeqPropertyListeners.suite,
      FilteredSeqPropertyListeners.suite,
      ReversedSeqPropertyListeners.suite,
    )
  }
}
