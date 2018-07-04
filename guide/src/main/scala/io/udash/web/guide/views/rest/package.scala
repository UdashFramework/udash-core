package io.udash.web.guide.views

import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom.Modifier

package object rest {
  def simpleExample(): Modifier =
    CodeBlock(
      """import io.udash.rest._
        |
        |trait MainServerREST {
        |  def simple(): SimpleServerREST
        |}
        |
        |trait SimpleServerREST {
        |  @GET def string(): Future[String]
        |  @GET def int(): Future[Int]
        |  @GET @RESTName("class") def cls(): Future[RestExampleClass]
        |}""".stripMargin
    )(GuideStyles)
}
