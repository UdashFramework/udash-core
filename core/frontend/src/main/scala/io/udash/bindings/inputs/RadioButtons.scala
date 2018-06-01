package io.udash.bindings.inputs

import java.{util => ju}

import io.udash._
import io.udash.properties.PropertyCreator
import org.scalajs.dom.{Element, Event, Node}
import org.scalajs.dom.html.{Div, Input => JSInput}
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Radio buttons group for finite options with one element selection.
  */
object RadioButtons {
  def inputsOnlyDecorator[T]: Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, _) => in }
  def spanWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, v) => span(in, label(labelFactory(v))).render }
  def divWithLabelDecorator[T](labelFactory: T => Modifier): Seq[(JSInput, T)] => Seq[Node] =
    _.map { case (in, v) => div(in, label(labelFactory(v))).render }

  /**
    * @param selectedItem Property which is going to be bound to radio buttons group.
    * @param options Seq of available options, one radio button will be created for each option.
    * @param decorator Function creating HTML element from buttons Seq.
    *                  (Check: `RadioButtons.inputsOnlyDecorator`, `RadioButtons.spanWithLabelDecorator` and `RadioButtons.divWithLabelDecorator`)
    * @param inputModifiers Modifiers to apply on each generated checkbox.
    * @return HTML element created by decorator.
    */
  def apply[T : PropertyCreator](
    selectedItem: Property[T], options: ReadableProperty[Seq[T]]
  )(decorator: Seq[(JSInput, T)] => Seq[Node], inputModifiers: Modifier*): InputBinding[Div] = {
    new InputBinding[Div] {
      private val buttons = div(
        nestedInterceptor(
          produceWithNested(options) { case (opts, nested) =>
            if (opts.nonEmpty && !opts.contains(selectedItem.get)) {
              selectedItem.set(opts.head)
            }

            decorator(
              opts.zipWithIndex.map { case (opt, idx) =>
                val in = input(
                  inputModifiers, tpe := "radio", value := idx.toString,
                  nested((checked := "checked").attrIf(selectedItem.transform(_ == opt)))
                ).render

                in.onchange = (_: Event) => selectedItem.set(opt)

                (in, opt)
              }
            )
          }
        )
      ).render

      override def render: Div = buttons
    }
  }

  /**
    * @param property Property which is going to be bound to radio buttons group.
    * @param options Seq of available options, one radio button will be created for each option.
    * @param decorator Function creating HTML element from buttons Seq.
    * @param xs Modifiers to apply on each generated checkbox.
    * @return HTML element created by decorator.
    */
  @deprecated("Use the constructor with dynamic options set and generic element type.", "0.7.0")
  def apply(
    property: Property[String], options: Seq[String],
    decorator: Seq[(JSInput, String)] => JsDom.TypedTag[Element], xs: Modifier*
  ): JsDom.TypedTag[Element] = {
    val bind = prepareBind(property)
    val htmlInputs = prepareHtmlInputs(options, bind)(xs:_*)
    decorator(htmlInputs.zip(options))
  }

  private def prepareHtmlInputs(options: Seq[String], binding: JsDom.Modifier)(xs: Modifier*): Seq[JSInput] = {
    val uuid: String = ju.UUID.randomUUID().toString
    options.map { opt =>
      val el: JSInput = input(tpe := "radio", value := opt, binding)(xs:_*).render
      el.name = uuid
      el
    }
  }

  private def prepareBind(property: Property[String]): JsDom.Modifier = {
    def updateInput(t: JSInput): Unit = {
      t.checked = property.get == t.value
    }

    new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[JSInput]

        updateInput(element)
        property.listen(_ => updateInput(element))
        element.onchange = (_: Event) => {
          property.set(element.value)
        }
      }
    }
  }
}
