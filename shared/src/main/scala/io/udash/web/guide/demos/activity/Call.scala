package io.udash.web.guide.demos.activity

case class Call(rpcName: String, method: String, args: Seq[String]) {
  override def toString: String = s"$rpcName.$method args: ${args.mkString("[", ", ", "]")}"
}