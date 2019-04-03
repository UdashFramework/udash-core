package io.udash
package rest.openapi
package adjusters

import com.avsystem.commons._
import com.avsystem.commons.annotation.NotInheritedFromSealedTypes
import com.avsystem.commons.meta.infer
import com.avsystem.commons.misc.{Opt, OptArg}
import com.avsystem.commons.rpc.AsRaw
import io.udash.rest.raw._

import scala.annotation.StaticAnnotation

sealed trait Adjuster extends StaticAnnotation

/**
  * Base trait for annotations which may adjust [[io.udash.rest.openapi.Schema Schema]] derived for various symbols
  * in REST API traits.
  * Schema adjusters may be applied on:
  *
  * - Types for which [[io.udash.rest.openapi.RestStructure RestStructure]] is macro materialized and
  * [[io.udash.rest.openapi.RestSchema RestSchema]] derived from it.
  * This includes all types with companion extending
  * [[io.udash.rest.RestDataCompanion RestDataCompanion]].
  *
  * - Fields of case classes for which [[io.udash.rest.openapi.RestStructure RestStructure]] is macro materialized.
  *
  * - [[io.udash.rest.Body Body]] parameters of REST methods.
  *
  * Schema adjusters DO NOT WORK on REST methods themselves and their path/header/query/cookie parameters.
  * Instead, use [[io.udash.rest.openapi.adjusters.OperationAdjuster OperationAdjuster]] and
  * [[io.udash.rest.openapi.adjusters.ParameterAdjuster ParameterAdjuster]].
  *
  * Also, be aware that schema adjusters may also be applied on schema references. In such cases, the schema reference
  * is wrapped into a [[io.udash.rest.openapi.Schema Schema]] object with `allOf` property containing the original
  * reference. This effectively allows you to extend the referenced schema but you cannot inspect it in the process.
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
  * Base trait for annotation which may adjust [[io.udash.rest.openapi.Parameter Parameter]] generated for path,
  * query or header parameters
  * of REST RPC methods.
  */
trait ParameterAdjuster extends Adjuster {
  def adjustParameter(parameter: Parameter): Parameter
}

/**
  * A [[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]] which can also be applied on a (non-body)
  * parameter to affect its schema.
  */
trait ParameterSchemaAdjuster extends ParameterAdjuster { this: SchemaAdjuster =>
  final def adjustParameter(parameter: Parameter): Parameter =
    parameter.schema.fold(parameter)(s => parameter.copy(schema = s.map(adjustSchema)))
}

/**
  * Base trait for annotations which may adjust [[io.udash.rest.openapi.Operation Operation]] generated for REST HTTP methods.
  * Operation adjusters may also be specified on prefix methods - they will be applied to all operations
  * generated for the result of this prefix method.
  */
trait OperationAdjuster extends Adjuster {
  def adjustOperation(operation: Operation): Operation
}

/**
  * Base trait for annotations which may adjust [[io.udash.rest.openapi.PathItem PathItem]] generated for REST HTTP methods.
  * Path item adjusters may also be specified on prefix methods - they will be applied to all path items
  * generated for the result this prefix method.
  */
trait PathItemAdjuster extends Adjuster {
  def adjustPathItem(pathItem: PathItem): PathItem
}

/**
  * Convenience implementation of [[io.udash.rest.openapi.adjusters.OperationAdjuster OperationAdjuster]] for adjusting
  * [[io.udash.rest.openapi.RequestBody RequestBody]] of an operation.
  * Request body adjuster is only be applied if [[io.udash.rest.openapi.Operation Operation]] has
  * [[io.udash.rest.openapi.RequestBody RequestBody]] defined and it's not a
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
  * Convenience implementation of [[io.udash.rest.openapi.adjusters.OperationAdjuster OperationAdjuster]] for adjusting
  * [[io.udash.rest.openapi.Response Response]] associated with `200 OK`
  * status code (this may be changed by overriding `statusCode` method).
  * Default response adjuster is only applied if [[io.udash.rest.openapi.Responses Responses]] contains a
  * [[io.udash.rest.openapi.Response Response]] defined for
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

/** Convenience implementation of [[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]] */
class adjustSchema(f: Schema => Schema) extends SchemaAdjuster {
  def adjustSchema(value: Schema): Schema = f(value)
}
/** Convenience implementation of [[io.udash.rest.openapi.adjusters.ParameterAdjuster ParameterAdjuster]] */
class adjustParameter(f: Parameter => Parameter) extends ParameterAdjuster {
  def adjustParameter(value: Parameter): Parameter = f(value)
}
/** Convenience implementation of [[io.udash.rest.openapi.adjusters.OperationAdjuster OperationAdjuster]] */
class adjustOperation(f: Operation => Operation) extends OperationAdjuster {
  def adjustOperation(value: Operation): Operation = f(value)
}
/** Convenience implementation of [[io.udash.rest.openapi.adjusters.PathItemAdjuster PathItemAdjuster]] */
class adjustPathItem(f: PathItem => PathItem) extends PathItemAdjuster {
  def adjustPathItem(value: PathItem): PathItem = f(value)
}

