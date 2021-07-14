package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object InlineFormDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
    import scalatags.JsDom.all._

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
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (rendered.setup(_.applyTags(GuideStyles.frame)), source)
}

