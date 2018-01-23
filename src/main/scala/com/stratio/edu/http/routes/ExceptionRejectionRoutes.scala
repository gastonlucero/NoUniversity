package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler}

trait ExceptionRejectionRoutes {

  implicit val system: ActorSystem

  def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex: ArithmeticException =>
        extractUri { uri =>
          println(s"Exception Handler catch the exception")
          complete(HttpResponse(StatusCodes.InternalServerError,
            entity = HttpEntity(ContentType(MediaTypes.`application/json`),
              s"Request to $uri could not be handled normally [${ex.getMessage}]")))
        }
    }

  def myRejectionHandler = RejectionHandler.newBuilder()
    .handleAll[MethodRejection] { methodRejections =>
    val names = methodRejections.map(_.supported.name)
    complete((StatusCodes.MethodNotAllowed, s"Not Supported: ${names mkString}"))
  }
    .handleNotFound {
      extractUri { uri =>
        complete((StatusCodes.NotFound, s"Method [${uri.path}] NotFound"))
      }
    }
    .result()

  lazy val exceptionRoutes =
    (handleRejections(myRejectionHandler) & handleExceptions(exceptionHandler)) {
      pathPrefix("rejections") {
        pathEnd {
          (get & parameters('number.as[Int])) { number =>
            complete(s"number = $number")
          }
        }
      } ~
        pathPrefix("exception") {
          pathEnd {
            (get & parameters('number.as[Int])) { number =>
              complete(s"Exception path ${number / 0}")
            }
          }
        }
    }
}

