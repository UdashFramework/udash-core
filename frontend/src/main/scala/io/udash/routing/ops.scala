package io.udash.routing

object /:/ {
  def unapply(path: String): Option[(String, String)] = {
    val strippedPath = path.stripSuffix("/")
    val splitIndex = strippedPath.lastIndexOf("/")
    if (splitIndex == -1)
      None
    else {
      val left = strippedPath.substring(0, splitIndex)
      val right = strippedPath.substring(splitIndex + 1, strippedPath.length)
      Some((left, right))
    }
  }

  def apply(left: String, right: String) = left + "/" + right
}

object StringRoutingOps {
  import io.udash.routing.{/:/ => op}

  implicit class StringRoutingOps(val left: String) extends AnyVal {
    def /:/(right: Any): String = op(left, right.toString)
  }
}