package io.udash.rest
package typescript.other

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx, Opt}
import com.avsystem.commons.serialization.flatten
import io.udash.rest.typescript.{TsRestApiCompanion, TsRestDataCompanion, tsOptional}

import scala.concurrent.Future

final class Enumik(implicit ctx: EnumCtx) extends AbstractValueEnum
object Enumik extends AbstractValueEnumCompanion[Enumik] {
  final val This, That, Other, Whatever: Value = new Enumik
}

case class MajFriend(
  thatFriend: typescript.MajFriend
)
object MajFriend extends TsRestDataCompanion[MajFriend]

@flatten("type") sealed trait Tree
case class Branch(
  @tsOptional left: Opt[Tree],
  @tsOptional right: Opt[Tree]
) extends Tree
case object Leaf extends Tree
object Tree extends TsRestDataCompanion[Tree]

trait OtherApi {
  @PUT @CustomBody def thatEcho(frjend: MajFriend, @Query opcja: Enumik): Future[MajFriend]
  @PUT @CustomBody def echo(frjend: typescript.MajFriend, @Query opcja: Enumik): Future[typescript.MajFriend]
  @GET def gimmeTree: Future[Tree]
}
object OtherApi extends TsRestApiCompanion[OtherApi]