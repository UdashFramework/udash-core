package io.udash.rpc

import com.avsystem.commons.annotation.AnnotationAggregate
import com.avsystem.commons.rpc.rpcName

class RPCName(name: String) extends AnnotationAggregate {
  @rpcName(name)
  type Implied
}
