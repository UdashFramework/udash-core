package io.udash
package rest.openapi
package adjusters

import com.avsystem.commons._
import com.avsystem.commons.annotation.NotInheritedFromSealedTypes
import com.avsystem.commons.meta.infer
import com.avsystem.commons.misc.{Opt, OptArg}
import io.udash.rest.JsonValue
import com.avsystem.commons.rpc.AsRaw

import scala.annotation.StaticAnnotation

sealed trait Adjuster extends StaticAnnotation

/**
  * Base trait for annotations which may adjust [[Schema]] derived for various symbols in REST API traits.
  * Schema adjusters may be applied on:
  *
  * - Types for which [[RestStructure]] is macro materialized and [[RestSchema]] derived from it.
  * This includes all types with companion extending
  * [[io.udash.rest.RestDataCompanion RestDataCompanion]].
  *
  * - Fields of case classes for which [[RestStructure]] is macro materialized.
  *
  * - Body parameters of REST methods (parameters tagged with [[io.udash.rest.BodyField BodyField]]
  * or [[io.udash.rest.Body Body]] annotations)
  *
  * Schema adjusters DO NOT WORK on REST methods themselves and their path/header/query parameters.
  * Instead, use [[OperationAdjuster]] and [[ParameterAdjuster]].
  *
  * Also, be aware that schema adjusters may also be applied on schema references. In such cases, the schema reference
  * is wrapped into a [[Schema]] object with `allOf` property containing the original reference. This effectively
  * allows you to extend the referenced schema but you cannot inspect it in the process.
  */
trait SchemaAdjuster extends Adjuster with NotInheritedFromSealedTypes {
  def adjustSchema(schema: Schema): Schema
}
object SchemaAdjuster {
  def adjustRef(adjusters: List[SchemaAdjuster], schema: RefOr[Schema]): RefOr[Schema] =
    if (adjusters.nonEmpty)
      schema.map(s => adjusters.foldRight(s)(_ adjustSchema _))
    else schema
}

/**
  * Base trait for annotation which may adjust [[Parameter]] generated for path, query or header parameters
  * of REST RPC methods.
  */
trait ParameterAdjuster extends Adjuster {
  def adjustParameter(parameter: Parameter): Parameter
}

/**
  * A [[SchemaAdjuster]] which can also be applied on a (non-body) parameter to affect its schema.
  */
trait ParameterSchemaAdjuster extends ParameterAdjuster { this: SchemaAdjuster =>
  final def adjustParameter(parameter: Parameter): Parameter =
    parameter.schema.fold(parameter)(s => parameter.copy(schema = s.map(adjustSchema)))
}

/**
  * Base trait for annotations which may adjust [[Operation]] generated for REST HTTP methods.
  * Operation adjusters may also be specified on prefix methods - they will be applied to all operations
  * generated for the result of this prefix method.
  */
trait OperationAdjuster extends Adjuster {
  def adjustOperation(operation: Operation): Operation
}

/**
  * Base trait for annotations which may adjust [[PathItem]] generated for REST HTTP methods.
  * Path item adjusters may also be specified on prefix methods - they will be applied to all path items
  * generated for the result this prefix method.
  */
trait PathItemAdjuster extends Adjuster {
  def adjustPathItem(pathItem: PathItem): PathItem
}

/**
  * Convenience implementation of [[OperationAdjuster]] for adjusting [[RequestBody]] of an operation.
  * Request body adjuster is only be applied if [[Operation]] has [[RequestBody]] defined and it's not a
  * reference.
  */
trait RequestBodyAdjuster extends OperationAdjuster {
  final def adjustOperation(operation: Operation): Operation =
    operation.requestBody match {
      case OptArg(RefOr.Value(requestBody)) =>
        operation.copy(requestBody = RefOr(adjustRequestBody(requestBody)))
      case _ => operation
    }

  def adjustRequestBody(requestBody: RequestBody): RequestBody
}

/**
  * Convenience implementation of [[OperationAdjuster]] for adjusting [[Response]] associated with `200 OK`
  * status code (this may be changed by overriding `statusCode` method).
  * Default response adjuster is only applied if [[Responses]] contains a [[Response]] defined for
  * `200 OK` and it's not a reference.
  */
trait SuccessfulResponseAdjuster extends OperationAdjuster {
  def statusCode: Int = 200
  final def adjustOperation(operation: Operation): Operation =
    operation.responses |> { resps =>
      resps.byStatusCode.getOpt(statusCode) match {
        case Opt(RefOr.Value(resp)) =>
          operation.copy(responses = resps.copy(
            byStatusCode = resps.byStatusCode.updated(statusCode, RefOr(adjustResponse(resp)))
          ))
        case _ => operation
      }
    }

  def adjustResponse(response: Response): Response
}

