package io.udash.bindings

import java.{util => ju}

import io.udash.properties.single.Property
import org.scalajs.dom.{html, _}

import scala.concurrent.ExecutionContext
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Radio buttons group for finite options with one element selection.
  */
object RadioButtons {
  /**
    * @param property Property which gonna be bound to radio buttons group.
    * @param options Seq of available options, one radio button will be created for each option.
    * @param decorator Function creating HTML element from buttons Seq.
    * @param xs Modifiers to apply on each generated checkbox.
    * @return HTML element created by decorator.
    */
  def apply(property: Property[String], options: Seq[String], decorator: Seq[(html.Input, String)] => JsDom.TypedTag[html.Element], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.Element] = {
    val bind = prepareBind(property)
    val htmlInputs = prepareHtmlInputs(options, bind)(xs:_*)
    decorator(htmlInputs.zip(options))
  }

  private def prepareHtmlInputs(options: Seq[String], binding: JsDom.Modifier)(xs: Modifier*) = {
    val uuid: String = ju.UUID.randomUUID().toString
    options.map(opt => {
      val el: html.Input = input(tpe := "radio", value := opt, binding)(xs:_*).render
      el.name = uuid
      el
    })
  }

  private def prepareBind(property: Property[String])(implicit ec: ExecutionContext): JsDom.Modifier = {
    def updateInput(t: html.Input) = {
      t.checked = property.get == t.value
    }

    new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[html.Input]

        updateInput(element)
        property.listen(_ => updateInput(element))
        element.onchange = (event: Event) => {
          property.set(element.value)
        }
      }
    }
  }
}
