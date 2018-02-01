package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path


trait SimpleRoutes {

  implicit val system: ActorSystem

  lazy val simpleRoutes: Route =
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body><b>Stratio No University!</b></body></html>"))
    } ~
      path("ping") {
        get {
          complete(StatusCodes.OK, "Pong!")
        }
      } ~
      path("ping" / Segment) {
        pathParam =>
          ??? //Complete with GET method
      } ~
      path("pingUrlParam") {
        ??? //Use parameter directive and read param 'number'
      } ~
      path("pingHeader") {
        headerValueByName("myHeader") { header =>
          complete(s"Pong with header = $header")
        }
      }

}
