package io.udash.rpc

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc._
import com.avsystem.commons.serialization.json.RawJson
import com.avsystem.commons.serialization.{GenCodec, HasGenCodec}
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.Logged

import scala.annotation.implicitNotFound
import scala.concurrent.Future

case class JsonStr(json: String) extends AnyVal
object JsonStr {
  implicit val codec: GenCodec[JsonStr] = GenCodec.create(
    i => JsonStr(i.readCustom(RawJson).getOrElse(i.readSimple().readString())),
    (o, v) => if (!o.writeCustom(RawJson, v.json)) o.writeSimple().writeString(v.json)
  )

  implicit def futureAsReal[T](implicit asReal: AsReal[JsonStr, T]): AsReal[Future[JsonStr], Future[T]] =
    _.mapNow(asReal.asReal)

  implicit def futureAsRaw[T](implicit asRaw: AsRaw[JsonStr, T]): AsRaw[Future[JsonStr], Future[T]] =
    _.mapNow(asRaw.asRaw)

  @implicitNotFound("#{forT}")
  implicit def futureAsRealNotFound[T](
    implicit forT: ImplicitNotFound[AsReal[JsonStr, T]]
  ): ImplicitNotFound[AsReal[Future[JsonStr], Future[T]]] = ImplicitNotFound()

  @implicitNotFound("#{forT}")
  implicit def futureAsRawNotFound[T](
    implicit forT: ImplicitNotFound[AsRaw[JsonStr, T]]
  ): ImplicitNotFound[AsRaw[Future[JsonStr], Future[T]]] = ImplicitNotFound()
}

case class RpcInvocation(@methodName rpcName: String, @multi args: List[JsonStr])
object RpcInvocation extends HasGenCodec[RpcInvocation]

case class RpcFailure(remoteCause: String, remoteMessage: String)
  extends Exception(s"$remoteCause: $remoteMessage")
object RpcFailure extends HasGenCodec[RpcFailure]

sealed trait RpcServerMessage
object RpcServerMessage {
  implicit def codec(implicit ecr: ExceptionCodecRegistry): GenCodec[RpcServerMessage] =
    GenCodec.materialize
}

sealed trait RpcRequest {
  def invocation: RpcInvocation
  def gettersChain: List[RpcInvocation]
}
object RpcRequest extends HasGenCodec[RpcRequest]

/** [[RpcRequest]] which returns some value. */
case class RpcCall(invocation: RpcInvocation, gettersChain: List[RpcInvocation], callId: String)
  extends RpcRequest
object RpcCall extends HasGenCodec[RpcCall]

/** [[RpcRequest]] which returns Unit. */
case class RpcFire(invocation: RpcInvocation, gettersChain: List[RpcInvocation])
  extends RpcRequest with RpcServerMessage
object RpcFire extends HasGenCodec[RpcFire]

sealed trait RpcResponse extends RpcServerMessage {
  def callId: String
}
object RpcResponse {
  implicit def codec(implicit ecr: ExceptionCodecRegistry): GenCodec[RpcResponse] =
    GenCodec.materialize
}

/** Message containing response for [[RpcCall]]. */
case class RpcResponseSuccess(response: JsonStr, callId: String) extends RpcResponse
/** Message reporting failure of [[RpcCall]]. */
case class RpcResponseFailure(cause: String, errorMsg: String, callId: String) extends RpcResponse
/** Message reporting exception from [[RpcCall]]. */
case class RpcResponseException(name: String, exception: Throwable, callId: String) extends RpcResponse
object RpcResponseException {
  implicit def codec(implicit ecr: ExceptionCodecRegistry): GenCodec[RpcResponseException] =
    GenCodec.nullableObject(
      in => {
        val name = in.getNextNamedField("name").readSimple().readString()
        val exception = ecr.get[Throwable](name).read(in.getNextNamedField("exception"))
        val callId = in.getNextNamedField("callId").readSimple().readString()
        RpcResponseException(name, exception, callId)
      },
      {
        case (out, RpcResponseException(name, exception, callId)) =>
          out.writeField("name").writeSimple().writeString(name)
          ecr.get[Throwable](name).write(out.writeField("exception"), exception)
          out.writeField("callId").writeSimple().writeString(callId)
      }
    )
}

trait RawRpc[Self <: RawRpc[Self]] { this: Self =>
  @multi def get(@composite invocation: RpcInvocation): Self
  @multi @verbatim def fire(@composite invocation: RpcInvocation): Unit

  final def resolveGetterChain(getterInvocations: List[RpcInvocation]): Self =
    getterInvocations.foldRight[Self](this)((inv, rpc) => rpc.get(inv))

  final def handleFire(rpcFire: RpcFire): Unit =
    resolveGetterChain(rpcFire.gettersChain).fire(rpcFire.invocation)
}

trait ClientRawRpc extends RawRpc[ClientRawRpc] {
  type Self = ClientRawRpc
}
object ClientRawRpc extends RawRpcCompanion[ClientRawRpc] {
  @implicitNotFound("${T} is not a valid client RPC trait, " +
    "does it have a companion object that extends DefaultClientRpcCompanion or other similar companion base class?")
  implicit def asRealNotFound[T]: ImplicitNotFound[AsReal[ClientRawRpc, T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid client RPC trait, " +
    "does it have a companion object that extends DefaultClientRpcCompanion or other similar companion base class?")
  implicit def asRawNotFound[T]: ImplicitNotFound[AsRaw[ClientRawRpc, T]] = ImplicitNotFound()
}

trait ServerRawRpc extends RawRpc[ServerRawRpc] {
  type Self = ServerRawRpc
  @multi def call(@composite invocation: RpcInvocation): Future[JsonStr]

  final def handleCall(rpcCall: RpcCall): Future[JsonStr] =
    resolveGetterChain(rpcCall.gettersChain).call(rpcCall.invocation).catchFailures
}
object ServerRawRpc extends RawRpcCompanion[ServerRawRpc] {
  @implicitNotFound("${T} is not a valid server RPC trait, " +
    "does it have a companion object that extends DefaultServerRpcCompanion or other similar companion base class?")
  implicit def asRealNotFound[T]: ImplicitNotFound[AsReal[ServerRawRpc, T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid server RPC trait, " +
    "does it have a companion object that extends DefaultServerRpcCompanion or other similar companion base class?")
  implicit def asRawNotFound[T]: ImplicitNotFound[AsRaw[ServerRawRpc, T]] = ImplicitNotFound()
}

@allowIncomplete
case class ServerRpcMetadata[T](
  @reifyName name: String,
  @multi @rpcMethodMetadata getters: Map[String, GetterMethod[_]],
  @multi @rpcMethodMetadata methods: Map[String, LoggedMethod[_]]
)
object ServerRpcMetadata extends RpcMetadataCompanion[ServerRpcMetadata]

@allowIncomplete
case class GetterMethod[T](
  @infer @checked resultMetadata: ServerRpcMetadata.Lazy[T]
) extends TypedMetadata[T]

@allowIncomplete
case class LoggedMethod[T](
  @reifyName name: String,
  @optional @reifyAnnot logged: Opt[Logged],
) extends TypedMetadata[T]
