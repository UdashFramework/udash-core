package io.udash.rpc

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc._
import com.avsystem.commons.serialization.json.RawJson
import com.avsystem.commons.serialization.{GenCodec, HasGenCodec}
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.{ClientId, Logged}

import scala.annotation.implicitNotFound
import scala.concurrent.Future

case class JsonStr(json: String) extends AnyVal
object JsonStr {
  implicit val codec: GenCodec[JsonStr] = GenCodec.create(
    i => JsonStr(i.readCustom(RawJson).getOrElse(i.readSimple().readString())),
    (o, v) => if (!o.writeCustom(RawJson, v.json)) o.writeSimple().writeString(v.json)
  )

  implicit def futureAsReal[T](implicit asReal: AsReal[JsonStr, T]): AsReal[Future[JsonStr], Future[T]] =
    AsReal.create(_.mapNow(asReal.asReal))

  implicit def futureAsRaw[T](implicit asRaw: AsRaw[JsonStr, T]): AsRaw[Future[JsonStr], Future[T]] =
    AsRaw.create(_.mapNow(asRaw.asRaw))

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

sealed trait RpcMessage
object RpcMessage {
  implicit def codec(implicit ecr: ExceptionCodecRegistry): GenCodec[RpcMessage] =
    GenCodec.materialize
}

sealed trait RpcProtocolMessage extends RpcMessage
object RpcProtocolMessage extends HasGenCodec[RpcProtocolMessage]

case class RpcClientInit(clientId: ClientId) extends RpcProtocolMessage
object RpcClientInit extends HasGenCodec[RpcClientInit]

sealed trait RpcRequest extends RpcMessage {
  def invocation: RpcInvocation
  def gettersChain: List[RpcInvocation]
}
object RpcRequest extends HasGenCodec[RpcRequest]

/** [[RpcRequest]] which returns some value. */
case class RpcCall(invocation: RpcInvocation, gettersChain: List[RpcInvocation], callId: String) extends RpcRequest
object RpcCall extends HasGenCodec[RpcCall]

/** [[RpcRequest]] which returns Unit. */
case class RpcFire(invocation: RpcInvocation, gettersChain: List[RpcInvocation]) extends RpcRequest
object RpcFire extends HasGenCodec[RpcFire]

sealed trait RpcResponse extends RpcMessage {
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

trait RawRpc {
  @multi def get(@composite invocation: RpcInvocation): RawRpc
  @multi @verbatim def fire(@composite invocation: RpcInvocation): Unit
  @multi def call(@composite invocation: RpcInvocation): Future[JsonStr]

  final def resolveGetterChain(getterInvocations: List[RpcInvocation]): RawRpc =
    getterInvocations.foldRight[RawRpc](this)((inv, rpc) => rpc.get(inv))

  final def handleFire(rpcFire: RpcFire): Unit =
    resolveGetterChain(rpcFire.gettersChain).fire(rpcFire.invocation)

  final def handleCall(rpcCall: RpcCall): Future[JsonStr] =
    resolveGetterChain(rpcCall.gettersChain).call(rpcCall.invocation).catchFailures
}

object RawRpc extends RawRpcCompanion[RawRpc] {
  @implicitNotFound("${T} is not a valid RPC trait, " +
    "does it have a companion object that extends DefaultRpcCompanion or other similar companion base class?")
  implicit def asRealNotFound[T]: ImplicitNotFound[AsReal[RawRpc, T]] = ImplicitNotFound()

  @implicitNotFound("${T} is not a valid RPC trait, " +
    "does it have a companion object that extends DefaultRpcCompanion or other similar companion base class?")
  implicit def asRawNotFound[T]: ImplicitNotFound[AsRaw[RawRpc, T]] = ImplicitNotFound()
}

@allowIncomplete
case class RpcMetadata[T](
  @reifyName name: String,
  @multi @rpcMethodMetadata getters: Map[String, GetterMethod[_]],
  @multi @rpcMethodMetadata @annotated[Logged] loggedMethods: Map[String, LoggedMethod[_]]
)
object RpcMetadata extends RpcMetadataCompanion[RpcMetadata]

@allowIncomplete
case class GetterMethod[T](
  @infer @checked resultMetadata: RpcMetadata.Lazy[T]
) extends TypedMetadata[T]

@allowIncomplete
case class LoggedMethod[T](
  @reifyName name: String
) extends TypedMetadata[T]
