package scala.meta.metals

import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

import org.eclipse.lsp4j.{InitializeParams, InitializedParams}
import org.eclipse.lsp4j.jsonrpc.Launcher

import scala.concurrent.ExecutionContext
import scala.meta.internal.metals.{
  Buffers,
  GlobalTrace,
  MetalsLanguageClient,
  MetalsLanguageServer,
  MetalsLogger,
  MetalsServerConfig
}
import scala.meta.io.AbsolutePath
import scala.util.control.NonFatal

object Main {
  def main(args: Array[String]): Unit = {
    val systemIn = System.in
    val systemOut = System.out
    val tracePrinter = GlobalTrace.setup("LSP")
    val exec = Executors.newCachedThreadPool()
    val ec = ExecutionContext.fromExecutorService(exec)
    val config = MetalsServerConfig.default
    println("== init")
    val server = new MetalsLanguageServer(
      ec,
      redirectSystemOut = true,
      charset = StandardCharsets.UTF_8,
      config = config
    )
//    try {
//      scribe.info(s"Starting Metals server with configuration: $config")
//      val launcher = new Launcher.Builder[MetalsLanguageClient]()
//        .traceMessages(tracePrinter)
//        .setExecutorService(exec)
//        .setInput(systemIn)
//        .setOutput(systemOut)
//        .setRemoteInterface(classOf[MetalsLanguageClient])
//        .setLocalService(server)
//        .create()
//      val clientProxy = launcher.getRemoteProxy
//      server.connectToLanguageClient(clientProxy)
//      launcher.startListening().get()
//    } catch {
//      case NonFatal(e) =>
//        e.printStackTrace(systemOut)
//        sys.exit(1)
//    } finally {
//      server.cancelAll()
//    }

    val ws = "file:///Users/kerfume/gits/Reminder/reminder-backend"
    val path = AbsolutePath(ws)
    val buffers = Buffers()
    val client = new MockClient(path, buffers)

    MetalsLogger.updateDefaultFormat()

    server.connectToLanguageClient(client)

    val initialize = CreateParam.makeInitial()
    println(initialize.getCapabilities) // FIXME null
    val res = server.fakeInitialize(initialize)

    println(res)
    val a = new InitializedParams
    server.initialized(a).get()
    //server.shutdown().get()

    println(a)
    //Thread.sleep(10000)
    val didOpen = server.didOpen(CreateParam.makeDidOpen()).get()
    println(didOpen)
    val dres = server.definition(CreateParam.makeDefinition()).get()
    println(dres)
  }

}
