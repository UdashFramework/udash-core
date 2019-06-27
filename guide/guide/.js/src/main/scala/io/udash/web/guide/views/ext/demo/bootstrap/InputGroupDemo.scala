package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object InputGroupDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.form.UdashInputGroup
    import scalatags.JsDom.all._

    val vanityUrl = Property.blank[String]
    val buttonDisabled = Property(true)
    vanityUrl.listen(v => buttonDisabled.set(v.isEmpty))
    val button = UdashButton()("Clear")
    button.listen { case _ => vanityUrl.set("") }

    div(
      label("Your URL"),
      UdashInputGroup(Some(BootstrapStyles.Size.Large).toProperty)(
        UdashInputGroup.prependText(
          "https://example.com/users/",
          bind(vanityUrl)
        ),
        UdashInputGroup.input(TextInput(vanityUrl)().render),
        UdashInputGroup.append(
          UdashButton(disabled = buttonDisabled)("Go!").render,
          button.render
        )
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

