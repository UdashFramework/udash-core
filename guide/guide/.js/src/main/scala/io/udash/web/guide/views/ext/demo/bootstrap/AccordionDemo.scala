package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object AccordionDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.collapse.{UdashAccordion, UdashCollapse}
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val news = SeqProperty("Title 1", "Title 2", "Title 3")

    val accordion = UdashAccordion(news)(
      (news, _) => span(news.get).render,
      (_, _) => div(
        Card.card, Card.body, Background.color(Color.Light)
      )(ul(repeat(events)(event =>
        li(event.get.toString).render
      ))).render
    )

    news.elemProperties.map(accordion.collapseOf)
      .filter(_.isDefined)
      .foreach(_.get.listen { case ev => events.append(ev) })

    div(accordion.render).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

