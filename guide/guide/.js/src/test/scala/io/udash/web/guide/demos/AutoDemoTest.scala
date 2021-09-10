package io.udash.web.guide.demos

import org.scalatest.funsuite.AnyFunSuite

class AutoDemoTest extends AnyFunSuite {
  test("MPC fix") {
    val result = AutoDemo.mpcFix(
      """
        |class SubscribeModel(val email: String)
        |// HasModelPropertyCreator indicates that
        |// you can create ModelProperty for SubscribeModel
        |object SubscribeModel {
        |    implicit val mpc: ModelPropertyCreator[SubscribeModel] = ModelPropertyCreator.materialize
        |}
        |println("sth")
        |""".stripMargin
    )

    val expected =
      """
        |class SubscribeModel(val email: String)
        |// HasModelPropertyCreator indicates that
        |// you can create ModelProperty for SubscribeModel
        |object SubscribeModel extends HasModelPropertyCreator[SubscribeModel]
        |println("sth")
        |""".stripMargin

    assert(result == expected)
  }


}
