package io.udash.rpc

/**
 * Author: ghik
 * Created: 28/05/15.
 */
case class RPCFailure(remoteCause: String, remoteMessage: String) extends Exception(s"$remoteCause: $remoteMessage")
