package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object IconsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash.bootstrap._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.utils.UdashIcons
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    UdashButtonToolbar()(
      UdashButtonGroup()(
        Seq(
          UdashIcons.FontAwesome.Solid.alignLeft,
          UdashIcons.FontAwesome.Solid.alignCenter,
          UdashIcons.FontAwesome.Solid.alignRight,
          UdashIcons.FontAwesome.Solid.alignJustify
        ).map(icon => UdashButton()(i(icon)).render): _*,
      ).render,
      UdashButtonGroup()(
        Seq(
          UdashIcons.FontAwesome.Brands.bitcoin,
          UdashIcons.FontAwesome.Solid.euroSign,
          UdashIcons.FontAwesome.Solid.dollarSign,
          UdashIcons.FontAwesome.Brands.superpowers
        ).map(icon => UdashButton()(i(icon)).render): _*,
      ).render
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (div(GuideStyles.frame)(rendered), source.linesIterator)
  }
}