/**
  * Sets the `title` of a [[io.udash.rest.openapi.Schema Schema]]. It can be applied on standard
  * [[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]] targets and also
  * all parameters of REST methods (not just body parameters).
  */
class title(title: String) extends SchemaAdjuster with ParameterSchemaAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(title = title)
}

/**
  * Like [[io.udash.rest.openapi.adjusters.description description]] but changes [[io.udash.rest.openapi.Schema Schema]]
  * description instead of [[io.udash.rest.openapi.Parameter Parameter]] description when applied
  * on a (non-body) parameter. It has no effect when applied on a method.
  */
class schemaDescription(desc: String) extends SchemaAdjuster with ParameterSchemaAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(description = desc)
}

/**
  * Annotation that specifies description that will be included into generated OpenAPI specification.
  * It can be applied on REST methods ([[OperationAdjuster OperationAdjuster]]), path/header/query/cookie parameters
  * ([[io.udash.rest.openapi.adjusters.ParameterAdjuster ParameterAdjuster]]),
  * body parameters ([[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]]), case class fields
  * ([[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]]) and ADTs for which
  * [[io.udash.rest.openapi.RestStructure RestStructure]]
  * is macro generated ([[io.udash.rest.openapi.adjusters.SchemaAdjuster SchemaAdjuster]]).
  */
class description(desc: String) extends SchemaAdjuster with ParameterAdjuster with OperationAdjuster {
  def adjustSchema(schema: Schema): Schema = schema.copy(description = desc)
  def adjustParameter(parameter: Parameter): Parameter = parameter.copy(description = desc)
  def adjustOperation(operation: Operation): Operation = operation.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for that method's
  * [[io.udash.rest.openapi.RequestBody RequestBody]].
  */
class bodyDescription(desc: String) extends RequestBodyAdjuster {
  def adjustRequestBody(requestBody: RequestBody): RequestBody = requestBody.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for that method's
  * [[io.udash.rest.openapi.Response Response]] associated with `200 OK` status code.
  */
class responseDescription(desc: String, override val statusCode: Int = 200) extends SuccessfulResponseAdjuster {
  def adjustResponse(response: Response): Response = response.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify description for
  * [[io.udash.rest.openapi.PathItem PathItem]] associated with that method's path.
  */
class pathDescription(desc: String) extends PathItemAdjuster {
  def adjustPathItem(pathItem: PathItem): PathItem = pathItem.copy(description = desc)
}

/**
  * Annotation which may be applied on HTTP REST method to specify summary for
  * [[io.udash.rest.openapi.Operation Operation]] generated for that method.
  */
class summary(summary: String) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation = operation.copy(summary = summary)
}

/**
  * Annotation which may be applied on HTTP REST method to specify summary for
  * [[io.udash.rest.openapi.PathItem PathItem]] associated with that method's path.
  */
class pathSummary(summary: String) extends PathItemAdjuster {
  def adjustPathItem(pathItem: PathItem): PathItem = pathItem.copy(summary = summary)
}

/**
  * Adds example to [[io.udash.rest.openapi.Schema Schema]] or [[io.udash.rest.openapi.Parameter Parameter]] object
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
  * Allows setting custom `operationId` for [[io.udash.rest.openapi.Operation Operation]] objects generated
  * for REST HTTP methods.
  *
  * By default, `operationId` is set to method's `rpcName` which in turn
  * defaults to method's regular name. If method is overloaded, method name may be prepended with lowercased
  * HTTP method followed by underscore (e.g. "post_")
  */
class operationId(operationId: OptArg[String] = OptArg.Empty) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation =
    operation.copy(operationId = operationId)
}

/**
  * Adds OpenAPI operation tags to an [[io.udash.rest.openapi.Operation Operation]] object.
  */
class tags(tags: String*) extends OperationAdjuster {
  def adjustOperation(operation: Operation): Operation =
    operation.copy(tags = operation.tags ++ tags)
}

/**
  * Prefix methods may be annotated with this annotation to specify prefix that will be prepended to
  * `operationId` of all [[io.udash.rest.openapi.Operation Operation]] objects generated for result of that prefix method.
  * By default, this prefix is prefix method's name with underscore,
  * so this annotation may be used in particular to set empty prefix.
  */
class operationIdPrefix(val prefix: String) extends StaticAnnotation
