package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ListGroupDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.list.UdashListGroup
    import io.udash.bootstrap.utils.BootstrapStyles.{active => activeStyle, disabled => disabledStyle, _}
    import io.udash.css.CssStyleName
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val news = SeqProperty("Title 1", "Title 2", "Title 3")

    def newsStyle(newsProperty: Property[String]): ReadableProperty[CssStyleName] = {
      newsProperty.transform(_.last match {
        case '1' => activeStyle
        case '2' => disabledStyle
        case '3' => List.color(Color.Success)
        case '4' => List.color(Color.Danger)
        case '5' => List.color(Color.Info)
        case '6' => List.color(Color.Warning)
      })
    }

    var i = 1
    val appendHandler = window.setInterval(() => {
      news.append(s"Dynamic $i")
      i += 1
    }, 2000)
    window.setTimeout(() => window.clearInterval(appendHandler), 12000)

    div(
      UdashListGroup(news)((news, nested) =>
        li(nested(cls.bind(newsStyle(news).transform(_.className))))(nested(bind(news))).render
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}
