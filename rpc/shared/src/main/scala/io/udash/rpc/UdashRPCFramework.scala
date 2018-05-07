package io.udash.rpc

import com.avsystem.commons.rpc.{GetterRPCFramework, ProcedureRPCFramework}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization._
import com.github.ghik.silencer.silent
import io.udash.rpc.serialization.ExceptionCodecRegistry

/** Base for all RPC frameworks in Udash. */
trait UdashRPCFramework extends GetterRPCFramework with ProcedureRPCFramework with GenCodecSerializationFramework {
  override type RawValue = String
  type RawRPC <: GetterRawRPC with ProcedureRawRPC

  class ParamTypeMetadata[+T]
  implicit object ParamTypeMetadata extends ParamTypeMetadata[Nothing]

  class ResultTypeMetadata[+T]
  implicit object ResultTypeMetadata extends ResultTypeMetadata[Nothing]

  protected def rawValueCodec: GenCodec[RawValue]

  sealed trait RPCRequest {
    def invocation: RawInvocation
    def gettersChain: List[RawInvocation]
  }

  /** [[io.udash.rpc.UdashRPCFramework.RPCRequest]] which returns some value. */
  case class RPCCall(invocation: RawInvocation, gettersChain: List[RawInvocation], callId: String) extends RPCRequest

  /** [[io.udash.rpc.UdashRPCFramework.RPCRequest]] which returns Unit. */
  case class RPCFire(invocation: RawInvocation, gettersChain: List[RawInvocation]) extends RPCRequest

  sealed trait RPCResponse {
    def callId: String
  }
  /** Message containing response for [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseSuccess(response: RawValue, callId: String) extends RPCResponse
  /** Message reporting failure of [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseFailure(cause: String, errorMsg: String, callId: String) extends RPCResponse
  /** Message reporting exception from [[io.udash.rpc.UdashRPCFramework.RPCCall]]. */
  case class RPCResponseException(name: String, exception: Throwable, callId: String) extends RPCResponse
  case class RPCFailure(remoteCause: String, remoteMessage: String)
    extends Exception(s"$remoteCause: $remoteMessage")

  object RPCResponse {
    implicit def RPCResponseExceptionCodec(implicit ecr: ExceptionCodecRegistry): GenCodec[RPCResponseException] =
      GenCodec.createNullableObject(
        in => {
          val name = peekOrNextField(in, "name").readString()
          val exception = ecr.get[Throwable](name).read(peekOrNextField(in, "exception"))
          val callId = peekOrNextField(in, "callId").readString()
          RPCResponseException(name, exception, callId)
        },
        { case (out, RPCResponseException(name, exception, callId)) =>
          out.writeField("name").writeString(name)
          ecr.get[Throwable](name).write(out.writeField("exception"), exception)
          out.writeField("callId").writeString(callId)
        }
      )

    implicit def RPCResponseCodec(implicit ecr: ExceptionCodecRegistry): GenCodec[RPCResponse] =
      GenCodec.materialize[RPCResponse]
  }

  private def peekOrNextField(oi: ObjectInput, name: String): Input =
    oi.peekField(name).getOrElse {
      val fi = oi.nextField()
      if (fi.fieldName != name) {
        throw new ReadFailure(s"Expected field $name, got ${fi.fieldName}")
      }
      fi
    }

  @silent // this overrides default String codec, scalac thinks it's unused
  private implicit def implicitRawValueCodec: GenCodec[RawValue] = rawValueCodec

  /* GenCodecs for internal classes of RPC framework. */
  implicit val RawInvocationCodec: GenCodec[RawInvocation] = GenCodec.materialize
  implicit val RPCRequestCodec: GenCodec[RPCRequest] = GenCodec.materialize
  implicit val RPCFailureCodec: GenCodec[RPCFailure] = GenCodec.materialize
}
