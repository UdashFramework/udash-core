package io.udash.web.guide.components

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.css.CssStyleName

object BootstrapUtils {

  /**
    * Wells component from bootstrap3 is absent in bootstrap4.
    * These well-like styles make elements look like the good old bootstrap3 well.
    *
    * Source: https://getbootstrap.com/docs/3.3/components/#wells
    */
  def wellStyles: Seq[CssStyleName] = Seq(
    BootstrapStyles.Card.card,
    BootstrapStyles.Card.body,
    BootstrapStyles.Background.color(Color.Light),
  )

}
