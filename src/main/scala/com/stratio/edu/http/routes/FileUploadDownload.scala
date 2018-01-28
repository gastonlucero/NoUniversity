package com.stratio.edu.http.routes

import java.io.File
import java.nio.file.StandardOpenOption._

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString

import scala.concurrent.ExecutionContext

trait FileUploadDownload {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  implicit val executionContext: ExecutionContext

  lazy val fileRoute: Route = pathPrefix("files") {
    fileUpload("csv") {
      case (metadata, fileByteSource) =>
        val file = new File("/tmp/test.csv")
        val concat =
          fileByteSource.via(Framing.delimiter(ByteString("\n"), 1024))
            .map(_.utf8String + "\n")
            .map(ByteString(_))
            .runWith(FileIO.toPath(file.toPath, Set(CREATE, APPEND, WRITE)))
        onSuccess(concat) { sum => {
          complete(s"File Uploaded = ${metadata.fileName}")
        }
        }
    } ~ path("download") {
      getFromFile(new File("/tmp/test.csv"), ContentType.apply(ContentTypes.`text/csv(UTF-8)`).contentType)
    }
  }
}
