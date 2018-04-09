package io.udash.benchmarks.serialization

import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.{GenCodec, flatten}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

case class Something(
  name: String,
  year: Int,
  stuffs: List[Stuff],
  ints: Set[Int]
)
object Something {
  implicit val codec: GenCodec[Something] = GenCodec.materialize
  implicit val encoder: Encoder[Something] = deriveEncoder[Something]
  implicit val decoder: Decoder[Something] = deriveDecoder[Something]
  implicit val rw: upickle.default.ReadWriter[Something] = upickle.default.macroRW

  val Example = Something(
    "The Name of Something",
    2017,
    List(
      Stuff(Map(), 3.15),
      Stuff(Map("fuu" -> true, "boo" -> false, "fag" -> true), 3.14),
      Stuff(Map("fuu" -> true), 3.16),
      Stuff(Map("fuu" -> true, "boo \n\r\t" -> false, "fag" -> true, "moar" -> false), 3.17),
      Stuff(Map.empty, 3.18),
      Stuff(Map("fuu" -> true, "boo" -> false, "fag" -> true), 3.19)
    ),
    Set(
      1, 5, 62, -23, 454, 123, 75, -234
    )
  )

  final val ExampleJson = Example.asJson
  final val ExampleJsonString = ExampleJson.noSpaces
}

case class Stuff(map: Map[String, Boolean], factor: Double)
object Stuff {
  implicit val codec: GenCodec[Stuff] = GenCodec.materialize
  implicit val encoder: Encoder[Stuff] = deriveEncoder[Stuff]
  implicit val decoder: Decoder[Stuff] = deriveDecoder[Stuff]
  implicit val rw: upickle.default.ReadWriter[Stuff] = upickle.default.macroRW
}

@flatten sealed trait FlatSealedStuff
sealed trait SealedStuff
case class Case1(i: Int) extends SealedStuff with FlatSealedStuff
object Case1 {
  implicit val rw: upickle.default.ReadWriter[Case1] = upickle.default.macroRW
}
case class Case2(i: Int) extends SealedStuff with FlatSealedStuff
object Case2 {
  implicit val rw: upickle.default.ReadWriter[Case2] = upickle.default.macroRW
}
case class Case3(i: Int) extends SealedStuff with FlatSealedStuff
object Case3 {
  implicit val rw: upickle.default.ReadWriter[Case3] = upickle.default.macroRW
}
case class Case4(i: Int) extends SealedStuff with FlatSealedStuff
object Case4 {
  implicit val rw: upickle.default.ReadWriter[Case4] = upickle.default.macroRW
}
case class Case5(i: Int) extends SealedStuff with FlatSealedStuff
object Case5 {
  implicit val rw: upickle.default.ReadWriter[Case5] = upickle.default.macroRW
}
case class Case6(i: Int) extends SealedStuff with FlatSealedStuff
object Case6 {
  implicit val rw: upickle.default.ReadWriter[Case6] = upickle.default.macroRW
}
case class Case7(i: Int) extends SealedStuff with FlatSealedStuff
object Case7 {
  implicit val rw: upickle.default.ReadWriter[Case7] = upickle.default.macroRW
}
object SealedStuff {
  implicit val codec: GenCodec[SealedStuff] = GenCodec.materialize
  implicit val encoder: Encoder[SealedStuff] = deriveEncoder[SealedStuff]
  implicit val decoder: Decoder[SealedStuff] = deriveDecoder[SealedStuff]
  implicit val rw: upickle.default.ReadWriter[SealedStuff] = upickle.default.macroRW

  final val ExampleList = List[SealedStuff](Case5(5), Case3(3), Case1(1), Case7(7), Case2(2), Case4(4), Case6(6))
  final val ExampleJson = ExampleList.asJson
  final val ExampleJsonString = ExampleJson.noSpaces
  final val ExampleUpickleJsonString = upickle.default.write(SealedStuff.ExampleList)
}
object FlatSealedStuff {
  implicit val codec: GenCodec[FlatSealedStuff] = GenCodec.materialize

  final val ExampleList = List[FlatSealedStuff](Case5(5), Case3(3), Case1(1), Case7(7), Case2(2), Case4(4), Case6(6))
  final val ExampleJsonString = JsonStringOutput.write(ExampleList)
}

case class Foo(s: String, d: Double, i: Int, l: Long, bs: List[Boolean])
object Foo {
  implicit val circeEncodeFoo: Encoder[Foo] = deriveEncoder
  implicit val circeDecodeFoo: Decoder[Foo] = deriveDecoder
  implicit val codec: GenCodec[Foo] = GenCodec.materialize
  implicit val rw: upickle.default.ReadWriter[Foo] = upickle.default.macroRW

  final val ExampleMap: Map[String, Foo] = List.tabulate(100) { i =>
    ("b" * i) -> Foo("a" * i, (i + 2.0) / (i + 1.0), i, i * 1000L, (0 to i).map(_ % 2 == 0).toList)
  }.toMap

  final val ExampleJson = ExampleMap.asJson
  final val ExampleJsonString = ExampleJson.noSpaces
  final val ExampleUpickleJsonString = upickle.default.write(ExampleMap)
}