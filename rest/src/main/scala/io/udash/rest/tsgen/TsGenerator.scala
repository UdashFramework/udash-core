package io.udash.rest
package tsgen

import java.io.{File, FileWriter}

import com.avsystem.commons._

import scala.annotation.tailrec

trait TsReference {
  def resolve(gen: TsGenerator): String
}

trait TsDefinition extends TsReference {
  def module: TsModule
  def name: String
  def contents(gen: TsGenerator): String

  def resolve(gen: TsGenerator): String =
    gen.resolve(this)
}

final case class TsModule(path: List[String], external: Boolean = false) {
  val name: String = path.lastOpt.getOrElse("")

  override def toString: String =
    path.mkString(if (external) "" else "/", "/", "")

  def importPathFor(imported: TsModule): String =
    if (external) path.mkString("/")
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
  def apply[T](implicit tag: TsModuleTag[T]): TsModule = tag.module
}

final case class TsModuleTag[T](module: TsModule) extends AnyVal

final class TsGenerator(
  val codecsModule: String,
  val rawModule: String
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
        writer.write(
          s"""import * as $codecsModule from "udash-rest-client/lib/codecs"
             |import * as $rawModule from "udash-rest-client/lib/raw"
             |""".stripMargin
        )
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

  def resolve(definition: TsDefinition): String = {
    val importingModule = resolving.headOpt.map(_.module).filter(_ != definition.module)

    val curModule = definition.module
    val moduleEntry = modules.getOrElseUpdate(curModule, new ModuleEntry(curModule))

    if (!resolving.contains(definition)) try {
      resolving ::= definition

      moduleEntry.definitions.get(definition.name) match {
        case Some(Entry(prevDefn, prevDefnStr)) =>
          if (prevDefn != definition) {
            // TODO: this can probably blow up stack
            val definitionStr = definition.contents(this)
            if (definitionStr != prevDefnStr) {
              throw new IllegalStateException(
                s"duplicate TypeScript definition ${definition.name} in module ${definition.module}")
            }
            moduleEntry.definitions += ((definition.name, Entry(definition, definitionStr)))
          }
        case None =>
          val definitionStr = definition.contents(this)
          moduleEntry.definitions += ((definition.name, Entry(definition, definitionStr)))
      }
    } finally {
      resolving = resolving.tail
    }

    importingModule match {
      case Opt(mod) => s"${modules(mod).importModule(curModule)}.${definition.name}"
      case Opt.Empty => definition.name
    }
  }

  def write(outputDir: File): Unit =
    modules.values.foreach(_.write(outputDir))
}
