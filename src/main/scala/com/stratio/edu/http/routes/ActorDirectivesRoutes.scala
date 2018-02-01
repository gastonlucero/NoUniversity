package com.stratio.edu.http.routes

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

trait ActorDirectivesRoutes {

  implicit val system: ActorSystem

  implicit val executionContext: ExecutionContext

  // Required by the `ask` (?) method
  implicit val timeoutRequest = Timeout(10 second)

  lazy val actorBackend :ActorRef = ??? //create ActorBackend

  lazy val actorRoutes: Route =
    pathSuffix("actors") {
      get {
        val actorResponse: Future[String] = for {
          response <- (actorBackend ? "Ping").mapTo[String]
        } yield (response)
        onComplete(actorResponse) {
          case Success(r) => complete(200, r)
          case Failure(f) => complete(500, f)
        }
      }
    }

}
