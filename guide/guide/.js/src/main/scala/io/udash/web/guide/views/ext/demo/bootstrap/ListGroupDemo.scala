package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.window
import scalatags.JsDom

object ListGroupDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")

    def newsStyle(newsProperty: Property[String]): ReadableProperty[String] = {
      newsProperty.transform(_.last match {
        case '1' => BootstrapStyles.active.className
        case '2' => BootstrapStyles.disabled.className
        case '3' => BootstrapStyles.List.color(BootstrapStyles.Color.Success).className
        case '4' => BootstrapStyles.List.color(BootstrapStyles.Color.Danger).className
        case '5' => BootstrapStyles.List.color(BootstrapStyles.Color.Info).className
        case '6' => BootstrapStyles.List.color(BootstrapStyles.Color.Warning).className
      })
    }

    val listGroup = UdashListGroup(news)((news, nested) =>
      li(nested(cls.bind(newsStyle(news))))(nested(bind(news))).render
    )

    var i = 1
    val appendHandler = window.setInterval(() => {
      news.append(s"Dynamic $i")
      i += 1
    }, 2000)
    window.setTimeout(() => window.clearInterval(appendHandler), 20000)

    div(listGroup)
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

