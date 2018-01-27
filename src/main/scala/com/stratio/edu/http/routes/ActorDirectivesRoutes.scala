package com.stratio.edu.http.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.stratio.edu.http.actors.ActorBackend

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ActorDirectivesRoutes {

  implicit val system: ActorSystem

  implicit val executionContext: ExecutionContext

  // Required by the `ask` (?) method
  implicit val timeoutRequest = Timeout(10 second)

  lazy val actorBackend = system.actorOf(Props[ActorBackend])

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
