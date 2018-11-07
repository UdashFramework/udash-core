package io.udash
package bootstrap

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object BootstrapJsContext {
  @js.native
  @JSImport("bootstrap", JSImport.Namespace)
  object BootstrapJs extends js.Object
  val bootstrapJs: BootstrapJs.type = BootstrapJs

  @js.native
  @JSImport("tempusdominus-bootstrap-4", JSImport.Namespace)
  object BootstrapDatepickerJs extends js.Object
  val bootstrapDatepickerJs: BootstrapDatepickerJs.type = BootstrapDatepickerJs
}
