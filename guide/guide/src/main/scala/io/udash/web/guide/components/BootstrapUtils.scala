package io.udash.web.guide.components

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Color, SpacingSize}
import io.udash.css.CssStyleName

object BootstrapUtils {

  /**
    * Wells component from bootstrap3 is absent in bootstrap4.
    * These well-like styles make elements look like the good old bootstrap3 well.
    *
    * Source: https://getbootstrap.com/docs/3.3/components/#wells
    */
  val wellStyles: Seq[CssStyleName] = Seq(
    BootstrapStyles.Border.border(),
    BootstrapStyles.Border.rounded(),
    BootstrapStyles.Background.color(Color.Light),
    BootstrapStyles.Spacing.padding(size = SpacingSize.Normal),
  )

}
