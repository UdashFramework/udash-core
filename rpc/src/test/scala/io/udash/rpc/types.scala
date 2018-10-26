package io.udash.rpc

import com.avsystem.commons.serialization.HasGenCodec

case class TestCC(i: Int, l: Long, intAsDouble: Double, b: Boolean, s: String, list: List[Char])
object TestCC extends HasGenCodec[TestCC]

case class NestedTestCC(i: Int, t: TestCC, t2: TestCC)
object NestedTestCC extends HasGenCodec[NestedTestCC]

case class DeepNestedTestCC(n: NestedTestCC, l: DeepNestedTestCC)
object DeepNestedTestCC extends HasGenCodec[DeepNestedTestCC]

case class CompleteItem(unit: Unit, string: String, specialString: String, char: Char, boolean: Boolean, byte: Byte, short: Short, int: Int,
                        long: Long, float: Float, double: Double, binary: Array[Byte], list: List[String],
                        set: Set[String], obj: TestCC, map: Map[String, Int])
object CompleteItem extends HasGenCodec[CompleteItem]
