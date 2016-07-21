package io.udash.bindings

import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.properties.seq.SeqProperty
import org.scalajs.dom.{html, _}

import scala.concurrent.ExecutionContext
import scalatags.JsDom
import scalatags.JsDom.all._

/**
  * Select of finite options for single and multi selection.
  */
object Select {
  /**
    * Single select for ValueProperty.
    *
    * @param property Property to bind.
    * @param options Seq of available options.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and selected attributes.
    * @return HTML select tag with bound ValueProperty, applied modifiers and nested options.
    */
  def apply(property: Property[String], options: Seq[String], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.Select] = {
    val htmlOptions = prepareHtmlOptions(options)

    def refreshSelectedItems() = {
      htmlOptions.foreach(option => {
        option.selected = property.get == option.value
      })
    }

    val bind = new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[html.Select]

        refreshSelectedItems()
        val r = property.listen(_ => refreshSelectedItems())
        element.onchange = (event: Event) => {
          property.set(htmlOptions.find(o => o.selected).get.value)
        }
      }
    }

    select(bind, xs)(htmlOptions)
  }

  /**
    * Multi selection for SeqProperty. Bound SeqProperty will contain selected options.
    *
    * @param property Property to bind.
    * @param options Seq of available options.
    * @param xs Additional Modifiers, don't use modifiers on value, onchange and selected attributes.
    * @return HTML select tag with bound SeqProperty, applied modifiers and nested options.
    */
  def apply(property: SeqProperty[String, _ <: ReadableProperty[String]], options: Seq[String], xs: Modifier*)(implicit ec: ExecutionContext): JsDom.TypedTag[html.Select] = {
    val htmlOptions = prepareHtmlOptions(options)

    def refreshSelectedItems() = {
      val selection = property.get
      htmlOptions.foreach(option => {
        option.selected = selection.contains(option.value)
      })
    }

    def collectSelectedItems: Seq[String] = {
      htmlOptions.filter(option => option.selected).map(option => option.value)
    }

    val bind = new JsDom.Modifier {
      override def applyTo(t: Element): Unit = {
        val element = t.asInstanceOf[html.Select]

        refreshSelectedItems()
        property.listen(_ => refreshSelectedItems())
        element.onchange = (event: Event) => property.set(collectSelectedItems)
      }
    }

    select(multiple := true, bind, xs)(htmlOptions)
  }

  private def prepareHtmlOptions(options: Seq[String]) = options.map(opt => {
    val v = opt
    //TODO provide separated function for displayed value
    option(value := v)(v).render
  })
}
