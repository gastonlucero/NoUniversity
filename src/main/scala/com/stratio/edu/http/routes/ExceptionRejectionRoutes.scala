package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler}
import akka.pattern.CircuitBreaker

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ExceptionRejectionRoutes {

  implicit val system: ActorSystem
  implicit val executionContext: ExecutionContext

  def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      ??? // catch the exception and complete the request
    }

  lazy val circuitBreaker = new CircuitBreaker(system.scheduler,
    maxFailures = 1,
    callTimeout = 5.seconds,
    resetTimeout = 5.second
  )

  def myRejectionHandler = RejectionHandler.newBuilder()
    .handleCircuitBreakerOpenRejection { circuit =>
      complete(HttpResponse(StatusCodes.Conflict, entity = "Numa CircuitBreaker remaining time = " + circuit.cause.remainingDuration))
    }
    .handleAll[MethodRejection] { methodRejections =>
    val names = methodRejections.map(_.supported.name)
    complete((StatusCodes.MethodNotAllowed, s"Method not supported in Numa: ${names mkString}"))
  }
    .handleNotFound {
      extractUri { uri =>
        complete((StatusCodes.NotFound, s"Method [${uri.path}] NotFound in Numa"))
      }
    }
    .result()

  lazy val exceptionRoutes = pathPrefix("myHandlers") {
    (handleRejections(myRejectionHandler) & handleExceptions(exceptionHandler)) {
      path("rejections") {
        {
          (get & parameters('number.as[Int])) { number =>
            complete(s"number = $number")
          }
        }
      } ~
        path("breaker" / Segment) { waiting =>
          val response: Future[String] = Future {
            Thread.sleep(Duration.apply(waiting).toMillis)
            "Hello circuit breaker"
          }
          onCompleteWithBreaker(circuitBreaker)(response) {
            case Success(r) => complete(200, r)
            case Failure(f) => complete(StatusCodes.InternalServerError, s"An error occurred on breaker: ${f.getMessage}")
          }
        } ~
        path("exception") {
          {
            (get & parameters('number.as[Int])) { number =>
              complete(s"Exception path ${number / 0}") //This is a RuntimeError, in this case exceptionHandler
              // catch the exception and generate the http response, complete exceptionHandler
              //Hint : Division by zero throw ArithmeticException
            }
          }
        }
    }
  }
}

