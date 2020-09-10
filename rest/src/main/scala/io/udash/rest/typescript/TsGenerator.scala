package io.udash.rest
package typescript

import java.io.{File, FileWriter}

import com.avsystem.commons._
import com.avsystem.commons.serialization.json.JsonStringOutput

import scala.annotation.tailrec

trait TsReference {
  def resolve(gen: TsGeneratorCtx): String
}

trait TsDefinition extends TsReference {
  def module: TsModule
  def name: String
  def contents(gen: TsGeneratorCtx): String

  def resolve(gen: TsGeneratorCtx): String =
    gen.resolve(this)

  protected def quote(str: String): String = JsonStringOutput.write(str)
}

final case class TsModule(path: List[String], external: Boolean = false) {
  val name: String = path.lastOpt.getOrElse("_root_")

  override def toString: String =
    path.mkString(if (external) "" else "/", "/", "")

  /** Returns path of some other module relative to this module. */
  def importPathFor(imported: TsModule): String =
    if (imported.external) imported.path.mkString("/")
    else {
      @tailrec def loop(thisPath: List[String], importedPath: List[String]): String =
        (thisPath, importedPath) match {
          case (_ :: Nil, imported) =>
            imported.mkString("./", "/", "")
          case (_, importedName :: Nil) =>
            "../" * (thisPath.length - 1) + importedName
          case (thead :: ttail, ihead :: itail) if thead == ihead =>
            loop(ttail, itail)
          case _ =>
            val levelsToGoBack = thisPath.length - 1
            importedPath.mkString("../" * levelsToGoBack, "/", "")
        }
      loop(path, imported.path)
    }
}
object TsModule {
  /**
   * Creates a [[TsModule]] based on its _absolute_ path, i.e.
   * - if the path starts with `/` then it is interpreted as local module, relative to the output
   * directory passed to [[TsGenerator.write]]
   * - if the path does not start with `/` then it is interpreted as external module (from `node_modules`).
   */
  def fromAbsolutePath(path: String): TsModule = {
    val external = !path.startsWith("/")
    val pathSegs = path.stripPrefix("/").split('/').toList
    TsModule(pathSegs, external)
  }

  final val RawModule = fromAbsolutePath("udash-rest-client/lib/raw")
  final val CodecsModule = fromAbsolutePath("udash-rest-client/lib/codecs")
  final val ClientModule = fromAbsolutePath("udash-rest-client/lib/client")

  def of[T](implicit tag: TsModuleTag[T]): TsModule = tag.module
}

final case class TsModuleTag[T](module: TsModule) extends AnyVal

case class TsGeneratorCtx(gen: TsGenerator, inModule: TsModule) {
  def resolve(definition: TsDefinition): String = gen.resolve(inModule, definition)
  def importModule(module: TsModule): String = gen.importModule(inModule, module)

  def codecs: String = gen.importModule(inModule, TsModule.CodecsModule)
  def raw: String = gen.importModule(inModule, TsModule.RawModule)
}

final class TsGenerator(
  // Code automatically added at the beginning of every TS file
  val prelude: String = ""
) {
  private class ModuleEntry(module: TsModule) {
    val definitions = new MLinkedHashMap[String, Entry]

    private val imports = new MLinkedHashMap[TsModule, String]
    private val importsByName = new MLinkedHashMap[String, MListBuffer[TsModule]]

    def importModule(module: TsModule): String = imports.getOpt(module).getOrElse {
      val conflictingModules = importsByName.getOrElseUpdate(module.name, new MListBuffer)
      val importName = if (conflictingModules.isEmpty) module.name else s"${module.name}$$${conflictingModules.size}"
      conflictingModules += module
      imports(module) = importName
      importName
    }

    def write(outputDir: File): Unit = {
      val moduleFile = new File(outputDir, module.path.mkString("", File.separator, ".ts"))
      moduleFile.getParentFile.mkdirs()
      val writer = new FileWriter(moduleFile)
      try {
        writer.write(prelude)
        imports.foreach { case (imported, ident) =>
          val path = module.importPathFor(imported)
          writer.write(s"""import * as $ident from "$path"""")
          writer.write('\n')
        }
        definitions.values.foreach { entry =>
          writer.write("\n")
          writer.write(entry.contents)
        }
      } finally {
        writer.close()
      }
    }
  }

  private case class Entry(definition: TsDefinition, contents: String)

  private[this] val modules = new MLinkedHashMap[TsModule, ModuleEntry]
  private[this] var resolving = List.empty[TsDefinition]

  /**
   * Resolves a TypeScript definition by adding it to the .ts file determined by its module and returning
   * a string that can be used as a reference to this definition. Reference is relative to the module that this
   * definition is referred from (appropriate import is added if necessary).
   */
  def resolve(inModule: TsModule, definition: TsDefinition): String = {
    add(definition)
    if (inModule == definition.module) definition.name
    else s"${importModule(inModule, definition.module)}.${definition.name}"
  }

  def add[Api: TsApiTypeTag](pathPrefix: Vector[String]): Unit = {
    def loop(apiType: TsApiType, pathPrefix: Vector[String]): Unit = {
      add(apiType.definition(pathPrefix))
      apiType.subApis.foreach { case (morePrefix, subApi) =>
        loop(subApi, pathPrefix ++ morePrefix)
      }
    }
    loop(TsApiType[Api], pathPrefix)
  }

  def add(definition: TsDefinition): Unit = {
    val curModule = definition.module
    val moduleEntry = modules.getOrElseUpdate(curModule, new ModuleEntry(curModule))

    if (!resolving.contains(definition)) try {
      resolving ::= definition

      moduleEntry.definitions.get(definition.name) match {
        case Some(Entry(prevDefn, prevDefnStr)) =>
          if (prevDefn != definition) {
            // TODO: this can probably blow up stack
            val definitionStr = definition.contents(TsGeneratorCtx(this, curModule))
            if (definitionStr != prevDefnStr) {
              throw new IllegalStateException(
                s"duplicate TypeScript definition ${definition.name} in module ${definition.module}")
            }
            moduleEntry.definitions += ((definition.name, Entry(definition, definitionStr)))
          }
        case None =>
          val definitionStr = definition.contents(TsGeneratorCtx(this, curModule))
          moduleEntry.definitions += ((definition.name, Entry(definition, definitionStr)))
      }
    } finally {
      resolving = resolving.tail
    }
  }

  def importModule(importingModule: TsModule, importedModule: TsModule): String =
    modules.getOrElseUpdate(importingModule, new ModuleEntry(importingModule)).importModule(importedModule)

  /**
   * Writes out all the collected TypeScript files into the target directory.
   * Subdirectories are automatically created in order to reflect TS module structure.
   */
  def write(outputDir: File): Unit =
    modules.values.foreach(_.write(outputDir))
}
