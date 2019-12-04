package io.udash.benchmarks.properties

import com.avsystem.commons._
import io.udash._
import japgolly.scalajs.benchmark.Benchmark

import scala.util.Random

trait BenchmarkUtils {
  case class ModelItem(i: Int, s: String, sub: Option[ModelItem])
  object ModelItem extends HasModelPropertyCreator[ModelItem] {
    def random: ModelItem = ModelItem(
      Random.nextInt(100), Random.nextString(5), Some(ModelItem(Random.nextInt(100), Random.nextString(5), None))
    )
  }

  sealed trait ModelWithSeqItem {
    def i: Int
    def s: String
    def seq: BSeq[Int]
  }

  case class ModelWithBSeqItem(i: Int, s: String, seq: BSeq[Int]) extends ModelWithSeqItem
  object ModelWithBSeqItem extends HasModelPropertyCreator[ModelWithBSeqItem] {
    def random: ModelWithBSeqItem = ModelWithBSeqItem(
      Random.nextInt(100), Random.nextString(5), 1 to Random.nextInt(100) + 100
    )
  }

  case class ModelWithISeqItem(i: Int, s: String, seq: ISeq[Int]) extends ModelWithSeqItem
  object ModelWithISeqItem extends HasModelPropertyCreator[ModelWithISeqItem] {
    def random: ModelWithISeqItem = ModelWithISeqItem(
      Random.nextInt(100), Random.nextString(5), 1 to Random.nextInt(100) + 100
    )
  }

  def slowInc(v: Int): Int = {
    var r = v
    (1 to 10000).foreach(_ => r += 1)
    r
  }

  def slowDec(v: Int): Int = {
    var r = v
    (1 to 10000).foreach(_ => r -= 1)
    r
  }

  def addEmptyListeners[T](p: T)(count: Int, listenOp: T => Unit): Unit = {
    (1 to count).foreach(_ => listenOp(p))
  }

  def setAndGetValues[T1, T2](p: T1, t: T2)(count: Int, getToSetRatio: Double, setOp: (T1, Int) => Unit, getOp: T2 => Any): Unit = {
    var counter: Double = 0
    (1 to count).foreach { i =>
      setOp(p, i)

      counter += getToSetRatio
      while (counter >= 1) {
        getOp(t)
        counter -= 1
      }
    }
  }

  def replaceElements(p: SeqProperty[Int], i: Int): Unit = {
    val start = Random.nextInt(p.size / 2)
    val count = Random.nextInt(p.size / 3)
    p.replace(start, count, Seq.tabulate(count)(_ + i): _*)
  }

  def generateGetSetListenBenchmarks[T1, T2](properties: Seq[(String, () => (T1, T2))])(
    setsCounts: Seq[Int], getToSetRatios: Seq[Double], listenersCounts: Seq[Int],
    setAndGetOps: Seq[(String, (T1, Int) => Unit, T2 => Any)], listenOps: Seq[(String, T2 => Unit)]
  ): Seq[Benchmark[Unit]] = {
    var id = 0
    for {
      propertyCreator <- properties
      setAndGetOp <- setAndGetOps
      listenOp <- listenOps
      listenersCount <- listenersCounts
      setsCount <- setsCounts
      getToSetRatio <- getToSetRatios
    } yield {
      val (propertiesDesc, props) = propertyCreator
      val (setAndGetDesc, setter, getter) = setAndGetOp
      val (listenerDesc, listener) = listenOp
      id += 1
      Benchmark(s"${"%03d".format(id)}. set and get ($setsCount and ${setsCount * getToSetRatio} times - $setAndGetDesc) on $propertiesDesc with $listenersCount listeners ($listenerDesc)") {
        val (p, t) = props()
        addEmptyListeners(t)(listenersCount, listener)
        setAndGetValues(p, t)(setsCount, getToSetRatio, setter, getter)
      }
    }
  }
}
