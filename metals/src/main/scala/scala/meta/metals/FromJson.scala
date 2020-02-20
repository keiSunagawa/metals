package scala.meta.metals


object FromJson {
  def initializeRequestJson = io.circe.parser.parse(initializeRequest).right.get
  val initializeRequest: String =
  """ {
    |  "processId": 56114,
    |  "rootPath": "/Users/kerfume/gits/Reminder/reminder-backend",
    |  "rootUri": "file:///Users/kerfume/gits/Reminder/reminder-backend",
    |  "capabilities": {
    |    "workspace": {
    |      "applyEdit": true,
    |      "workspaceEdit": {
    |        "documentChanges": true,
    |        "resourceOperations": [
    |          "create",
    |          "rename",
    |          "delete"
    |        ]
    |      },
    |      "didChangeWatchedFiles": {
    |        "dynamicRegistration": true
    |      },
    |      "symbol": {
    |        "symbolKind": {
    |          "valueSet": [
    |            1,
    |            2,
    |            3,
    |            4,
    |            5,
    |            6,
    |            7,
    |            8,
    |            9,
    |            10,
    |            11,
    |            12,
    |            13,
    |            14,
    |            15,
    |            16,
    |            17,
    |            18,
    |            19,
    |            20,
    |            21,
    |            22,
    |            23,
    |            24,
    |            25,
    |            26
    |          ]
    |        }
    |      },
    |      "executeCommand": {
    |        "dynamicRegistration": false
    |      },
    |      "workspaceFolders": true,
    |      "configuration": true
    |    },
    |    "textDocument": {
    |      "synchronization": {
    |        "willSave": true,
    |        "willSaveWaitUntil": true,
    |        "didSave": true
    |      },
    |      "completion": {
    |        "completionItem": {
    |          "snippetSupport": true,
    |          "documentationFormat": [
    |            "markdown"
    |          ]
    |        },
    |        "contextSupport": true
    |      },
    |      "hover": {
    |        "contentFormat": [
    |          "markdown",
    |          "plaintext"
    |        ]
    |      },
    |      "signatureHelp": {
    |        "signatureInformation": {
    |          "parameterInformation": {
    |            "labelOffsetSupport": true
    |          }
    |        }
    |      },
    |      "documentSymbol": {
    |        "symbolKind": {
    |          "valueSet": [
    |            1,
    |            2,
    |            3,
    |            4,
    |            5,
    |            6,
    |            7,
    |            8,
    |            9,
    |            10,
    |            11,
    |            12,
    |            13,
    |            14,
    |            15,
    |            16,
    |            17,
    |            18,
    |            19,
    |            20,
    |            21,
    |            22,
    |            23,
    |            24,
    |            25,
    |            26
    |          ]
    |        },
    |        "hierarchicalDocumentSymbolSupport": true
    |      },
    |      "formatting": {
    |        "dynamicRegistration": true
    |      },
    |      "rangeFormatting": {
    |        "dynamicRegistration": true
    |      },
    |      "declaration": {
    |        "linkSupport": true
    |      },
    |      "definition": {
    |        "linkSupport": true
    |      },
    |      "typeDefinition": {
    |        "linkSupport": true
    |      },
    |      "implementation": {
    |        "linkSupport": true
    |      },
    |      "codeAction": {
    |        "codeActionLiteralSupport": {
    |          "codeActionKind": {
    |            "valueSet": [
    |              "",
    |              "quickfix",
    |              "refactor",
    |              "refactor.extract",
    |              "refactor.inline",
    |              "refactor.rewrite",
    |              "source",
    |              "source.organizeImports"
    |            ]
    |          }
    |        },
    |        "dynamicRegistration": true
    |      },
    |      "documentLink": {
    |        "dynamicRegistration": true
    |      },
    |      "rename": {
    |        "prepareSupport": true,
    |        "dynamicRegistration": true
    |      },
    |      "foldingRange": {
    |        "lineFoldingOnly": false,
    |        "dynamicRegistration": true
    |      },
    |      "semanticHighlightingCapabilities": {
    |        "semanticHighlighting": false
    |      },
    |      "callHierarchy": {
    |        "dynamicRegistration": false
    |      }
    |    }
    |  }
    |}
    |""".stripMargin

  val didOpen =
    """{
      |  "textDocument": {
      |    "uri": "file:///Users/kerfume/gits/Reminder/reminder-backend/server/src/main/scala/me/kerfume/reminder/server/ErrorInfo.scala",
      |    "languageId": "scala",
      |    "version": 0,
      |    "text": "package me.kerfume.reminder.server\n\nimport sttp.model.{StatusCode, Uri}\nimport sttp.tapir.{Codec, CodecFormat, EndpointOutput}\n\nsealed trait ErrorInfo\nobject ErrorInfo {\n  case class BadRequest(msg: String) extends ErrorInfo\n  object BadRequest {\n    implicit val codecPlaneText\n        : Codec[BadRequest, CodecFormat.TextPlain, String] \u003d\n      Codec.stringPlainCodecUtf8.map(BadRequest(_))(_.msg)\n  }\n  case class Redirect(uri: Uri) extends ErrorInfo\n  object Redirect {\n//    implicit val codecPlaneText\n//        : Codec[Redirect, CodecFormat.TextPlain, String] \u003d\n//      Codec.stringPlainCodecUtf8.map(\n//        s \u003d\u003e Redirect(Uri.parse(s).toOption.get) // FIXME unsafe\n//      )(_.uri.toString)\n    // FIXME: frontend: Affjax?redirect??????????????200???, affjax????????????????\n    implicit val codecPlaneText\n        : Codec[Redirect, CodecFormat.TextPlain, String] \u003d\n      Codec.stringPlainCodecUtf8.map(\n        s \u003d\u003e Redirect(Uri.parse(s).toOption.get) // FIXME unsafe\n      )(x \u003d\u003e s\"go redirect: ${x.uri.toString}\")\n  }\n\n  import sttp.tapir._\n\n  def errorInfoOutput: EndpointOutput[ErrorInfo] \u003d\n    oneOf[ErrorInfo](\n      statusMapping(StatusCode.BadRequest, plainBody[BadRequest]),\n//      statusMapping(\n//        StatusCode.MovedPermanently,\n//        header(\"Cache-Control\", \"no-cache\") and header[Redirect](\"Location\")\n//      )\n      // FIXME: frontend: Affjax?redirect??????????????200???, affjax????????????????\n      statusMapping(\n        StatusCode.Ok,\n        header(\"Cache-Control\", \"no-cache\") and plainBody[\n          Redirect\n        ]\n      )\n    )\n}\n"
      |  }
      |}
      |""".stripMargin
}
