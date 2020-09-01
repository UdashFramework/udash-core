package io.udash.rest
package tsgen.other

import io.udash.rest.tsgen.{MajFriend, TsRestApiCompanion}

import scala.concurrent.Future

trait OtherApi {
  @PUT @CustomBody def echo(frjend: MajFriend): Future[MajFriend]
}
object OtherApi extends TsRestApiCompanion[OtherApi]