package io.udash.bootstrap

import com.karasiq.bootstrap.form.{FormRadio, FormRadioGroup}
import io.udash._

import scala.concurrent.ExecutionContext

/**
  * [[Property]] adapters for scalajs-bootstrap.
  * Use with com.karasiq.bootstrap.BootstrapImplicits import.
  */
trait UdashBootstrapImplicits extends RxConverters {

  import com.karasiq.bootstrap.BootstrapImplicits._

  implicit def udashInputOps[T](value: Property[String]): RxInputOps[T] = RxInputOps(value)

  implicit def udashIntInputOps[T](value: Property[Int]): RxIntInputOps[T] = RxIntInputOps(value)

  implicit def udashDoubleInputOps[T](value: Property[Double]): RxDoubleInputOps[T] = RxDoubleInputOps(value)

  implicit def udashBooleanInputOps[T](value: Property[Boolean]): RxBooleanInputOps[T] = RxBooleanInputOps(value)

  implicit def udashStateOps(state: Property[Boolean]): RxStateOps = RxStateOps(state)

  implicit class FormRadioGroupOps(rg: FormRadioGroup)(implicit ec: ExecutionContext) {
    def radioListProperty: ReadableSeqProperty[FormRadio] = rg.radioList

    def valueProperty: Property[String] = rg.value
  }

}