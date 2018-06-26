package io.udash.rest

trait HasFakeInstances
object HasFakeInstances {
  implicit def fakeAsRawReal[T <: HasFakeInstances]: DefaultRESTFramework.AsRawRealRPC[T] = null
  implicit def fakeMetadata[T <: HasFakeInstances]: DefaultRESTFramework.RPCMetadata[T] = null
}
