package io.udash.web.guide.demos

import org.scalatest.funsuite.AnyFunSuite

class AutoDemoTest extends AnyFunSuite {
  test("MPC fix") {
    val result = AutoDemo.mpcFix(
      """
        |class SubscribeModel(val email: String)
        |object SubscribeModel {
        |    implicit val mpc: ModelPropertyCreator[SubscribeModel] = ModelPropertyCreator.materialize
        |}
        |println("sth")
        |""".stripMargin
    )

    AutoDemo.mpcFix(
      """
        |class SubscribeModel(val email: String)
        |object SubscribeModel {
        |    implicit val mpc: ModelPropertyCreator[SubscribeModel] = ModelPropertyCreator.materialize
        |}
        |println("sth")
        |""".stripMargin
    )

    val expected =
      """
        |class SubscribeModel(val email: String)
        |object SubscribeModel extends HasModelPropertyCreator[SubscribeModel]
        |println("sth")
        |""".stripMargin

    assert(result == expected)
  }

  test("Codec fix") {
    val result = AutoDemo.codecFix(
      """
        |class SubscribeModel(val email: String)
        |object SubscribeModel {
        |    implicit val codec: GenCodec[SubscribeModel] = GenCodec.materialize
        |}
        |println("sth")
        |""".stripMargin
    )

    val expected =
      """
        |class SubscribeModel(val email: String)
        |object SubscribeModel extends HasGenCodec[SubscribeModel]
        |println("sth")
        |""".stripMargin

    assert(result == expected)
  }


}
