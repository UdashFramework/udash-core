package io.udash.routing

trait Routing {
  type UrlChangeProvider = io.udash.routing.UrlChangeProvider
  type WindowUrlFragmentChangeProvider = io.udash.routing.WindowUrlFragmentChangeProvider
  type WindowUrlPathChangeProvider = io.udash.routing.WindowUrlPathChangeProvider
}

