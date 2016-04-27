package io.udash.rpc

case class TestCC(i: Int, l: Long, b: Boolean, s: String, list: List[Char])
case class NestedTestCC(i: Int, t: TestCC, t2: TestCC)
case class DeepNestedTestCC(n: NestedTestCC, l: DeepNestedTestCC)

case class CompleteItem(unit: Unit, string: String, specialString: String, char: Char, boolean: Boolean, byte: Byte, short: Short, int: Int,
                        long: Long, float: Float, double: Double, binary: Array[Byte], list: List[String],
                        set: Set[String], obj: TestCC, map: Map[String, Int])
