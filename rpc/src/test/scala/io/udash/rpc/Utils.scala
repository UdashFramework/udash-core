package io.udash.rpc

import com.avsystem.commons.serialization._
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}

import scala.util.Random

trait Utils {
  def completeItem() = CompleteItem(
    unit = (),
    string = Random.nextString(Random.nextInt(20)),
    specialString = "\n\f\b\t\r\n\\\"\\\\",
    char = Random.nextString(1).head,
    boolean = Random.nextBoolean(),
    byte = Random.nextInt().toByte,
    short = Random.nextInt().toShort,
    int = Random.nextInt(),
    long = Random.nextLong(),
    float = Random.nextFloat(),
    double = Random.nextDouble(),
    binary = Array.fill(Random.nextInt(20))(Random.nextInt().toByte),
    list = List.fill(Random.nextInt(20))(Random.nextString(Random.nextInt(20))),
    set = List.fill(Random.nextInt(20))(Random.nextString(Random.nextInt(20))).toSet,
    obj = TestCC(Random.nextInt(), Random.nextLong(), Random.nextInt(), Random.nextBoolean(), Random.nextString(Random.nextInt(20)), Nil),
    map = Map(Seq.fill(Random.nextInt(20))(Random.nextString(20) -> Random.nextInt()): _*)
  )

  implicit val codec: GenCodec[TestCC] = GenCodec.materialize[TestCC]
  implicit val codecN: GenCodec[NestedTestCC] = GenCodec.materialize[NestedTestCC]
  implicit val codecDN: GenCodec[DeepNestedTestCC] = new GenCodec[DeepNestedTestCC] {
    override def read(input: Input): DeepNestedTestCC = {
      def _read(acc: List[NestedTestCC])(next: Input): DeepNestedTestCC =
        if (next.readNull()) {
          acc.foldLeft(null: DeepNestedTestCC)((acc: DeepNestedTestCC, n: NestedTestCC) => DeepNestedTestCC(n, acc))
        } else {
          val obj = next.readObject()
          val n: NestedTestCC = obj.nextField() match {
            case in if in.fieldName == "n" =>
              codecN.read(in)
          }
          obj.nextField() match {
            case in if in.fieldName == "nest" =>
              _read(n :: acc)(in)
          }
        }

      _read(Nil)(input)
    }

    override def write(output: Output, value: DeepNestedTestCC): Unit = {
      val obj = output.writeObject()
      codecN.write(obj.writeField("n"), value.n)
      val f = obj.writeField("nest")
      if (value.l != null) this.write(f, value.l)
      else f.writeNull()
      obj.finish()
    }
  }
  implicit val codecCI: GenCodec[CompleteItem] = GenCodec.materialize[CompleteItem]

  def write[T: GenCodec](value: T): JsonStr =
    JsonStr(JsonStringOutput.write(value))

  def read[T: GenCodec](jsonStr: JsonStr): T =
    JsonStringInput.read[T](jsonStr.json)
}
