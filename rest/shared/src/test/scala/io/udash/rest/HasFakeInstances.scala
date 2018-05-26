package io.udash.rest

trait HasFakeInstances
object HasFakeInstances {
  implicit def fakeAsRealRaw[T <: HasFakeInstances]: DefaultRESTFramework.AsRealRawRPC[T] = null
  implicit def fakeMetadata[T <: HasFakeInstances]: DefaultRESTFramework.RPCMetadata[T] = null
}
