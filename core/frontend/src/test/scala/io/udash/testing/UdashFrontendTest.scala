package io.udash.testing

import com.github.ghik.silencer.silent

import scala.scalajs.concurrent.JSExecutionContext

trait UdashFrontendTest extends UdashSharedTest {
  import scalatags.JsDom.all.div
  def emptyComponent() = div().render

  @silent
  implicit val testExecutionContext = JSExecutionContext.runNow
}