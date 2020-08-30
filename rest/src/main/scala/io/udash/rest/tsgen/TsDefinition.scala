package io.udash.rest
package tsgen

import com.avsystem.commons.{MHashSet, MLinkedHashMap}

trait TsDefinition {
  def name: String
  def definition(ctx: TsGenerationCtx): String
}

final class TsGenerationCtx(
  val codecsModule: String,
  val rawModule: String
) {

  import TsGenerationCtx._

  private[this] val definitions = new MLinkedHashMap[String, Entry]
  private[this] val resolving = new MHashSet[TsDefinition]

  def resolve(definition: TsDefinition): String = {
    add(definition)
    definition.name
  }

  def add(definition: TsDefinition): Unit = if (resolving.add(definition)) {
    try {
      definitions.get(definition.name) match {
        case Some(Entry(prevDefn, prevDefnStr)) =>
          if (prevDefn != definition) {
            val definitionStr = definition.definition(this)
            if (definitionStr != prevDefnStr) {
              throw new IllegalStateException(s"duplicate TypeScript definition named ${definition.name}")
            }
            definitions += ((definition.name, Entry(definition, definitionStr)))
          }
        case None =>
          val definitionStr = definition.definition(this)
          definitions += ((definition.name, Entry(definition, definitionStr)))
      }
    } finally {
      resolving.remove(definition)
    }
  }

  def allDefinitions: String =
    definitions.valuesIterator.map(_.definitionStr).mkString("\n")
}
object TsGenerationCtx {
  private case class Entry(definition: TsDefinition, definitionStr: String)
}
