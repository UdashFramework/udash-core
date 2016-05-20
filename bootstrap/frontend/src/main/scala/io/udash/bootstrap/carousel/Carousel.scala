package io.udash.bootstrap.carousel

import com.karasiq.bootstrap.carousel.{Carousel => BoostrapCarousel}
import io.udash.bootstrap.UdashBootstrapImplicits

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

object Carousel extends UdashBootstrapImplicits {
  /*
    def reactive(data: SeqProperty[Modifier, Property[Modifier]], id: String = Bootstrap.newId): BoostrapCarousel =
      BoostrapCarousel.reactive(data, id)
  */

  def apply(content: Modifier*)(implicit ec: ExecutionContext): BoostrapCarousel =
    BoostrapCarousel.apply(content)

}