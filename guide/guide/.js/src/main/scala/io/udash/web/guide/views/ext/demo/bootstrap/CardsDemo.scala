package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object CardsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles.Color
    import io.udash.bootstrap.card.UdashCard
    import io.udash.bootstrap.list.UdashListGroup
    import scalatags.JsDom.all._

    val news = SeqProperty("Title 1", "Title 2", "Title 3")

    div(
      UdashCard(
        borderColor = Some(Color.Success).toProperty,
        textColor = Some(Color.Primary).toProperty,
      )(factory => Seq(
        factory.header("Card heading"),
        factory.body("Some default panel content here. Nulla vitae elit libero, " +
          "a pharetra augue. Aenean lacinia bibendum nulla sed consectetur. Aenean eu " +
          "leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Nullam " +
          "id dolor id nibh ultricies vehicula ut id elit."),
        factory.listGroup(nested => {
          val group = UdashListGroup(news)((news, nested) => li(nested(bind(news))).render)
          nested(group)
          group
        }),
        factory.footer("Card footer")
      ))
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