/** Convenience implementation of [[SchemaAdjuster]] */
class adjustSchema(f: Schema => Schema) extends SchemaAdjuster {
  def adjustSchema(value: Schema): Schema = f(value)
}
/** Convenience implementation of [[ParameterAdjuster]] */
class adjustParameter(f: Parameter => Parameter) extends ParameterAdjuster {
  def adjustParameter(value: Parameter): Parameter = f(value)
}
/** Convenience implementation of [[OperationAdjuster]] */
class adjustOperation(f: Operation => Operation) extends OperationAdjuster {
  def adjustOperation(value: Operation): Operation = f(value)
}
/** Convenience implementation of [[PathItemAdjuster]] */
class adjustPathItem(f: PathItem => PathItem) extends PathItemAdjuster {
  def adjustPathItem(value: PathItem): PathItem = f(value)
}

/**
  * Sets the `title` of a [[Schema]]. It can be applied on standard [[SchemaAdjuster]] targets and also
  * all parameters of REST methods (not just body parameters).
  */
class title(title: String) extends SchemaAdjuster with ParameterSchemaAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(title = title)
}

/**
  * Like [[description]] but changes [[Schema]] description instead of [[Parameter]] description when applied
  * on a (non-body) parameter. It has no effect when applied on a method.
  */
class schemaDescription(desc: String) extends SchemaAdjuster with ParameterSchemaAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(description = desc)
}

/**
  * Annotation that specifies description that will be included into generated OpenAPI specification.
  * It can be applied on REST methods ([[OperationAdjuster]]), path/header/query parameters ([[ParameterAdjuster]]),
  * body parameters ([[SchemaAdjuster]]), case class fields ([[SchemaAdjuster]]) and ADTs for which [[RestStructure]]
  * is macro generated ([[SchemaAdjuster]]).
  */
class description(desc: String) extends SchemaAdjuster with ParameterAdjuster with OperationAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(description = desc)
  def adjustParameter(parameter: Parameter): Parameter = parameter.copy(description = desc)
  def adjustOperation(operation: Operation): Operation = operation.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for that method's [[RequestBody]].
  */
class bodyDescription(desc: String) extends RequestBodyAdjuster {
  def adjustRequestBody(requestBody: RequestBody): RequestBody = requestBody.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for that method's [[Response]]
  * associated with `200 OK` status code.
  */
class responseDescription(desc: String, override val statusCode: Int = 200) extends SuccessfulResponseAdjuster {
  def adjustResponse(response: Response): Response = response.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for [[PathItem]] associated
  * with that method's path.
  */
class pathDescription(desc: String) extends PathItemAdjuster {
  def adjustPathItem(pathItem: PathItem): PathItem = pathItem.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify summary for [[Operation]] generated for
  * that method.
  */
class summary(summary: String) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation = operation.copy(summary = summary)
}

/**
  * Annotation which may be applied on HTTP REST method to specify summary for [[PathItem]] associated
  * with that method's path.
  */
class pathSummary(summary: String) extends PathItemAdjuster {
  def adjustPathItem(pathItem: PathItem): PathItem = pathItem.copy(summary = summary)
}

/**
  * Adds example to [[Schema]] or [[Parameter]] object
  */
class example[+T](value: T, @infer asJson: AsRaw[JsonValue, T] = infer.value)
  extends SchemaAdjuster with ParameterAdjuster {

  def adjustSchema(schema: Schema): Schema =
    schema.copy(example = asJson.asRaw(value))
  def adjustParameter(parameter: Parameter): Parameter =
    parameter.copy(example = asJson.asRaw(value))
}

/**
  * Can be applied on REST method parameters, case class parameters and REST types themselves to include
  * `"nullable": true` property into their OpenAPI Schema.
  */
class nullable extends SchemaAdjuster with ParameterSchemaAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(nullable = true)
}

/**
  * Allows setting custom `operationId` for [[Operation]] objects generated for REST HTTP methods.
  *
  * By default, `operationId` is set to method's [[com.avsystem.commons.rpc.rpcName rpcName]] which in turn
  * defaults to method's regular name. If method is overloaded, method name may be prepended with lowercased
  * HTTP method followed by underscore (e.g. "post_")
  */
class operationId(operationId: OptArg[String] = OptArg.Empty) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation =
    operation.copy(operationId = operationId)
}

/**
  * Adds OpenAPI operation tags to an [[Operation]] object.
  */
class tags(tags: String*) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation =
    operation.copy(tags = tags.toList)
}

/**
  * Prefix methods may be annotated with this annotation to specify prefix that will be prepended to
  * `operationId` of all [[Operation]] objects generated for result of that prefix method.
  * By default, this prefix is prefix method's name with underscore,
  * so this annotation may be used in particular to set empty prefix.
  */
class operationIdPrefix(val prefix: String) extends StaticAnnotation
