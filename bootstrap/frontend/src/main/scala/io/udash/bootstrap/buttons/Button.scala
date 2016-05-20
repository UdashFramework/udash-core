package io.udash.bootstrap.buttons

import com.karasiq.bootstrap.buttons.{ButtonBuilder, ButtonSize, ButtonStyle}

object Button {
  // Shortcut to ButtonBuilder()
  def apply(style: ButtonStyle = ButtonStyle.default, size: ButtonSize = ButtonSize.default, block: Boolean = false, active: Boolean = false, disabled: Boolean = false): ButtonBuilder =
    com.karasiq.bootstrap.buttons.Button(style, size, block, active, disabled)
}
