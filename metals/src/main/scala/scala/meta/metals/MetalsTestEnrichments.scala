package scala.meta.metals

import ch.epfl.scala.bsp4j.BuildTarget
import ch.epfl.scala.bsp4j.BuildTargetCapabilities
import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.ScalacOptionsItem
import ch.epfl.scala.bsp4j.ScalacOptionsResult
import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult
import java.nio.file.Files
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.{lsp4j => l}
import scala.collection.mutable.ArrayBuffer
import scala.meta.internal.metals.WorkspaceSymbolInformation
import scala.meta.internal.metals.JdkSources
import scala.meta.internal.metals.Memory
import scala.meta.internal.metals.MetalsEnrichments._
import scala.meta.internal.metals.PositionSyntax._
import scala.meta.internal.metals.SemanticdbDefinition
import scala.meta.internal.metals.WorkspaceSymbolProvider
import scala.meta.internal.{semanticdb => s}
import scala.meta.io.Classpath
import scala.{meta => m}
import ch.epfl.scala.bsp4j.ScalaBuildTarget
import ch.epfl.scala.bsp4j.ScalaPlatform
import com.google.gson.Gson

/**
 *  Equivalent to scala.meta.internal.metals.MetalsEnrichments
 *  but only for tests
 */
object MetalsTestEnrichments {
  implicit class XtensionTestClasspath(classpath: Classpath) {
    def bytesSize: String = {
      val bytes = classpath.entries.foldLeft(0L) {
        case (a, b) =>
          a + Files.size(b.toNIO)
      }
      Memory.approx(bytes)
    }
  }

  implicit class XtensionTestLspRange(range: l.Range) {
    def formatMessage(
      severity: String,
      message: String,
      input: m.Input
    ): String = {
      try {
        val start = range.getStart
        val end = range.getEnd
        val pos = m.Position.Range(
          input,
          start.getLine,
          start.getCharacter,
          end.getLine,
          end.getCharacter
        )
        pos.formatMessage(severity, message)
      } catch {
        case e: IllegalArgumentException =>
          val result =
            s"${range.getStart.getLine}:${range.getStart.getCharacter} ${message}"
          scribe.error(result, e)
          result
      }
    }

  }
  implicit class XtensionTestDiagnostic(diag: l.Diagnostic) {
    def formatMessage(input: m.Input): String = {
      diag.getRange.formatMessage(
        diag.getSeverity.toString.toLowerCase(),
        diag.getMessage,
        input
      )
    }
  }
  implicit class XtensionMetaToken(token: m.Token) {
    def isIdentifier: Boolean = token match {
      case _: m.Token.Ident | _: m.Token.Interpolation.Id => true
      case _ => false
    }
    def toPositionParams(
      identifier: TextDocumentIdentifier
    ): TextDocumentPositionParams = {
      val range = token.pos.toLSP
      val start = range.getStart
      new TextDocumentPositionParams(identifier, start)
    }

  }

  implicit class XtensionDocumentSymbolOccurrence(info: l.SymbolInformation) {
    def fullPath: String = s"${info.getContainerName}${info.getName}"
    def toSymbolOccurrence: s.SymbolOccurrence = {
      val startRange = info.getLocation.getRange.getStart
      val endRange = info.getLocation.getRange.getEnd
      s.SymbolOccurrence(
        range = Some(
          new s.Range(
            startRange.getLine,
            startRange.getCharacter,
            startRange.getLine,
            startRange.getCharacter
          )
        ),
        // include end line for testing purposes
        symbol =
          s"${info.getContainerName}${info.getName}(${info.getKind}):${endRange.getLine + 1}",
        role = s.SymbolOccurrence.Role.DEFINITION
      )
    }
  }

}

import org.eclipse.{lsp4j => l}

object TestOrderings {
  implicit val lspRange: Ordering[l.Range] = new Ordering[l.Range] {
    override def compare(a: l.Range, b: l.Range): Int = {
      val byLine = Integer.compare(
        a.getStart.getLine,
        b.getStart.getLine
      )
      if (byLine != 0) {
        byLine
      } else {
        val byCharacter = Integer.compare(
          a.getStart.getCharacter,
          b.getStart.getCharacter
        )
        byCharacter
      }
    }
  }
}
