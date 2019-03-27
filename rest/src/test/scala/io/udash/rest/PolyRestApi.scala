package io.udash
package rest

trait PolyRestApi[F[_]] {
  def postThis(thing: String): F[Int]
}
object PolyRestApi extends DefaultPolyRestApiCompanion[PolyRestApi]
