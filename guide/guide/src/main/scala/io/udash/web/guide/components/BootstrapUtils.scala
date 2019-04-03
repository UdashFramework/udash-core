package io.udash.web.guide.components

import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Color, SpacingSize}
import io.udash.css.CssStyleName

object BootstrapUtils {

  val wellStyles: Seq[CssStyleName] = Seq(
    BootstrapStyles.Border.border(),
    BootstrapStyles.Border.rounded(),
    BootstrapStyles.Background.color(Color.Light),
    BootstrapStyles.Spacing.padding(size = SpacingSize.Normal),
  )

}
