package io.udash.web.guide.demos.activity

import com.avsystem.commons.serialization.HasGenCodec

final case class Call(rpcName: String, method: String, args: Seq[String]) {
  override def toString: String = s"$rpcName.$method args: ${args.mkString("[", ", ", "]")}"
}
object Call extends HasGenCodec[Call]