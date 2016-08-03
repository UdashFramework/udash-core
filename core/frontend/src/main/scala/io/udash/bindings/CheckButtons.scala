package io.udash.bindings

import io.udash.properties.seq.SeqProperty
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom.{html, _}

import scala.concurrent.ExecutionContext
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Checkboxes for finite options with many elements selection. Bound to SeqProperty.
  */
object CheckButtons {
  /**
    * @param property SeqProperty which gonna be bound to checkboxes
    * @param options Seq of available options, one checkbox will be created for each option.
    * @param decorator Function creating HTML element from checkboxes Seq.
    * @param xs Modifiers to apply on each generated checkbox.
    * @return HTML element created by decorator.
    */
  def apply(property: SeqProperty[String, _ <: ReadableProperty[String]], options: Seq[String], decorator: Seq[(html.Input, String)] => JsDom.TypedTag[html.Element], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.Element] = {
    val htmlInputs = prepareHtmlInputs(options)(xs:_*)
    val bind = prepareBind(property, htmlInputs)
    htmlInputs.foreach(bind.applyTo)
    decorator(htmlInputs.zip(options))
  }

  private def prepareHtmlInputs(options: Seq[String])(xs: Modifier*) =
    options.map(opt => input(tpe := "checkbox", value := opt)(xs:_*).render)

  private def prepareBind(property: SeqProperty[String, _ <: ReadableProperty[String]], htmlInputs: Seq[html.Input])(implicit ec: ExecutionContext): JsDom.Modifier = {
    def updateInput(t: html.Input) = {
      val selection = property.get
      t.checked = selection.contains(t.value)
    }

    new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[html.Input]

        updateInput(element)
        property.listen(_ => updateInput(element))
        element.onchange = (_: Event) => {
          val value = element.value
          if (element.checked) property.append(value)
          else property.remove(value)
        }
      }
    }
  }
}
