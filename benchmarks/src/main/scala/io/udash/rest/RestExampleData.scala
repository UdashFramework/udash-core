package io.udash.rest

import com.avsystem.commons.misc.{AbstractValueEnum, EnumCtx}

import scala.util.Random

final case class RestExampleData(number: Long, string: String)
object RestExampleData extends RestDataCompanion[RestExampleData] {
  final case class RestResponseSize(value: Int)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object RestResponseSize extends RestValueEnumCompanion[RestResponseSize] {
    final val Small: Value = new RestResponseSize(10)
    final val Medium: Value = new RestResponseSize(500)
    final val Huge: Value = new RestResponseSize(10000)
  }

  private def random() =
    RestExampleData(
      Random.nextLong(),
      Iterator.continually(Random.nextPrintableChar()).take(200).mkString
    )

  def generateRandomList(size: RestResponseSize): List[RestExampleData] =
    Range(0, size.value).toList.map(_ => RestExampleData.random())
}
