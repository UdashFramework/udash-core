package io.udash.catsinterop

object Examples extends App {
  import io.udash.properties.Properties._
  import cats.implicits._

  def createProp: Property[String] = Property[String]("u a a a")
  def createRoProp: ReadableProperty[String] = "u a a a".toProperty
  def createSeqProp: SeqProperty[String] = SeqProperty("u a a a")

  val prop1 = createProp
  val prop2 = createRoProp
  val prop3 = createRoProp

  (prop1.readable, prop2, prop3)
    .mapN { (v1, v2, v3) =>
      s"1 $v1, 2 $v2, 3 $v3"
    }
    .listen(a => println(a), true)

  prop1.set("omae wa mou")

  val sprop1 = createSeqProp
  val sprop2 = createSeqProp
  val sprop3 = createSeqProp

  sprop1.imap(_.toInt)(_.toString)

  (sprop1.readable, sprop2.readable, sprop3.readable)
    .mapN { (v1, v2, v3) =>
      s"1 $v1, 2 $v2, 3 $v3"
    }
    .listen(a => println(a), true)
}
