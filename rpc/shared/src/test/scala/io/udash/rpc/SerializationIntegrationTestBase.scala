package io.udash.rpc

import com.avsystem.commons.serialization.{GenCodec, Input, InputType, Output}
import io.udash.testing.UdashSharedTest

import scala.util.Random

class SerializationIntegrationTestBase extends UdashSharedTest with Utils {
  val repeats = 1000

  def tests(writer: UdashRPCFramework, reader: UdashRPCFramework) = {
    "serialize and deserialize all types" in {
      for (i <- 1 to repeats) {
        def cc() = TestCC(Random.nextInt(), Random.nextLong(), 123, Random.nextBoolean(), Random.nextString(200), List.fill(Random.nextInt(200))('a'))
        def ncc() = NestedTestCC(Random.nextInt(), cc(), cc())
        def dncc(counter: Int = 0): DeepNestedTestCC =
          if (counter < 200) DeepNestedTestCC(ncc(), dncc(counter + 1))
          else DeepNestedTestCC(ncc(), null)

        val test: DeepNestedTestCC = dncc()
        val serialized = writer.write(test)
        val deserialized = reader.read[DeepNestedTestCC](serialized)

        deserialized should be(test)
      }
    }

    "serialize and deserialize types with custom gencodec" in {
      implicit def optionGencodec[T: GenCodec]: GenCodec[Option[T]] =
        new GenCodec[Option[T]] {
          override def write(output: Output, value: Option[T]): Unit =
            value match {
              case Some(v) => implicitly[GenCodec[T]].write(output, v)
              case None => output.writeNull()
            }

          override def read(input: Input): Option[T] = input.inputType match {
            case InputType.Null =>
              input.readNull()
              None
            case _ =>
              Some(implicitly[GenCodec[T]].read(input))
          }
        }

      val testOpts = Seq(
        None,
        Some(10L),
        Some(Long.MaxValue)
      )

      testOpts.foreach(opt => {
        val serialized = writer.write(opt)
        val deserialized = reader.read[Option[Long]](serialized)
        deserialized should be(opt)
      })
    }
  }
}

