package io.udash.rpc

/**
  * Marker trait for all client RPC interfaces.
  * </p>
  * <ul>
  * <li>types of arguments must be serializable by uPickle library</li>
  * <li>return type must be either `Unit` or another RPC interface</li>
  * <li>method must not have type parameters</li>
  * </ul>
  */
trait ClientRPC extends RPC
