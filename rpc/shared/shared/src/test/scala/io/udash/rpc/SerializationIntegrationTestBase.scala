package io.udash.rpc

import io.udash.testing.UdashSharedTest

import scala.language.higherKinds
import scala.util.Random

class SerializationIntegrationTestBase extends UdashSharedTest with Utils {
  val repeats = 1000

  def tests(writer: UdashRPCFramework, reader: UdashRPCFramework) = {
    "serialize and deserialize all types" in {
      for (i <- 1 to repeats) {
        def cc() = TestCC(Random.nextInt(), Random.nextLong(), Random.nextBoolean(), Random.nextString(200), List.fill(Random.nextInt(200))('a'))
        def ncc() = NestedTestCC(Random.nextInt(), cc(), cc())
        def dncc(counter: Int = 0): DeepNestedTestCC =
          if (counter < 200) DeepNestedTestCC(ncc(), dncc(counter + 1))
          else DeepNestedTestCC(ncc(), null)

        val test: DeepNestedTestCC = dncc()
        val serialized = writer.rawToString(writer.write(test))
        val deserialized = reader.read[DeepNestedTestCC](reader.stringToRaw(serialized))

        deserialized should be(test)
      }
    }
  }
}

