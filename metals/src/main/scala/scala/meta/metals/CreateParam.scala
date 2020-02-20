package scala.meta.metals

import io.circe.Json
import org.eclipse.lsp4j._
import io.circe.optics.JsonPath._
import monocle.{Optional, Traversal}
import scala.collection.JavaConverters._

object CreateParam {
  def makeInitial(): InitializeParams = {
    val p = new InitializeParams()

    set(root.processId.int) { p.setProcessId(_) }
    set(root.rootPath.string) { p.setRootPath(_) }
    set(root.rootUri.string) { p.setRootUri(_) }
    val c = new ClientCapabilities()

    val wc = new WorkspaceClientCapabilities()
    val wcroot = root.capabilities.workspace
    set(wcroot.applyEdit.boolean) { wc.setApplyEdit(_) }
    val workspaceEdit = new WorkspaceEditCapabilities()
    set(wcroot.workspaceEdit.documentChanges.boolean) {
      workspaceEdit.setDocumentChanges(_)
    }
    set(wcroot.workspaceEdit.resourceOperations.each.string) {
      workspaceEdit.setResourceOperations(_)
    }
    // skip didChangeWatchedFiles
    wc.setWorkspaceEdit(workspaceEdit)

    val sym = new SymbolCapabilities()
    val symK = new SymbolKindCapabilities()
    set(wcroot.symbol.symbolKind.valueSet.each.int) { i =>
      symK.setValueSet(
        i.asScala.map(SymbolKind.forValue(_)).asJava
      )
    }
    sym.setSymbolKind(symK)
    wc.setSymbol(sym)
    // skip executeCommand
    set(wcroot.workspaceFolders.boolean) { wc.setWorkspaceFolders(_) }
    set(wcroot.configuration.boolean) { wc.setConfiguration(_) }

    val td = new TextDocumentClientCapabilities()
    val tdroot = root.capabilities.textDocument
    // skip synchronization
    // skip completion
    // skip hover
    // skip signatureHelp
    val symTd = new DocumentSymbolCapabilities()
    val symKTd = new SymbolKindCapabilities()
    set(tdroot.documentSymbol.symbolKind.valueSet.each.int) { i =>
      symKTd.setValueSet(
        i.asScala.map(SymbolKind.forValue(_)).asJava
      )
    }
    symTd.setSymbolKind(symKTd)
    td.setDocumentSymbol(symTd)

    // TODO

    val df = new DefinitionCapabilities()
    df.setLinkSupport(true)
    td.setDefinition(df)

    c.setWorkspace(wc)
    c.setTextDocument(td)

    p.setCapabilities(c)

    p
  }

  def makeDidOpen(): DidOpenTextDocumentParams = {
    val td = new DidOpenTextDocumentParams()
    val tdRow = new TextDocumentItem()
    tdRow.setUri(
      "file:///Users/kerfume/gits/Reminder/reminder-backend/server/src/main/scala/me/kerfume/reminder/server/ErrorInfo.scala"
    )
    tdRow.setLanguageId("scala")
    tdRow.setVersion(0)
    tdRow.setText(
      "package me.kerfume.reminder.server\n\nimport sttp.model.{StatusCode, Uri}\nimport sttp.tapir.{Codec, CodecFormat, EndpointOutput}\n\nsealed trait ErrorInfo\nobject ErrorInfo {\n  case class BadRequest(msg: String) extends ErrorInfo\n  object BadRequest {\n    implicit val codecPlaneText\n        : Codec[BadRequest, CodecFormat.TextPlain, String] \u003d\n      Codec.stringPlainCodecUtf8.map(BadRequest(_))(_.msg)\n  }\n  case class Redirect(uri: Uri) extends ErrorInfo\n  object Redirect {\n//    implicit val codecPlaneText\n//        : Codec[Redirect, CodecFormat.TextPlain, String] \u003d\n//      Codec.stringPlainCodecUtf8.map(\n//        s \u003d\u003e Redirect(Uri.parse(s).toOption.get) // FIXME unsafe\n//      )(_.uri.toString)\n    // FIXME: frontend: Affjax?redirect??????????????200???, affjax????????????????\n    implicit val codecPlaneText\n        : Codec[Redirect, CodecFormat.TextPlain, String] \u003d\n      Codec.stringPlainCodecUtf8.map(\n        s \u003d\u003e Redirect(Uri.parse(s).toOption.get) // FIXME unsafe\n      )(x \u003d\u003e s\"go redirect: ${x.uri.toString}\")\n  }\n\n  import sttp.tapir._\n\n  def errorInfoOutput: EndpointOutput[ErrorInfo] \u003d\n    oneOf[ErrorInfo](\n      statusMapping(StatusCode.BadRequest, plainBody[BadRequest]),\n//      statusMapping(\n//        StatusCode.MovedPermanently,\n//        header(\"Cache-Control\", \"no-cache\") and header[Redirect](\"Location\")\n//      )\n      // FIXME: frontend: Affjax?redirect??????????????200???, affjax????????????????\n      statusMapping(\n        StatusCode.Ok,\n        header(\"Cache-Control\", \"no-cache\") and plainBody[\n          Redirect\n        ]\n      )\n    )\n}\n"
    )
    td.setTextDocument(tdRow)
    td
  }

  def makeDefinition(): TextDocumentPositionParams = {
    val td = new TextDocumentIdentifier()
    td.setUri(
      "file:///Users/kerfume/gits/Reminder/reminder-backend/server/src/main/scala/me/kerfume/reminder/server/ErrorInfo.scala"
    )
    val pos = new Position()
    pos.setLine(6)
    pos.setCharacter(10)
    val tdp = new TextDocumentPositionParams()

    tdp.setPosition(pos)
    tdp.setTextDocument(td)
    tdp
  }

  lazy val json = FromJson.initializeRequestJson
  def set[A](path: Optional[Json, A])(in: A => Unit): Unit = {
    val a = path.getOption(json).get
    println(s"== set To $a")
    in(a)
  }
  def set[A](path: Traversal[Json, A])(in: java.util.List[A] => Unit): Unit = {
    val a = path.getAll(json).asJava
    println(s"== set To $a")
    in(a)
  }
}
