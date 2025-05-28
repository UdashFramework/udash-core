package io.udash.rest

import monix.eval.Task
import monix.reactive.Observable

import scala.util.Random

case class RestExampleData(number: Long, string: String)

object RestExampleData extends RestDataCompanion[RestExampleData]{
  private def random() = {
    RestExampleData(
      Random.nextLong(),
      Iterator.continually(Random.nextPrintableChar()).take(200).mkString
    )
  }

  def generateRandomObservable(size: Int): Observable[RestExampleData] =
    Observable.fromIterable(Range(0, size).map(_ => RestExampleData.random()))

  def generateRandomList(size: Int): Task[List[RestExampleData]] =
    Task.eval(Range(0, size).toList.map(_ => RestExampleData.random()))

}