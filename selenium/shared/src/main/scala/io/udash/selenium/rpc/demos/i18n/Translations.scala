package io.udash.selenium.rpc.demos.i18n

import io.udash.i18n.TranslationKey


object Translations {
  import TranslationKey._

  object auth {
    val loginLabel = key("auth.loginLabel")
    val passwordLabel = key("auth.passwordLabel")

    object login {
      val buttonLabel = key("auth.login.buttonLabel")
      val retriesLeft = key1[Int]("auth.login.retriesLeft")
      val retriesLeftOne = key("auth.login.retriesLeftOne")
    }

    object register {
      val buttonLabel = key("auth.register.buttonLabel")
    }
  }

  object exceptions {
    val example = key("server.exception.example")
  }
}
