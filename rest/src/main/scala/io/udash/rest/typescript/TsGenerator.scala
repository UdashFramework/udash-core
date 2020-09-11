package io.udash.rest
package typescript

import java.io.{File, FileWriter}

import com.avsystem.commons._
import com.avsystem.commons.serialization.json.JsonStringOutput

import scala.annotation.tailrec
import scala.collection.mutable

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
  val absolutePath: String = path.mkString(if (external) "" else "/", "/", "")

  override def toString: String = absolutePath

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

  implicit val ordering: Ordering[TsModule] = Ordering.by(_.absolutePath)
}

final case class TsModuleTag[T](module: TsModule) extends AnyVal

case class TsGeneratorCtx(gen: TsGenerator, inModule: TsModule) {
  def resolve(definition: TsDefinition): String =
    gen.resolve(inModule, definition)

  def importIdentifier(module: TsModule, identifier: String): String =
    gen.importIdentifier(inModule, module, identifier)

  def codecs(identifier: String): String =
    importIdentifier(TsModule.CodecsModule, identifier)

  def raw(identifier: String): String =
    importIdentifier(TsModule.RawModule, identifier)
}

final class TsGenerator(
  // Code automatically added at the beginning of every TS file
  val prelude: String = ""
) {
  private case class Entry(definition: TsDefinition, contents: String)

  private class ModuleEntry(module: TsModule) {
    val definitions = new MLinkedHashMap[String, Entry]

    private val imports = new mutable.TreeMap[TsModule, mutable.TreeMap[String, String]]
    private val importsByName = new MHashMap[String, MHashSet[TsModule]]
    private val importsByAliasName = new MHashMap[String, MHashSet[TsModule]]

    def addDefinition(definition: TsDefinition): Unit =
      definitions.get(definition.name) match {
        case Some(Entry(prevDefn, prevDefnStr)) =>
          if (prevDefn != definition) {
            // TODO: this can probably blow up stack
            doAddDefinition(definition, Opt(prevDefnStr))
          }
        case None =>
          doAddDefinition(definition, Opt.Empty)
      }

    private def doAddDefinition(definition: TsDefinition, prevContents: Opt[String]): Unit =
      if (importsByAliasName.contains(definition.name)) {
        // trying to add definition with name that conflicts with some import
        // must regenerate the entire module
        val allDefinitions = definitions.valuesIterator.map(_.definition).toList
        definitions.clear()
        imports.clear()
        importsByName.clear()
        importsByAliasName.clear()
        addDefinition(definition) // adding this definition first so that imports added by
        allDefinitions.foreach(addDefinition)
      } else {
        importsByName.getOrElseUpdate(definition.name, new MHashSet) += definition.module
        importsByAliasName.getOrElseUpdate(definition.name, new MHashSet) += definition.module
        val contents = definition.contents(TsGeneratorCtx(TsGenerator.this, module))
        if (prevContents.exists(_ != contents)) {
          throw new IllegalStateException(
            s"duplicate TypeScript definition ${definition.name} in module $module")
        }
        definitions += ((definition.name, Entry(definition, contents)))
      }

    def importIdentifier(module: TsModule, identifier: String): String =
      imports.getOrElseUpdate(module, new mutable.TreeMap).getOrElseUpdate(identifier, {
        val conflictingModules = importsByName.getOrElseUpdate(identifier, new MHashSet)
        conflictingModules += module
        val aliasName = if (conflictingModules.size <= 1) identifier else s"${identifier}_${conflictingModules.size}"
        importsByAliasName.getOrElseUpdate(aliasName, new MHashSet) += module
        aliasName
      })

    def write(outputDir: File): Unit = {
      val moduleFile = new File(outputDir, module.path.mkString("", File.separator, ".ts"))
      moduleFile.getParentFile.mkdirs()
      val writer = new FileWriter(moduleFile)
      try {
        writer.write(prelude)
        imports.foreach { case (importedModule, identifiers) =>
          val path = module.importPathFor(importedModule)
          val importClauses = identifiers.toVector.sortBy(_._1).iterator.map {
            case (name, aliasName) if name == aliasName => name
            case (name, aliasName) => s"$name as $aliasName"
          }.mkString("{", ", ", "}")
          writer.write(s"""import $importClauses from "$path"""")
          writer.write("\n")
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
    else importIdentifier(inModule, definition.module, definition.name)
  }

  def add[Api: TsApiTypeTag](pathPrefix: Vector[String]): Unit = {
    def loop(apiType: TsApiType, pathPrefix: Vector[String]): Unit = {
      if(!apiType.empty) {
        add(apiType.definition(pathPrefix))
      }
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
      moduleEntry.addDefinition(definition)
    } finally {
      resolving = resolving.tail
    }
  }

  def importIdentifier(importingModule: TsModule, importedModule: TsModule, identifier: String): String =
    modules.getOrElseUpdate(importingModule, new ModuleEntry(importingModule)).importIdentifier(importedModule, identifier)

  /**
   * Writes out all the collected TypeScript files into the target directory.
   * Subdirectories are automatically created in order to reflect TS module structure.
   */
  def write(outputDir: File): Unit =
    modules.values.foreach(_.write(outputDir))
}
