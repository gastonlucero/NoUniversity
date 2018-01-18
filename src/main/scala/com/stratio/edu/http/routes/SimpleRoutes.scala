package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import com.stratio.edu.http.utils.ConfigComponent


trait SimpleRoutes extends ConfigComponent {

  implicit val system: ActorSystem

  //Route es a type defined = RequestContext ⇒ Future[RouteResult]
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
          get {
            complete(StatusCodes.OK, s"Pong $pathParam!")
          }
      } ~
      path("pingUrlParam") {
        parameter('number.as[Int]) {
          numberParameter => {
            complete(200 -> HttpEntity(ContentTypes.`application/json`, s"Pong $numberParameter!"))
          }
        }
      } ~
      path("pingHeader") {
        headerValueByName("myHeader") { header =>
          complete(s"Pong the header = $header")
        }
      }

}
