package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.card.UdashCard
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object CardsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")

    div(
      UdashCard(
        borderColor = Some(BootstrapStyles.Color.Success).toProperty,
        textColor = Some(BootstrapStyles.Color.Primary).toProperty,
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
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

