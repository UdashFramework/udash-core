package io.udash.rest
package typescript.other

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx, Opt}
import com.avsystem.commons.serialization.flatten
import io.udash.rest.typescript.{MajFriend, TsRestApiCompanion, TsRestDataCompanion, tsOptional}

import scala.concurrent.Future

final class Enumik(implicit ctx: EnumCtx) extends AbstractValueEnum
object Enumik extends AbstractValueEnumCompanion[Enumik] {
  final val This, That, Other, Whatever: Value = new Enumik
}

@flatten("type") sealed trait Tree
case class Branch(
  @tsOptional left: Opt[Tree],
  @tsOptional right: Opt[Tree]
) extends Tree
case object Leaf extends Tree
object Tree extends TsRestDataCompanion[Tree]

trait OtherApi {
  @PUT @CustomBody def echo(frjend: MajFriend, @Query opcja: Enumik): Future[MajFriend]
  @GET def gimmeTree: Future[Tree]
}
object OtherApi extends TsRestApiCompanion[OtherApi]