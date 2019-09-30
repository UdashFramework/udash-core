package io.udash.bootstrap

import io.udash._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

object UdashBootstrap {
  final val False: ReadableProperty[Boolean] = false.toProperty
  final val True: ReadableProperty[Boolean] = true.toProperty
  final val ColorSecondary: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Secondary.toProperty
  private final val NoneProperty = scala.None.toProperty
  def None[A]: ReadableProperty[Option[A]] = NoneProperty.asInstanceOf[ReadableProperty[Option[A]]]

  /** Loads FontAwesome styles. */
  def loadFontAwesome(): Element =
    link(rel := "stylesheet", href := "https://use.fontawesome.com/releases/v5.11.2/css/all.css").render

  /** Loads Bootstrap styles. */
  def loadBootstrapStyles(): Element =
    link(rel := "stylesheet", href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css").render
}
