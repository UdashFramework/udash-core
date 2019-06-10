package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object InlineFormDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val search = Property.blank[String]
    val something = Property.blank[String]

    div(
      UdashForm(inline = true)(factory => Seq(
        UdashInputGroup()(
          UdashInputGroup.prependText("Search: "),
          UdashInputGroup.input(
            factory.input.textInput(search)().render
          )
        ).render,
        UdashInputGroup()(
          UdashInputGroup.prependText("Something: "),
          UdashInputGroup.input(
            factory.input.textInput(something)().render
          )
        ).render,
      ))
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

