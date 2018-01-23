package com.stratio.edu.http.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.stratio.edu.http.actors.ActorBackend

import scala.concurrent.duration._

trait ActorDirectivesRoutes {

  implicit val system: ActorSystem

  // Required by the `ask` (?) method
  implicit lazy val timeoutRequest = Timeout(10 second)

  lazy val actorBackend = system.actorOf(Props[ActorBackend])

  lazy val actorRoutes: Route =
    pathPrefix("actors") {
      path("ping") {
        get {
          val actorResponse: String = (actorBackend ? "Ping").asInstanceOf[String] //With ask , the response is of type Any
          complete(200, actorResponse)
        }
      }
    }

}
