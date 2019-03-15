package io.udash
package testing

import org.scalatest.Assertions

trait CompilationErrorAssertions extends Assertions {
  def typeErrorFor(code: String): String = macro io.udash.macros.TestMacros.typeErrorImpl
}
