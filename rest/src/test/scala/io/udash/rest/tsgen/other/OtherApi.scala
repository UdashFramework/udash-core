package io.udash.rest
package tsgen.other

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.rest.tsgen.{MajFriend, TsRestApiCompanion}

import scala.concurrent.Future

final class Enumik(implicit ctx: EnumCtx) extends AbstractValueEnum
object Enumik extends AbstractValueEnumCompanion[Enumik] {
  final val This, That, Other, Whatever: Value = new Enumik
}

trait OtherApi {
  @PUT @CustomBody def echo(frjend: MajFriend, @Query opcja: Enumik): Future[MajFriend]
}
object OtherApi extends TsRestApiCompanion[OtherApi]